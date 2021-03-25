package aloxy.test.blog.post;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> getAllUsers() {
        return User.listAll();
    }

    @POST
    @Path("register")
    @Transactional
    public Response register(@Valid User user) {

        User userDB = User.findByUsername(user.getUsername());
        if (userDB != null) {
            return Response.ok("User already exists").status(400).build();
        }

        user.id = null;
        user.setConfirmed(true);
        user.encryptPassword();
        user.persist();
        return Response.ok(user).status(201).build();
    }

    @POST
    @Path("login")
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

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            // LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code).entity(exceptionJson).build();
        }

    }

}