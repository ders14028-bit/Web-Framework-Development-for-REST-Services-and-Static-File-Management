package com.webframework.core;

@FunctionalInterface
public interface Route {
    String handle(Request request, Response response) throws Exception;
}
