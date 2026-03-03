package com.webframework.example;

import static com.webframework.core.MiniWebFramework.get;
import static com.webframework.core.MiniWebFramework.start;
import static com.webframework.core.MiniWebFramework.staticfiles;

public class App {
    public static void main(String[] args) throws Exception {
        staticfiles("/webroot");
        get("/App/hello", (req, res) -> {
            String name = req.getValues("name");
            if (name == null || name.isBlank()) {
                return "hello world";
            }
            return "hello " + name;
        });
        get("/App/pi", (req, res) -> String.valueOf(Math.PI));
        start(8080);
    }
}
