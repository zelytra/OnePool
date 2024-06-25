package fr.zelytra.game.manager.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MessageDecoderTest {

    private MessageDecoder messageDecoder;

    @BeforeEach
    public void setup() {
        messageDecoder = new MessageDecoder();
    }

    @Test
    void testDecodeValidMessage() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SocketMessage<String> originalMessage = new SocketMessage<>(MessageType.CREATE_POOL, "Test Data");
        String jsonString = objectMapper.writeValueAsString(originalMessage);

        SocketMessage<?> decodedMessage = messageDecoder.decode(jsonString);

        assertNotNull(decodedMessage);
        assertEquals(MessageType.CREATE_POOL, decodedMessage.messageType());
        assertEquals("Test Data", decodedMessage.data());
    }

    @Test
    void testDecodeInvalidMessage() {
        String invalidJson = "{invalid json}";

        assertThrows(RuntimeException.class, () -> {
            messageDecoder.decode(invalidJson);
        });
    }

    @Test
    void testWillDecode() {
        assertTrue(messageDecoder.willDecode("Non-empty string"));
        assertFalse(messageDecoder.willDecode(null));
    }
}