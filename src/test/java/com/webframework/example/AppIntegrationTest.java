package com.webframework.example;

import static com.webframework.core.MiniWebFramework.get;
import static com.webframework.core.MiniWebFramework.start;
import static com.webframework.core.MiniWebFramework.staticfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AppIntegrationTest {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static int port;

    @BeforeAll
    static void startServer() throws Exception {
        port = findFreePort();

        staticfiles("/webroot");
        get("/App/hello", (req, res) -> {
            String name = req.getValues("name");
            if (name == null || name.isBlank()) {
                return "hello world";
            }
            return "hello " + name;
        });
        get("/App/pi", (req, res) -> String.valueOf(Math.PI));

        Thread serverThread = new Thread(() -> {
            try {
                start(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        waitUntilServerResponds();
    }

    @Test
    void helloWithoutNameReturnsDefaultMessage() throws Exception {
        HttpResponse<String> response = sendGet("/App/hello");

        assertEquals(200, response.statusCode());
        assertEquals("hello world", response.body());
    }

    @Test
    void helloWithNameReturnsPersonalizedMessage() throws Exception {
        HttpResponse<String> response = sendGet("/App/hello?name=Pedro");

        assertEquals(200, response.statusCode());
        assertEquals("hello Pedro", response.body());
    }

    @Test
    void piEndpointReturnsPiValue() throws Exception {
        HttpResponse<String> response = sendGet("/App/pi");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().startsWith("3.14"));
    }

    @Test
    void staticIndexIsServed() throws Exception {
        HttpResponse<String> response = sendGet("/index.html");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().toLowerCase().contains("<html"));
    }

    private static HttpResponse<String> sendGet(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void waitUntilServerResponds() throws Exception {
        int retries = 30;
        while (retries-- > 0) {
            try {
                HttpResponse<String> response = sendGet("/App/pi");
                if (response.statusCode() == 200) {
                    return;
                }
            } catch (Exception ignored) {
            }
            Thread.sleep(100);
        }
        throw new IllegalStateException("Server did not start in time");
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
