package fr.zelytra.friend;

import fr.zelytra.logger.LogEndpoint;
import fr.zelytra.user.UserEntity;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/friends")
@Authenticated
public class FriendEndpoint {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("list")
    @LogEndpoint
    @Transactional
    public Response getFriends() {
        UserEntity user = UserEntity.findById(securityIdentity.getPrincipal().getName());

        if (user == null) {
            user = new UserEntity(securityIdentity.getPrincipal().getName());
        }

        List<FriendEntity> friendsList = FriendEntity.find(
                "user1 = :user OR user2 = :user",
                Parameters.with("user", user)
        ).list();

        return Response.ok(friendsList).build();
    }

    @DELETE
    @Path("list/{username}")
    @LogEndpoint
    public Response deleteFriend(@PathParam("username") String username) {
        return Response.ok().build();
    }

    @POST
    @Path("invite/send/{username}")
    @LogEndpoint
    public Response inviteFriend(@PathParam("username") String username) {
        UserEntity user1 = UserEntity.findById(securityIdentity.getPrincipal().getName());
        UserEntity user2 = UserEntity.findById(username);

        if (user1 == null || user2 == null) {
            Log.info("One of the users doesn't exist");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Map<String, String> params = new HashMap<>();
        params.put("user1", user1.getUsername());
        params.put("user2", user2.getUsername());

        List<FriendEntity> friendsList = FriendEntity.find("(user_id1 = :user1 AND user_id2 = :user2) OR (user_id1 = :user2 AND user_id2 = :user1)", params).list();
        if (!friendsList.isEmpty()) {
            Log.info("Request already sent");
            return Response.ok().build();
        }

        new FriendEntity(user1, user2, InviteStatus.PENDING);
        Log.info("Sending friend invitation from " + user1.getUsername() + " to " + user2.getUsername());

        return Response.ok().build();
    }

    @POST
    @Path("invite/status/{username}")
    @LogEndpoint
    public Response updateInviteStatus(@PathParam("username") String username, InviteStatus status) {
        return Response.ok().build();
    }

    @GET
    @Path("invite/list")
    @LogEndpoint
    public Response getInviteList() {
        return Response.ok().build();
    }
}
