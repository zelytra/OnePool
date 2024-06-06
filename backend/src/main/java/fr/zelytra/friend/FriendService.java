package fr.zelytra.friend;

import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FriendService {

    @Inject
    UserService userService;

    /**
     * @param user
     * @return Find all the friendship of a user
     */
    public List<FriendEntity> getFriendsList(UserEntity user) {
        return FriendEntity.find("user1 = :user OR user2 = :user", Parameters.with("user", user)).list();
    }

    /**
     * @param username1
     * @param username2
     * @return Find specific friendship of two uer
     */
    public FriendEntity getFriend(String username1, String username2) {
        UserEntity user1 = userService.getUserByName(username1);
        UserEntity user2 = userService.getUserByName(username2);

        if (user1 == null || user2 == null) {
            Log.info("One of the users doesn't exist");
            return null;
        }

        return FriendEntity.find(
                "(user1 = :user1 AND user2 = :user2) OR (user1 = :user2 AND user2 = :user1)",
                Parameters.with("user1", user1).and("user2", user2)).firstResult();
    }

    /**
     * @param username
     * @return All friend UserEntity of a user
     */
    public List<UserEntity> getAllFriendUser(String username) {
        UserEntity user = userService.getUserByName(username);
        List<UserEntity> foundedUser = new ArrayList<>();

        if (user == null) {
            return foundedUser;
        }

        List<FriendEntity> friendships = getFriendsList(user);
        for (FriendEntity friend : friendships) {
            if (friend.getUser1().getAuthUsername().equalsIgnoreCase(user.getAuthUsername())) {
                foundedUser.add(friend.getUser2());
            } else {
                foundedUser.add(friend.getUser1());
            }

        }
        return foundedUser;
    }


}
