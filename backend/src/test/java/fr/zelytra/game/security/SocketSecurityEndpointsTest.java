package fr.zelytra.game.security;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.quarkus.test.oidc.server.OidcWiremockTestResource.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class SocketSecurityEndpointsTest {

    private static final String RANDOM_BEARER_TOKEN = "337aab0f-b547-489b-9dbd-a54dc7bdf20d";

    @Test
    void registerClient_connectionRefused() {
        given()
                .when()
                .header("Authorization", "Bearer: " + RANDOM_BEARER_TOKEN)
                .get("/socket/register")
                .then()
                .statusCode(401);
    }

    @Test
    void registerClient() {
        Response response = given()
                .auth().oauth2(getAccessToken("User1", Set.of("user")))
                .when().get("/socket/register")
                .then().statusCode(200)
                .extract()
                .response();
        String key = response.getBody().asString();
        assertNotNull(key);
    }
}
