package hexlet.code.controller;

import io.javalin.http.Handler;

public class RootController {
    private static Handler url = ctx -> ctx.render("index.html");

    public static Handler getUrl() {
        return url;
    }
}
