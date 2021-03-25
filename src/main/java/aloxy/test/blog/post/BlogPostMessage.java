package aloxy.test.blog.post;

import java.util.Date;

public class BlogPostMessage {
    private String content;
    private Long userId;
    private Date timestamp;


    public BlogPostMessage() {
    }

    public BlogPostMessage(String content, Long userId, Date timestamp) {
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BlogPostMessage content(String content) {
        setContent(content);
        return this;
    }

    public BlogPostMessage userId(Long userId) {
        setUserId(userId);
        return this;
    }

    public BlogPostMessage timestamp(Date timestamp) {
        setTimestamp(timestamp);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " content='" + getContent() + "'" +
            ", userId='" + getUserId() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }


    public BlogPost toBlogPost() {
        return new BlogPost(content, timestamp);
    }

}
