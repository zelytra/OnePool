package fr.zelytra.friend;

import fr.zelytra.user.UserEntity;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.common.mapper.TypeRef;
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
class FriendEndpointTest {

    @BeforeEach
    @Transactional
    void init(){
        UserEntity user1 = new UserEntity("user1");
        UserEntity user2 = new UserEntity("user2");
        UserEntity user3 = new UserEntity("user3");
        UserEntity user4 = new UserEntity("user4");
        FriendEntity friendship = new FriendEntity(user1, user2, InviteStatus.ACCEPT);
        FriendEntity friendship2 = new FriendEntity(user1, user3, InviteStatus.ACCEPT);
        FriendEntity friendship3 = new FriendEntity(user2, user3, InviteStatus.ACCEPT);
        FriendEntity friendship4 = new FriendEntity(user4, user3, InviteStatus.ACCEPT);
    }

    @Test
    void getFriends_code200() {
        List<FriendEntity> list = given()
                .auth().oauth2(getAccessToken("userTest", Set.of("user")))
                .when().get("/friends/list")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assert list.isEmpty();
    }

    @Test
    void getFriends() {
        List<FriendEntity> list = given()
                .auth().oauth2(getAccessToken("user1", Set.of("user")))
                .when().get("/friends/list")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertEquals(2, list.size(), "The list should contain exactly two friendships");
        assertEquals(InviteStatus.ACCEPT, list.getFirst().status);
        assertEquals("user1", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user2", list.getFirst().getUser2().getAuthUsername());
    }

    @Test
    void getFriends_ofUser1() {
        List<FriendEntity> list = given()
                .auth().oauth2(getAccessToken("user1", Set.of("user")))
                .when().get("/friends/list")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertEquals(2, list.size(), "The list should contain exactly one friendship");

        //Testing friendship 1
        assertEquals(InviteStatus.ACCEPT, list.getFirst().status);
        assertEquals("user1", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user2", list.getFirst().getUser2().getAuthUsername());

        // Testing friendship 2
        assertNotNull(list.get(1));
        assertEquals("user1", list.get(1).getUser1().getAuthUsername());
        assertEquals("user3", list.get(1).getUser2().getAuthUsername());
    }

    @Test
    void getFriends_ofUser2() {
        List<FriendEntity> list = given()
                .auth().oauth2(getAccessToken("user2", Set.of("user")))
                .when().get("/friends/list")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertEquals(2, list.size(), "The list should contain exactly one friendship");

        //Testing friendship 2
        assertEquals(InviteStatus.ACCEPT, list.getFirst().status);
        assertEquals("user1", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user2", list.getFirst().getUser2().getAuthUsername());

        //Testing friendship 3
        assertEquals(InviteStatus.ACCEPT, list.getFirst().status);
        assertEquals("user2", list.get(1).getUser1().getAuthUsername());
        assertEquals("user3", list.get(1).getUser2().getAuthUsername());
    }

    @Test
    void getFriends_ofUser4() {
        List<FriendEntity> list = given()
                .auth().oauth2(getAccessToken("user4", Set.of("user")))
                .when().get("/friends/list")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertEquals(1, list.size(), "The list should contain exactly one friendship");

        //Testing friendship 4
        assertEquals(InviteStatus.ACCEPT, list.getFirst().status);
        assertEquals("user4", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user3", list.getFirst().getUser2().getAuthUsername());
    }

    @AfterEach
    @Transactional
    void cleanDataBase() {
        FriendEntity.deleteAll();
        UserEntity.deleteAll();
    }
}
