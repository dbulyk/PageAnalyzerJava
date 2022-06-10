package hexlet.code.controller;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.ebean.PagedList;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
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

    public static Handler getUrlCheck() {
        return urlCheck;
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
        String nameUrl = ctx.formParam("name");
        URL fullUrl;
        final int statusUnprocessable = 422;

        try {
            fullUrl = new URL(nameUrl);
        } catch (MalformedURLException e) {
            ctx.status(statusUnprocessable);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.render("index.html");
            return;
        }

        Url duplicateUrl = new QUrl()
                .name.equalTo(nameUrl)
                .findOne();

        if (duplicateUrl != null) {
            ctx.status(statusUnprocessable);
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("index.html");
            return;
        }

        String protocol = fullUrl.getProtocol();
        String domain = fullUrl.getAuthority();
        String shortUrl = protocol + "://" + domain;

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

        List<UrlCheck> urlChecks = new QUrlCheck()
                .url.equalTo(url)
                .orderBy().id.desc()
                .findList();

        ctx.attribute("urlChecks", urlChecks);
        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };

    private static Handler urlCheck = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        HttpResponse<String> response;
        try {
            response = Unirest
                    .get(url.getName())
                    .asString();
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Страница не может быть проверена, невалидный URL.");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls/" + id);
            return;
        }

        Document body = Jsoup.parse(response.getBody());
        int statusCode = response.getStatus();
        String title = body.title();
        String description = null;
        String h1 = null;

        if (body.selectFirst("meta[name=description]") != null) {
            description = body.selectFirst("meta[name=description]").attr("content");
        }

        if (body.selectFirst("h1") != null) {
            h1 = body.selectFirst("h1").text();
        }

        UrlCheck check = new UrlCheck(statusCode, title, h1, description, url);
        check.save();

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + id);
    };
}
