package fr.zelytra.user;

import fr.zelytra.friend.FriendEntity;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.quarkus.test.oidc.server.OidcWiremockTestResource.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class UserEndpointTest {

    @Test
    void getPreferences_generateUnknownUser(){
        UserEntity user = given()
                .auth().oauth2(getAccessToken("userTest", Set.of("user")))
                .when().get("/user/preferences")
                .then().statusCode(200)
                .extract()
                .body()
                .as(UserEntity.class);
        assertNotNull(user);
        UserEntity foundedUser = UserEntity.findById("userTest");
        assertNotNull(foundedUser);
        assertEquals(foundedUser.getUsername(), "userTest","The user found didn't match the input user");
    }

    @AfterEach
    @Transactional
    void cleanDataBase(){
        FriendEntity.deleteAll();
        UserEntity.deleteAll();
    }
}
