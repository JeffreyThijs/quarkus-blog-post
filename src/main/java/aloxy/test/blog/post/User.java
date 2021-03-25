package aloxy.test.blog.post;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jboss.logging.Logger;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "known_users")
public class User extends PanacheEntity {

    @Inject
    Logger logger;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private boolean confirmed;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BlogPost> blogPosts = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, String email, boolean confirmed) {
        this.username = username;
        // this.password = BcryptUtil.bcryptHash(password);
        this.password = password;
        this.email = email;
        this.confirmed = confirmed;
    }

    public User(String username, String password, String email, boolean confirmed, List<BlogPost> posts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.confirmed = confirmed;
        this.blogPosts = posts;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }

    public boolean getConfirmed() {
        return this.confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User username(String username) {
        setUsername(username);
        return this;
    }

    public User email(String email) {
        setEmail(email);
        return this;
    }

    public User confirmed(boolean confirmed) {
        setConfirmed(confirmed);
        return this;
    }

    public List<BlogPost> getBlogPosts() {
        return this.blogPosts;
    }

    public void setBlogPosts(List<BlogPost> posts) {
        this.blogPosts = posts;
    }

    @Override
    public String toString() {
        return "{" + " username='" + getUsername() + "'" + ", password='" + getPassword() + "'" + ", email='"
                + getEmail() + "'" + ", confirmed='" + isConfirmed() + "'" + "}";
    }

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public boolean checkPassword(String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), this.password).verified;
    }

    public void encryptPassword() {
        String unencryptedPassword = this.password;
        this.password = BCrypt.withDefaults().hashToString(12, unencryptedPassword.toCharArray());
        if(!checkPassword(unencryptedPassword)){
            // FIXME: throw a good error here
            logger.error("something went wrong");
        }
    }

    public void addBlogPost(BlogPost data) {
        this.blogPosts.add(data);
        data.setUser(this);
    }

    public void removeBlogPost(BlogPost data) {
        this.blogPosts.remove(data);
        data.setUser(null);
    }
}