package fr.zelytra.game.manager.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import jakarta.websocket.Session;

import java.util.TimeZone;

public record SocketMessage<T>(MessageType messageType, T data) {

    /**
     * Sends a message to a player within a session identified by the WebSocket ID.
     *
     * @param session     The WebSocket to which the data will be sent. This is used to identify the player who should receive the message.
     * @throws Error if there is an issue with converting the {@link SocketMessage} object to a JSON string.
     */
    public void sendDataToPlayer(Session session) {
        String json = formatMessage();

        if (session == null || session.getAsyncRemote() == null) {
            Log.error("[sendDataToPlayer] Failed to get the player socket");
            return;
        }

        // Send the data to the specific WebSocket connection
        session.getAsyncRemote().sendText(json, result -> {
            if (result.isOK()) {
                Log.info("[sendDataToPlayer] Type: " + messageType.name());
            }
            if (result.getException() != null) {
                Log.error("[sendDataToPlayer] Unable to send message to [" + session.getId() + "]: " + result.getException());
            }
        });
    }

    /**
     * Formats a message to be sent to a player.
     *
     * @return a JSON string representation of the message
     * @throws Error if there is an issue with converting the {@link SocketMessage} object to a JSON string.
     */
    private String formatMessage() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // To serialize as ISO-8601 strings
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        String json;
        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new Error(e);
        }
        return json;
    }

    @Override
    public String toString() {
        return messageType().name() + " " + data.getClass();
    }
}
