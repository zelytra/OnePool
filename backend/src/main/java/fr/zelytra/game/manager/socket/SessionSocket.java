package fr.zelytra.game.manager.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.zelytra.game.manager.message.SocketMessage;
import fr.zelytra.game.manager.message.SocketTimeOutManager;
import fr.zelytra.game.pool.GameRules;
import fr.zelytra.game.pool.GameStatus;
import fr.zelytra.game.pool.PoolTeam;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

import static fr.zelytra.game.manager.message.ObjectMapperConfig.objectMapper;

// WebSocket endpoint
@ServerEndpoint(value = "/sessions/{token}/{sessionId}")
public class SessionSocket {

    private final SocketTimeOutManager socketTimeOutManager = new SocketTimeOutManager();

    //TODO Handle list of version, not only the latest
    @ConfigProperty(name = "app.version")
    String appVersion;

    @Inject
    PoolSocketService socketService;

    @OnOpen
    public void onOpen(Session session) {
        socketTimeOutManager.initLogin(session);
        Log.info("[ANYONE] Connecting...");
    }


    @OnMessage
    public void onMessage(String message, Session session, @PathParam("sessionId") String sessionId, @PathParam("token") String token) throws IOException {

        // Deserialize the incoming message to SocketMessage<?>
        SocketMessage<?> socketMessage = objectMapper.readValue(message, new TypeReference<>() {
        });

        // Handle the message based on its type
        switch (socketMessage.messageType()) {
            case CONNECT_TO_POOL -> {
                String username = objectMapper.convertValue(socketMessage.data(), String.class);
                socketService.joinPool(username, sessionId, session);
                socketTimeOutManager.completeLogin(session.getId());
            }
            case SET_RULES -> {
                socketService.setRule(objectMapper.convertValue(socketMessage.data(), GameRules.class), session.getId());
            }
            case UPDATE_TEAMS -> {
                socketService.setPlayersTeam(objectMapper.convertValue(socketMessage.data(), PoolTeam.class), session.getId());
            }
            case CHANGE_GAME_STATES -> {
                GameStatus status = objectMapper.convertValue(socketMessage.data(), GameStatus.class);
                socketService.setStatus(status, session.getId());
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
}
