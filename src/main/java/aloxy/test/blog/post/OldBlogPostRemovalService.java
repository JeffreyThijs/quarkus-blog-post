package aloxy.test.blog.post;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class OldBlogPostRemovalService {

    @Inject
    EntityManager em;

    @ConfigProperty(name = "blog.post.old.posts.hourThreshold") 
    Optional<Long> hourThreshold;

    public Date getDateXHoursAgo(Long hoursAgo) {
        Date date = new Date();
        return new Date (date.getTime() - hoursAgo*60*60*1000);
    }

    public Date getDateXSecondsAgo(Long secondsAgo) {
        Date date = new Date();
        return new Date (date.getTime() - secondsAgo*1000);
    }

    @Scheduled(every="10s")
    @Transactional  
    void removeOldBlogPosts() {

        Long xHours = hourThreshold.orElse(24l);
        Date date = getDateXHoursAgo(xHours);
        // // for testing:
        // Date date = getDateXSecondsAgo(xHours);
        List<BlogPost> oldPosts = BlogPost.listAll();
        int counter = 0;
        for(BlogPost bp : oldPosts) {
            if (bp.getTimestamp().before(date)) {
                bp.delete();
                counter++;
            }
        }

        System.out.println("Removed " + counter + " old blog posts since they were older than " + xHours + " hours!");


        // FIXME: make query that deletes all old posts
        // Query query = em.createQuery("from blogpost where timestamp < :now");
        // query.setParameter("timestamp", date, TemporalType.TIMESTAMP);
        // oldPosts = query.getResultList();
        // System.out.println("Removed " + oldPosts.size() + " old blog posts!");
    }
}
