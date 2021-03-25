package aloxy.test.blog.post;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogPostResource {

    // In real world applications -> secure endpoints with cookie/bearer token, etc

    @Inject
    @Channel("http-new-blog-post")
    Emitter<BlogPostMessage> emitter;

    @GET
    public List<BlogPost> getAllBlogPosts() {
        return BlogPost.listAll();
    }

    @GET
    @Path("{userId}")
    public List<BlogPost> getAllUserBlogPosts(@QueryParam("userId") Long userId) {
        User user = User.findById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getBlogPosts();
    }

    @POST
    @Path("{userId}")
    @Transactional
    public Response addBlogPost(@QueryParam("userId") Long userId, @Valid BlogPost post) {
        User user = User.findById(userId);
        if (user == null) {
            return Response.ok("User with id: " + userId + " not found!").status(404).build();
        }
        post.id = null;
        user.addBlogPost(post);
        user.persist();
        emitter.send(new BlogPostMessage(post.getContent(), user.id, post.getTimestamp()));
        return Response.ok(post).status(201).build();
    }

}
