package fr.zelytra.notification;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.zelytra.game.manager.message.MessageType;
import fr.zelytra.game.manager.message.SocketMessage;
import fr.zelytra.game.manager.message.SocketTimeOutManager;
import io.quarkus.logging.Log;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static fr.zelytra.game.manager.message.ObjectMapperConfig.objectMapper;

// WebSocket endpoint
@ServerEndpoint(value = "/notification/{token}")
public class NotificationSocket {

    private final SocketTimeOutManager socketTimeOutManager = new SocketTimeOutManager();
    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        socketTimeOutManager.initLogin(session);
        Log.info("[ANYONE] Connecting...");
    }


    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token) throws IOException {

        // Deserialize the incoming message to SocketMessage<?>
        SocketMessage<?> socketMessage = objectMapper.readValue(message, new TypeReference<>() {
        });

        // Handle the message based on its type
        switch (socketMessage.messageType()) {
            case INIT_NOTIFICATION -> {
                String username = objectMapper.convertValue(socketMessage.data(), String.class);
                sessions.put(username, session);
                socketTimeOutManager.completeLogin(session.getId());
            }
            case SEND_NOTIFICATION -> {
                NotificationMessage<Object> notificationMessage = objectMapper.convertValue(socketMessage.data(), NotificationMessage.class);
                SocketMessage<NotificationMessage<Object>> socketMessageHandler = new SocketMessage<>(MessageType.SEND_NOTIFICATION, notificationMessage);

                for (String username : notificationMessage.users()) {
                    if (!sessions.containsKey(username)) {
                        Log.warn("[notification] User " + username + " not found");
                        continue;
                    }
                    socketMessageHandler.sendDataToPlayer(sessions.get(username));
                }
                Log.info("[notification] Notifications pushed to " + notificationMessage.users().size() + " users");
            }
            default -> Log.info("Unhandled message type: " + socketMessage.messageType());
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {

        session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        Log.error("WebSocket error for session " + session.getId() + ": " + throwable);
        session.close();
    }
}
