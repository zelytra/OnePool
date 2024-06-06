package fr.zelytra.friend;

import fr.zelytra.logger.LogEndpoint;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/friends")
@Authenticated
public class FriendEndpoint {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    FriendService friendService;

    @Inject
    UserService userService;

    @GET
    @Path("list")
    @LogEndpoint
    @Transactional
    public Response getFriends() {
        UserEntity user = userService.getOrCreateUserByName(securityIdentity.getPrincipal().getName());
        return Response.ok(friendService.getFriendsList(user)).build();
    }

    @DELETE
    @Path("list/{username}")
    @LogEndpoint
    @Transactional
    public Response deleteFriend(@PathParam("username") String username) {
        String receiver = securityIdentity.getPrincipal().getName();
        FriendEntity friendship = friendService.getFriend(receiver, username);

        if (friendship == null) {
            Log.info("Friendship doesn't exist");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        FriendEntity.deleteById(friendship.getId());
        return Response.ok().build();
    }

    @POST
    @Path("invite/send/{username}")
    @LogEndpoint
    @Transactional
    public Response inviteFriend(@PathParam("username") String username) {
        String requester = securityIdentity.getPrincipal().getName();
        FriendEntity friendship = friendService.getFriend(requester, username);

        if (friendship != null) {
            Log.info("Request already sent to " + username);
            return Response.ok("Request already sent to this user").build();
        }

        UserEntity user1 = userService.getOrCreateUserByName(requester);
        UserEntity user2 = userService.getUserByName(username);

        if (user2 == null) {
            Log.info("Unknown user " + username);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (user1.getAuthUsername().equalsIgnoreCase(user2.getAuthUsername())) {
            Log.info("User cannot invite him self : " + username);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        new FriendEntity(user1, user2, InviteStatus.PENDING);
        Log.info("Sending friend invitation from " + user1.getUsername() + " to " + user2.getUsername());

        return Response.ok().build();
    }

    @POST
    @Path("invite/accept/{username}")
    @LogEndpoint
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response acceptInvitation(@PathParam("username") String username) {
        String receiver = securityIdentity.getPrincipal().getName();
        FriendEntity friendship = friendService.getFriend(receiver, username);

        if (friendship == null) {
            Log.info("The friendship with " + username + " doesn't exist");
            return Response.ok("The friendship doesn't exist").build();
        }

        if (friendship.getStatus() != InviteStatus.PENDING) {
            Log.info("Friendship already accepted/refused by " + username);
            return Response.ok("Friendship already accepted/refused").build();
        }

        //Check if the user accepting is the receiver
        if (!friendship.getUser2().getAuthUsername().equalsIgnoreCase(receiver)) {
            Log.info("The user trying to accept the friend request is not allowed to do it");
            return Response.ok("The user trying to accept the friend request is not allowed to do it").build();
        }

        friendship.setStatus(InviteStatus.ACCEPT);
        return Response.ok().build();
    }
}
