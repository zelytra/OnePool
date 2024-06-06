package fr.zelytra.poolpoint;

import fr.zelytra.logger.LogEndpoint;
import fr.zelytra.user.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/leaderboard")
public class LeaderboardEndpoint {

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @LogEndpoint
    @Path("/all")
    public Response getTopLeaderboard() {
        return Response.ok(userService.getUsersOrderByPoolPoint()).build();
    }

    @GET
    @LogEndpoint
    @Path("/self")
    public Response getUserLeaderboardPosition() {
        return Response.ok(userService.getUsersLeaderboardPosition(securityIdentity.getPrincipal().getName())).build();
    }
}
