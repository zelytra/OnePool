package fr.zelytra.friend;

import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class FriendEntityTest {

    @Inject
    FriendService friendService;

    @Inject
    UserService userService;

    @Test
    @Transactional
    void friendEntityCreation() {
        UserEntity user1 = userService.getOrCreateUserByName("user1");
        UserEntity user2 = userService.getOrCreateUserByName("user2");
        FriendEntity friendEntity = new FriendEntity(user1, user2, null);
        assertEquals(InviteStatus.PENDING, friendEntity.getStatus());
        assertNotNull(friendEntity.getCreatedAt());
    }

    @Test
    @Transactional
    void friendEntityCreation_withStatusInit() {
        UserEntity user1 = userService.getOrCreateUserByName("user1");
        UserEntity user2 = userService.getOrCreateUserByName("user2");
        FriendEntity friendEntity = new FriendEntity(user1, user2, InviteStatus.ACCEPT);
        assertEquals(InviteStatus.ACCEPT, friendEntity.getStatus());
        assertNotNull(friendEntity.getCreatedAt());
    }

    @AfterEach
    @Transactional
    void cleanDataBase() {
        FriendEntity.deleteAll();
        UserEntity.deleteAll();
    }
}
