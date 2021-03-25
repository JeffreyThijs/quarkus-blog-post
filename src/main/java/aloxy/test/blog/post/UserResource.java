package aloxy.test.blog.post;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> getAllUsers(){
        return User.listAll();
    }

    @POST
    @Path("register")
    @Transactional
    public Response register(@Valid User user) {
        user.id = null;
        user.setConfirmed(true);
        user.persist();
        return Response.ok(user).status(201).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid User user) {
        User userDB = User.findByUsername(user.getUsername());
        if (userDB == null) {
            return Response.ok("No user found").status(404).build();
        }

        if (!userDB.checkPassword(user.getPassword())) {
            return Response.ok("Invalid credentials").status(403).build();
        }

        return Response.ok(user).status(201).build();

    }

}