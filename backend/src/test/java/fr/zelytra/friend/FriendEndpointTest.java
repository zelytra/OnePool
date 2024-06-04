package fr.zelytra.friend;

import fr.zelytra.user.UserEntity;
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
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class FriendEndpointTest {

    @Inject
    FriendService friendService;


    @BeforeEach
    @Transactional
    void init() {
        UserEntity user1 = new UserEntity("user1");
        UserEntity user2 = new UserEntity("user2");
        UserEntity user3 = new UserEntity("user3");
        UserEntity user4 = new UserEntity("user4");
        UserEntity user5 = new UserEntity("user5");
        new FriendEntity(user1, user2, InviteStatus.ACCEPT);
        new FriendEntity(user1, user3, InviteStatus.ACCEPT);
        new FriendEntity(user2, user3, InviteStatus.ACCEPT);
        new FriendEntity(user4, user3, InviteStatus.PENDING);
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
        assertEquals(InviteStatus.ACCEPT, list.getFirst().getStatus());
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
        assertEquals(InviteStatus.ACCEPT, list.getFirst().getStatus());
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
        assertEquals(InviteStatus.ACCEPT, list.getFirst().getStatus());
        assertEquals("user1", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user2", list.getFirst().getUser2().getAuthUsername());

        //Testing friendship 3
        assertEquals(InviteStatus.ACCEPT, list.getFirst().getStatus());
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
        assertEquals(InviteStatus.PENDING, list.getFirst().getStatus());
        assertEquals("user4", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user3", list.getFirst().getUser2().getAuthUsername());
    }

    @Test
    void inviteFriend() {
        given()
                .auth().oauth2(getAccessToken("user5", Set.of("user")))
                .when().post("friends/invite/send/user1")
                .then().statusCode(200);

        List<FriendEntity> list = given()
                .auth().oauth2(getAccessToken("user5", Set.of("user")))
                .when().get("/friends/list")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(1, list.size(), "The list should contain exactly one friendship");
        assertEquals(InviteStatus.PENDING, list.getFirst().getStatus());
        assertEquals("user5", list.getFirst().getUser1().getAuthUsername());
        assertEquals("user1", list.getFirst().getUser2().getAuthUsername());

    }

    @Test
    void inviteFriend_unknownUser() {
        given()
                .auth().oauth2(getAccessToken("user5", Set.of("user")))
                .when().post("friends/invite/send/unknownUser")
                .then().statusCode(400);
    }

    @Test
    void inviteFriend_inviteAlreadySent() {
        given()
                .auth().oauth2(getAccessToken("user1", Set.of("user")))
                .when().post("friends/invite/send/user2")
                .then().statusCode(200)
                .body(is("Request already sent to this user"));
    }

    @Test
    void acceptInvite() {
        given()
                .auth().oauth2(getAccessToken("user3", Set.of("user")))
                .when().post("friends/invite/accept/user4")
                .then().statusCode(200);

        FriendEntity friendship = friendService.getFriend("user3", "user4");
        assertNotNull(friendship);
        assertEquals(InviteStatus.ACCEPT, friendship.getStatus());
    }

    @Test
    void acceptInvite_requesterTryingToAcceptItSelfTheInvite() {
        given()
                .auth().oauth2(getAccessToken("user4", Set.of("user")))
                .when().post("friends/invite/accept/user3")
                .then().statusCode(200)
                .body(is("The user trying to accept the friend request is not allowed to do it"));
    }

    @Test
    void acceptInvite_acceptingAnyInvite() {
        given()
                .auth().oauth2(getAccessToken("user5", Set.of("user")))
                .when().post("friends/invite/accept/user1")
                .then().statusCode(200)
                .body(is("The friendship doesn't exist"));
    }

    @Test
    void acceptInvite_acceptingAnInviteAlreadyAcceptedOrRefused() {
        given()
                .auth().oauth2(getAccessToken("user2", Set.of("user")))
                .when().post("friends/invite/accept/user1")
                .then().statusCode(200)
                .body(is("Friendship already accepted/refused"));
    }

    @Test
    void deleteFriendship() {
        given()
                .auth().oauth2(getAccessToken("user2", Set.of("user")))
                .when().delete("friends/list/user1")
                .then().statusCode(200);
        FriendEntity friendship = friendService.getFriend("user2", "user1");
        assertNull(friendship);
    }

    @Test
    void deleteFriendship_noFriendship() {
        given()
                .auth().oauth2(getAccessToken("user5", Set.of("user")))
                .when().delete("friends/list/user1")
                .then().statusCode(400);
        FriendEntity friendship = friendService.getFriend("user5", "user1");
        assertNull(friendship);
    }

    @AfterEach
    @Transactional
    void cleanDataBase() {
        FriendEntity.deleteAll();
        UserEntity.deleteAll();
    }
}
