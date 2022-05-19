package hexlet.code.controller;

import io.javalin.http.Handler;

public class RootController {
    private static Handler welcome = ctx -> ctx.render("index.html");

    public static Handler getWelcome() {
        return welcome;
    }
}
