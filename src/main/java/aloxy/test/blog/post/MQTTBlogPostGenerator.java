package aloxy.test.blog.post;

import java.time.Duration;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class MQTTBlogPostGenerator {

    @Inject
    Logger logger;

    // Just for testing purposes to feed broker with actual messages

    private static final String content = new String("Lorem ipsum dolor sit amet"
            + ", consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
            + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
            + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
            + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

    @Outgoing("blog-post-generator")
    public Multi<BlogPostMessage> generate() {

        return Multi.createFrom().ticks().every(Duration.ofSeconds(15)).map(tick -> {
            BlogPostMessage message = new BlogPostMessage(content, 1000000l, new Date());
            logger.debug("Sending message: " + message.toString());
            return message;
        });
    }

}
