package hexlet.code.controller;

import io.javalin.http.Handler;

public class RootController {
    public static final Handler URL = ctx -> ctx.render("index.html");

}
