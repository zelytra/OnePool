package fr.zelytra.user;

import fr.zelytra.logger.LogEndpoint;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserEndpoint {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/preferences")
    @LogEndpoint
    public Response getPreferences() {
        UserEntity user = UserEntity.find("username",securityIdentity.getPrincipal().getName()).firstResult();

        // Init first time connection
        if(user == null) {
         UserEntity newUser = new UserEntity();

        }
        return Response.ok().build();
    }
}
