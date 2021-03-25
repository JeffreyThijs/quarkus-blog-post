package aloxy.test.blog.post;

import java.util.Date;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class OldBlogPostRemovalService {

    @Inject
    Logger logger;

    @Inject
    EntityManager em;

    @ConfigProperty(name = "blog.post.old.posts.hourThreshold")
    Optional<Long> hourThreshold;

    public Date getDateXHoursAgo(Long hoursAgo) {
        Date date = new Date();
        return new Date(date.getTime() - hoursAgo * 60 * 60 * 1000);
    }

    public Date getDateXSecondsAgo(Long secondsAgo) {
        Date date = new Date();
        return new Date(date.getTime() - secondsAgo * 1000);
    }

    @Scheduled(every = "60s")
    @Transactional
    void removeOldBlogPosts() {

        Long xHours = hourThreshold.orElse(24l);
        Date date = getDateXHoursAgo(xHours);
        // for testing:
        // Date date = getDateXSecondsAgo(xHours);
        BlogPost.deleteAllBeforeDate(date);
        logger.debug("Deleting potential old blog posts...");
    }
}
