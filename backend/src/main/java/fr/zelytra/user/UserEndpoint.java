package fr.zelytra.user;

import fr.zelytra.logger.LogEndpoint;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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
}
