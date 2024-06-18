package fr.zelytra.game.manager.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.zelytra.game.manager.message.SocketMessage;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.concurrent.*;

// WebSocket endpoint
@ServerEndpoint(value = "/sessions/{token}/{sessionId}")
public class SessionSocket {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static final ConcurrentMap<String, Future<?>> sessionTimeoutTasks = new ConcurrentHashMap<>();

    //TODO Handle list of version, not only the latest
    @ConfigProperty(name = "app.version")
    String appVersion;

    @Inject
    SocketService socketService;

    //@Inject
    //ExecutorService sqlExecutor;

    @OnOpen
    public void onOpen(Session session) {
        // Start a timeout task
        Future<?> timeoutTask = executor.submit(() -> {
            try {
                // Wait for a certain period for the initial message
                TimeUnit.SECONDS.sleep(1); // 1 seconds timeout
                // If the initial message is not received, close the session
                Log.info("[" + session.getId() + "] Timeout reached. Closing session.");
                session.close();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        });
        sessionTimeoutTasks.put(session.getId(), timeoutTask);
        Log.info("[ANYONE] Connecting...");
    }


    @OnMessage
    public void onMessage(String message, Session session, @PathParam("sessionId") String sessionId, @PathParam("token") String token) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        // Deserialize the incoming message to SocketMessage<?>
        SocketMessage<?> socketMessage = objectMapper.readValue(message, new TypeReference<>() {
        });

        // Handle the message based on its type
        switch (socketMessage.messageType()) {
            case CONNECT_TO_POOL -> {
                String username = objectMapper.convertValue(socketMessage.data(), String.class);
                socketService.joinPool(username, sessionId, session);
                removeUserFromTimeout(session.getId());
            }
            default -> Log.info("Unhandled message type: " + socketMessage.messageType());
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        socketService.playerClosedConnection(session.getId());
        session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        Log.error("WebSocket error for session " + session.getId() + ": " + throwable);
        session.close();
    }

    private void removeUserFromTimeout(String sessionId) {
        // Cancel the timeout task since we've received the message
        Future<?> timeoutTask = sessionTimeoutTasks.remove(sessionId);
        if (timeoutTask != null) {
            timeoutTask.cancel(true);
        }
    }
}
