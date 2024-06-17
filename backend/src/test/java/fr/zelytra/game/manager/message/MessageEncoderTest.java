package fr.zelytra.game.manager.message;

import com.google.gson.Gson;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class MessageEncoderTest {

    private MessageEncoder messageEncoder;
    private Gson gson;

    @BeforeEach
    void setup() {
        messageEncoder = new MessageEncoder();
        gson = new Gson();
    }

    @Test
    void testEncode() {
        SocketMessage<String> socketMessage = new SocketMessage<>(MessageType.CREATE_POOL, "Test Data");
        String encodedMessage = messageEncoder.encode(socketMessage);

        String expectedJson = gson.toJson(socketMessage);
        assertEquals(expectedJson, encodedMessage);
    }
}