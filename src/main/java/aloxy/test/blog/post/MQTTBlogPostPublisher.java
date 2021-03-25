package aloxy.test.blog.post;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class MQTTBlogPostPublisher {
    
    @Incoming("http-new-blog-post")
    @Outgoing("http-blog-post-publisher")
    public Uni<BlogPostMessage> publisNewHttpBlogPosts(BlogPostMessage post) {
        return Uni.createFrom().item(post);
    }

    @Incoming("mqtt-new-blog-post")
    @Outgoing("mqtt-blog-post-publisher")
    public Uni<BlogPostMessage> publishNewMqttBlogPosts(BlogPostMessage post) {
        return Uni.createFrom().item(post);
    }
}
