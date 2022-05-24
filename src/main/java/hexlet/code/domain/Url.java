package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public class Url extends Model {
    @Id
    private long id;
    @Column(unique = true)
    private String name;
    @WhenCreated
    private Instant createdAt;
    @OneToMany
    private List<UrlCheck> urlChecks;

    public Url() { }
    public Url(String n) {
        this.name = n;
    }

    /**@return id*/
    public long getId() {
        return id;
    }

    /**@return name*/
    public String getName() {
        return name;
    }

    /**@return created_at*/
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**@return urlChecks*/
    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }

    /**@return last check date*/
    public Instant getLastCheckDate() {
        if (!urlChecks.isEmpty()) {
            return urlChecks.get(urlChecks.size() - 1).getCreatedAt();
        }
        return null;
    }

    /**@return last check status*/
    public Integer getLastCheckStatus() {
        if (!urlChecks.isEmpty()) {
            return urlChecks.get(urlChecks.size() - 1).getStatusCode();
        }
        return null;
    }
}
