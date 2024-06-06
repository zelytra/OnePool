package fr.zelytra.user;

import fr.zelytra.friend.FriendService;
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

    @Inject
    FriendService friendService;

    @Inject
    UserService userService;

    @GET
    @Path("/preferences")
    @LogEndpoint
    @Transactional
    public Response getPreferences() {
        UserEntity user = userService.getUserByName(securityIdentity.getPrincipal().getName());
        if (user == null) {
            user = new UserEntity(securityIdentity.getPrincipal().getName());
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/research/{username}")
    @LogEndpoint
    @Transactional
    public Response findNewFriend(@PathParam("username") String username) {
        // Find the current authenticated user
        UserEntity currentUser = userService.getUserByName(securityIdentity.getPrincipal().getName());

        // Find all users that match the username pattern, excluding the current user
        List<SimpleUser> users = UserEntity.find("authUsername LIKE :username OR username LIKE :username",
                        Parameters.with("username", username + "%"))
                .project(SimpleUser.class).list()
                .stream()
                .filter(u -> !u.authUsername().equalsIgnoreCase(currentUser.getAuthUsername()))
                .toList();

        // Find all friends of the current user
        List<UserEntity> friends = friendService.getAllFriendUser(currentUser.getAuthUsername());

        // Filter out friends from the list of users
        List<SimpleUser> filteredUsers = users.stream()
                .filter(u -> friends.stream().noneMatch(f -> f.getAuthUsername().equalsIgnoreCase(u.authUsername())))
                .toList();

        return Response.ok(filteredUsers).build();
    }
}
