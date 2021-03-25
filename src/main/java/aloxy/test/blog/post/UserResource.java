package aloxy.test.blog.post;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jboss.logging.Logger;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    Logger logger;

    @Inject
    Mailer mailer;

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
        user.setConfirmed(false);
        user.generateConfirmationCode();
        user.encryptPassword();
        user.persist();

        // send mail
        // FIXME: static url
        String confirmationLink = "http://localhost:8080/users/confirm?userId=" + user.id + "&code="
                + user.getConfirmationCode();
        String confirmationMailBody = "Confirm email here: " + confirmationLink;
        // mailer.send(Mail.withText(user.getEmail(), "Confirmation email",
        // confirmationMailBody);
        logger.info(confirmationMailBody);

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

    @GET
    @Path("confirm")
    public Response confirmEmail(@QueryParam("userId") Long userId, @QueryParam("code") String confirmationCode) {

        User user = User.findById(userId);
        if (!user.getConfirmationCode().equals(confirmationCode)) {
            return Response.ok("Invalid code").status(401).build();
        }

        user.setConfirmed(true);
        logger.info("User with email: " + user.getEmail() + " has been confirmed!");

        return Response.ok().status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Inject
        Logger logger;

        @Override
        public Response toResponse(Exception exception) {
            logger.error("Failed to handle request", exception);

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