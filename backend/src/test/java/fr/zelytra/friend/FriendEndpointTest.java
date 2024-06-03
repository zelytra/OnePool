package fr.zelytra.friend;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class FriendEndpointTest {

    @Test
    void getFriends_code200(){
        given().when().get("/friends/list").then().statusCode(200);
    }
}
