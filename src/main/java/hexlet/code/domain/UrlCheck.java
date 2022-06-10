package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {
    @Id
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    @Lob
    private String description;
    @ManyToOne
    private Url url;
    @WhenCreated
    private Instant createdAt;

    public UrlCheck(int code, String t, String h, String desc, Url u) {
        this.statusCode = code;
        this.title = t;
        this.h1 = h;
        this.description = desc;
        this.url = u;
    }

    /**@return id*/
    public long getId() {
        return id;
    }

    /**@return statusCode*/
    public int getStatusCode() {
        return statusCode;
    }

    /**@return title*/
    public String getTitle() {
        return title;
    }

    /**@return h1*/
    public String getH1() {
        return h1;
    }

    /**@return description*/
    public String getDescription() {
        return description;
    }

    /**@return url*/
    public Url getUrl() {
        return url;
    }

    /**@return createdAt*/
    public Instant getCreatedAt() {
        return createdAt;
    }
}
