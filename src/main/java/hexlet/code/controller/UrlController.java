package hexlet.code.controller;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.ebean.PagedList;

import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

public class UrlController {
    public static Handler getCreateUrl() {
        return createUrl;
    }

    public static Handler getShowUrl() {
        return showUrl;
    }

    public static Handler getListUrl() {
        return listUrl;
    }

    private static Handler listUrl = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        final int rowsPerPage = 10;

        PagedList<Url> pagedUrl = new QUrl()
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrl.getList();
        int lastPage = pagedUrl.getTotalPageCount() + 1;
        int currentPage = pagedUrl.getPageIndex() + 1;

        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .toList();

        ctx.attribute("urls", urls);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urls/index.html");
    };

    private static Handler createUrl = ctx -> {
        String name = ctx.formParam("name");

        URL fullUrl = new URL(name);
        String protocol = fullUrl.getProtocol();
        String domain = fullUrl.getAuthority();
        int port = fullUrl.getPort();

        String shortUrl = protocol + "://" + domain;
        if (port != -1) {
            shortUrl += port;
        }

        Url findUrl = new QUrl()
                .name.equalTo(shortUrl)
                .findOne();

        if (findUrl != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", findUrl);
            ctx.render("index.html");
            return;
        }

        Url url = new Url(shortUrl);
        url.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    private static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };
}
