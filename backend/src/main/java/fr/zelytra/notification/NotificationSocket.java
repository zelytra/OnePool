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
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static fr.zelytra.game.manager.message.ObjectMapperConfig.objectMapper;

// WebSocket endpoint
@ServerEndpoint(value = "/notifications/{token}")
public class NotificationSocket {

    private final SocketTimeOutManager socketTimeOutManager = new SocketTimeOutManager();
    public final static ConcurrentMap<String, Session> notificationSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        socketTimeOutManager.initLogin(session);
        Log.info("[notification:OPEN] Connecting...");
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
                notificationSessions.put(username, session);
                socketTimeOutManager.completeLogin(session.getId());
                Log.info("[notification:INIT_NOTIFICATION] " + username + " connected");
            }
            case SEND_NOTIFICATION -> {
                NotificationMessage<?> notificationMessage = objectMapper.convertValue(socketMessage.data(), NotificationMessage.class);
                SocketMessage<NotificationMessage<?>> socketMessageHandler = new SocketMessage<>(MessageType.SEND_NOTIFICATION, notificationMessage);

                for (String username : notificationMessage.users()) {
                    if (!notificationSessions.containsKey(username)) {
                        Log.warn("[notification:SEND_NOTIFICATION] User " + username + " not found");
                        continue;
                    }
                    socketMessageHandler.sendDataToPlayer(notificationSessions.get(username));
                }
                Log.info("[notification:SEND_NOTIFICATION] Notifications pushed to " + notificationMessage.users().size() + " users");
            }
            default -> Log.info("Unhandled message type: " + socketMessage.messageType());
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        Log.info("[notification:CLOSE] " + session.getId());
        closeSocket(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        Log.error("[notification:ERROR] WebSocket error for session " + session.getId() + ": " + throwable);
        closeSocket(session);
    }

    private void closeSocket(Session session) throws IOException {
        for (var entry : notificationSessions.entrySet()) {
            if (entry.getValue().getId().equalsIgnoreCase(session.getId())) {
                notificationSessions.remove(entry.getKey());
            }
        }
        session.close();
    }

    public static void sendNotification(Notification<String> notificationMessage, String username) {
        NotificationMessage<Notification<String>> formatedMessage = new NotificationMessage<>(new ArrayList<>(), notificationMessage);
        SocketMessage<NotificationMessage<Notification<String>>> socketMessageHandler = new SocketMessage<>(MessageType.SEND_NOTIFICATION, formatedMessage);
        if (!notificationSessions.containsKey(username)) {
            Log.warn("[notification:SEND_NOTIFICATION] User " + username + " not found");
            return;
        }
        socketMessageHandler.sendDataToPlayer(notificationSessions.get(username));
    }
}
