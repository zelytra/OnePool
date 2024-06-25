package fr.zelytra.game.manager.message;

import fr.zelytra.user.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class SocketMessageTest {

    @Test
    void testSocketMessageToString() {
        // Test with String data
        SocketMessage<String> stringMessage = new SocketMessage<String>(MessageType.CREATE_POOL, "Hello World");
        assertEquals("CREATE_POOL class java.lang.String", stringMessage.toString());

        // Test with Integer data
        SocketMessage<Integer> intMessage = new SocketMessage<>(MessageType.CREATE_POOL, 123);
        assertEquals("CREATE_POOL class java.lang.Integer", intMessage.toString());

        // Test with custom data
        UserEntity userEntity = new UserEntity("testUser");
        SocketMessage<UserEntity> userMessage = new SocketMessage<>(MessageType.CREATE_POOL, userEntity);
        assertEquals("CREATE_POOL class fr.zelytra.user.UserEntity", userMessage.toString());
    }
}