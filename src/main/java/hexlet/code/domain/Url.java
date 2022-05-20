package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Url extends Model {
    @Id
    private long id;
    private String name;
    @WhenCreated
    private Date createdAt;

    public Url() { }
    public Url(String n) {
        this.name = n;
    }

    /**@return id url*/
    public long getId() {
        return id;
    }

    /**@return name url*/
    public String getName() {
        return name;
    }

    /**@return created_at url*/
    public Date getCreatedAt() {
        return createdAt;
    }
}
