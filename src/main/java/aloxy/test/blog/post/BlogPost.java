package aloxy.test.blog.post;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class BlogPost extends PanacheEntity {

    @NotBlank
    @Column(length = 1024)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date timestamp = new Date();

    public BlogPost() {
    }

    public BlogPost(String content) {
        this.content = content;
        this.timestamp = new Date();
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BlogPost content(String content) {
        setContent(content);
        return this;
    }

    @JsonProperty
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty
    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BlogPost)) {
            return false;
        }
        BlogPost blogPost = (BlogPost) o;
        return id != null && Objects.equals(content, blogPost.content);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "{" + " content='" + getContent() + "'" + ", timestamp='" + getTimestamp() + "'" + "}";
    }

    public static List<BlogPost> getBlogPostsSinceDate(Date date) {
        return find("select distinct bp FROM BlogPost bp where timestamp > ?1", date).firstResult(); 
        
    }

}