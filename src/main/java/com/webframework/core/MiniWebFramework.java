package com.webframework.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MiniWebFramework {
    private static final Map<String, Route> getRoutes = new HashMap<>();
    private static String staticFilesRoot = "";

    private MiniWebFramework() {
    }

    public static void get(String path, Route route) {
        if (path == null || !path.startsWith("/")) {
            throw new IllegalArgumentException("Route path must start with '/'");
        }
        getRoutes.put(path, route);
    }

    public static void staticfiles(String folder) {
        if (folder == null || folder.isBlank()) {
            throw new IllegalArgumentException("Static files folder must not be empty");
        }
        staticFilesRoot = normalizeFolder(folder);
    }

    public static void start(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                handle(serverSocket.accept());
            }
        }
    }

    private static void handle(Socket clientSocket) {
        BufferedReader in = null;
        OutputStream out = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = clientSocket.getOutputStream();

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                sendText(out, 400, "text/plain; charset=UTF-8", "Bad Request");
                return;
            }

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 3) {
                sendText(out, 400, "text/plain; charset=UTF-8", "Bad Request");
                return;
            }

            String method = tokens[0];
            String uriText = tokens[1];
            String protocol = tokens[2];

            if (!"GET".equals(method)) {
                sendText(out, 405, "text/plain; charset=UTF-8", "Method Not Allowed");
                return;
            }

            URI uri = new URI(uriText);
            String path = uri.getPath();
            Map<String, String> queryValues = parseQueryValues(uri.getQuery());

            Route route = getRoutes.get(path);
            if (route != null) {
                Request request = new Request(method, path, protocol, queryValues);
                Response response = new Response();
                String body = route.handle(request, response);
                sendText(out, response.getStatusCode(), response.getContentType(), body == null ? "" : body);
                return;
            }

            if (trySendStaticFile(path, out)) {
                return;
            }

            sendText(out, 404, "text/plain; charset=UTF-8", "Not Found");
        } catch (Exception e) {
            if (out != null) {
                try {
                    sendText(out, 500, "text/plain; charset=UTF-8", "Internal Server Error");
                } catch (IOException ignored) {
                }
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void sendText(OutputStream out, int status, String contentType, String body) throws IOException {
        byte[] payload = body.getBytes(StandardCharsets.UTF_8);
        sendBytes(out, status, contentType, payload);
    }

    private static void sendBytes(OutputStream out, int status, String contentType, byte[] body) throws IOException {
        String statusText = statusText(status);

        String headers = "HTTP/1.1 " + status + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private static boolean trySendStaticFile(String path, OutputStream out) throws IOException {
        if (staticFilesRoot == null || staticFilesRoot.isBlank()) {
            return false;
        }

        String staticPath = path;
        if (staticPath == null || staticPath.isBlank() || "/".equals(staticPath)) {
            staticPath = "/index.html";
        }

        if (staticPath.contains("..")) {
            sendText(out, 403, "text/plain; charset=UTF-8", "Forbidden");
            return true;
        }

        String resourcePath = staticFilesRoot + (staticPath.startsWith("/") ? staticPath : "/" + staticPath);
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(resourcePath);
        if (resourceStream == null) {
            return false;
        }

        try (resourceStream) {
            byte[] data = resourceStream.readAllBytes();
            sendBytes(out, 200, contentTypeFor(staticPath), data);
            return true;
        }
    }

    private static String contentTypeFor(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (lower.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "text/plain; charset=UTF-8";
    }

    private static String normalizeFolder(String folder) {
        String normalized = folder.trim().replace("\\", "/");
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static Map<String, String> parseQueryValues(String rawQuery) {
        Map<String, String> values = new HashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) {
            return values;
        }

        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            if (pair.isBlank()) {
                continue;
            }

            String[] keyValue = pair.split("=", 2);
            String key = decode(keyValue[0]);
            String value = keyValue.length > 1 ? decode(keyValue[1]) : "";
            values.put(key, value);
        }

        return values;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String statusText(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }
}
