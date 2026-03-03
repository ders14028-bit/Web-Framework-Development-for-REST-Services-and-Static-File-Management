package com.webframework.core;

import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, String> queryValues;

    public Request(String method, String path, String protocol, Map<String, String> queryValues) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.queryValues = queryValues;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getValues(String key) {
        return queryValues.getOrDefault(key, "");
    }
}
