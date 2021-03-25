package aloxy.test.blog.post;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
public class MQTTBlogPostPersistService {

    @Inject
    Logger logger;

    @Inject
    ObjectMapper om;

    @Inject @Channel("mqtt-new-blog-post")
    Emitter<BlogPostMessage> emitter;

    @Incoming("blog-post-persist-service")
    @Blocking
    @Transactional
    public void persist(byte[] blogPostMessageRaw) {

        logger.debug("received: " + new String(blogPostMessageRaw));
        try {
            BlogPostMessage message = om.readValue(new String(blogPostMessageRaw), BlogPostMessage.class);
            User user = User.findById(message.getUserId());
            BlogPost post = message.toBlogPost();
            user.addBlogPost(post);
            user.persist();
            emitter.send(message);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
