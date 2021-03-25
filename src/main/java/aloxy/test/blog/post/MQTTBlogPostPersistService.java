package aloxy.test.blog.post;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
public class MQTTBlogPostPersistService {

    @Inject
    ObjectMapper om;

    @Incoming("blog-post-persist-service")
    @Blocking
    @Transactional
    public void persist(byte[] blogPostMessageRaw) {

        System.out.println("received: " + new String(blogPostMessageRaw));
        try {
            BlogPostMessage message = om.readValue(new String(blogPostMessageRaw), BlogPostMessage.class);
            User user = User.findById(message.getUserId());
            BlogPost post = message.toBlogPost();
            user.addBlogPost(post);
            user.persist();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
