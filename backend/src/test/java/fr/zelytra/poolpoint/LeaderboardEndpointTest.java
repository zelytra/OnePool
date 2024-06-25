package fr.zelytra.poolpoint;

import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import fr.zelytra.user.reflections.LeaderBoardUser;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static io.quarkus.test.oidc.server.OidcWiremockTestResource.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class LeaderboardEndpointTest {

    @Inject
    UserService userService;

    @BeforeEach
    @Transactional
    void init() {
        for (int x = 0; x < 200; x++) {
            UserEntity user = userService.getOrCreateUserByName("User" + x);
            user.setPp(1000 + x);
        }
    }

    @Test
    void getTopLeaderboard_maxResult100() {
        List<LeaderBoardUser> list = given()
                .auth().oauth2(getAccessToken("User1", Set.of("user")))
                .when().get("/leaderboard/all")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        assertEquals(100, list.size(), "Should return max 100 results");
    }

    @Test
    void getTopLeaderboard_orderByPP() {
        List<LeaderBoardUser> list = given()
                .auth().oauth2(getAccessToken("User1", Set.of("user")))
                .when().get("/leaderboard/all")
                .then().statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        int index = 0;
        for (LeaderBoardUser leaderBoardUser : list) {
            assertEquals(1000 + index, leaderBoardUser.pp());
            index++;
        }
    }

    @Test
    void getUserLeaderboardPosition_returnTheRightPosition1() {
        UserLeaderBoardPosition userLeaderBoardPosition = given()
                .auth().oauth2(getAccessToken("User0", Set.of("user")))
                .when().get("/leaderboard/self")
                .then().statusCode(200)
                .extract()
                .body()
                .as(UserLeaderBoardPosition.class);
        assertEquals(200, userLeaderBoardPosition.position());
        assertEquals(1000,userLeaderBoardPosition.pp());
    }

    @Test
    void getUserLeaderboardPosition_returnTheRightPosition2() {
        UserLeaderBoardPosition userLeaderBoardPosition = given()
                .auth().oauth2(getAccessToken("User42", Set.of("user")))
                .when().get("/leaderboard/self")
                .then().statusCode(200)
                .extract()
                .body()
                .as(UserLeaderBoardPosition.class);
        assertEquals(158, userLeaderBoardPosition.position());
        assertEquals(1042,userLeaderBoardPosition.pp());
    }

    @AfterAll
    @Transactional
    static void cleanDataBase() {
        UserEntity.deleteAll();
    }
}
