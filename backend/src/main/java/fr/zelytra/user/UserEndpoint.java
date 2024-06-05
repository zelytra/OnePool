package fr.zelytra.user;

import fr.zelytra.logger.LogEndpoint;
import fr.zelytra.user.reflections.SimpleUser;
import io.quarkus.panache.common.Parameters;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/user")
@Authenticated
public class UserEndpoint {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/preferences")
    @LogEndpoint
    @Transactional
    public Response getPreferences() {
        UserEntity user = UserEntity.find("username", securityIdentity.getPrincipal().getName()).firstResult();
        if (user == null) {
            user = new UserEntity(securityIdentity.getPrincipal().getName());
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/research/{username}")
    @LogEndpoint
    @Transactional
    public Response findSimpleUser(@PathParam("username") String username) {
        List<SimpleUser> users = UserEntity.find("authUsername LIKE :username OR username LIKE :username", Parameters.with("username", username + "%")).project(SimpleUser.class).list();
        List<SimpleUser> filteredList = users.stream().filter(u -> !u.authUsername().equalsIgnoreCase(securityIdentity.getPrincipal().getName())).toList();
        return Response.ok(filteredList).build();
    }
}
