package hexlet.code;

import hexlet.code.domain.query.QUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;
import io.ebean.DB;
import io.ebean.Transaction;

import hexlet.code.domain.Url;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url existingUrl;
    private static Transaction transaction;
    private final int responseSuccess = 200;
    private final int responseFound = 302;
    private static MockWebServer mockWebServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;

        existingUrl = new Url("https://ru.hexlet.io");
        existingUrl.save();

        mockWebServer = new MockWebServer();

        String expected = Files.readString(Paths.get("src", "test", "resources", "expected", "mock"));
        mockWebServer.enqueue(new MockResponse().setBody(expected));
        mockWebServer.start();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @Nested
    class RootTest {
        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(responseSuccess);
            assertThat(response.getBody()).contains("Анализатор страниц");
        }
    }

    @Nested
    class UrlTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseSuccess);
            assertThat(body).contains(existingUrl.getName());
        }

        @Test
        void testShow() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/" + existingUrl.getId())
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseSuccess);
            assertThat(body).contains(existingUrl.getName());
        }

        @Test
        void testCreate() {
            String inputName = "https://www.example.com";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("name", inputName)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(responseFound);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseSuccess);
            assertThat(body).contains("Страница успешно добавлена");

            Url actualUrl = new QUrl()
                    .name.equalTo(inputName)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(inputName);
        }

        @Test
        void testCheckUrl() {
            String mockDescription = "Живое онлайн сообщество программистов и разрабо";
            String mockTitle = "Хекслет — больше ";
            String mockH1 = "Онлайн-школа прог";

            HttpResponse<String> response = Unirest
                    .post(baseUrl + "/urls/" + existingUrl.getId() + "/checks")
                    .asString();

            assertThat(response.getStatus()).isEqualTo(responseFound);
            assertThat(response.getHeaders().getFirst("Location")).isEqualTo("/urls/" + existingUrl.getId());

            String body = Unirest
                    .get(baseUrl + "/urls/" + existingUrl.getId())
                    .asString()
                    .getBody();

            assertThat(body).contains("200");
            assertThat(body).contains(mockDescription);
            assertThat(body).contains(mockH1);
            assertThat(body).contains(mockTitle);
        }
    }
}
