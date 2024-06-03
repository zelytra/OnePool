package fr.zelytra.friend;

import fr.zelytra.logger.LogEndpoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/friends")
public class FriendEndpoint {

    @GET
    @Path("list")
    @LogEndpoint
    public Response getFriends() {
        return Response.ok().build();
    }

    @POST
    @Path("invite/send/{username}")
    @LogEndpoint
    public Response inviteFriend(@PathParam("username") String username) {
        return Response.ok().build();
    }

    @POST
    @Path("invite/status/{username}")
    @LogEndpoint
    public Response updateInviteStatus(@PathParam("username") String username, InviteStatus status) {
        return Response.ok().build();
    }
}
