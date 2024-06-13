package fr.zelytra.user;

import fr.zelytra.friend.FriendEntity;
import fr.zelytra.friend.InviteStatus;
import fr.zelytra.user.reflections.SimpleUser;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static io.quarkus.test.oidc.server.OidcWiremockTestResource.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class UserEndpointTest {

    @Inject
    UserService userService;

    @BeforeEach
    @Transactional
    void init() {
        UserEntity user1 = userService.getOrCreateUserByName("user1");
        UserEntity user2 = userService.getOrCreateUserByName("user2");
        UserEntity user3 = userService.getOrCreateUserByName("user3");
        UserEntity user4 = userService.getOrCreateUserByName("user4");
        UserEntity user5 = userService.getOrCreateUserByName("user5");
        new FriendEntity(user1, user2, InviteStatus.ACCEPT);
        new FriendEntity(user1, user3, InviteStatus.ACCEPT);
        new FriendEntity(user2, user3, InviteStatus.ACCEPT);
        new FriendEntity(user4, user3, InviteStatus.PENDING);
    }

    @Test
    void getPreferences_generateUnknownUser() {
        UserEntity user = given()
                .auth().oauth2(getAccessToken("userTest", Set.of("user")))
                .when().get("/user/preferences")
                .then().statusCode(200)
                .extract()
                .body()
                .as(UserEntity.class);
        assertNotNull(user);
        UserEntity foundedUser = userService.getUserByName("userTest");
        assertNotNull(foundedUser);
        assertEquals(foundedUser.getUsername(), "userTest", "The user found didn't match the input user");
    }

    @Test
    void findNewFriend_returnAllUsers() {
        List<SimpleUser> users = given()
                .auth().oauth2(getAccessToken("user5", Set.of("user")))
                .when().get("/user/research/user")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertNotNull(users);
        assertEquals(4, users.size(), "The user it self should not be returned");
    }

    @Test
    void findNewFriend_returnUserWithoutFriendship() {
        List<SimpleUser> users = given()
                .auth().oauth2(getAccessToken("user1", Set.of("user")))
                .when().get("/user/research/user")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertNotNull(users);
        assertEquals(2, users.size(), "The user it self should not be returned");
        assertEquals("user4", users.getFirst().authUsername());
        assertEquals("user5", users.get(1).authUsername());
    }

    @AfterEach
    @Transactional
    void cleanDataBase() {
        FriendEntity.deleteAll();
        UserEntity.deleteAll();
    }
}
