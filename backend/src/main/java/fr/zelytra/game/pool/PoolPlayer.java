package fr.zelytra.game.pool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.zelytra.user.UserEntity;
import jakarta.websocket.Session;

public class PoolPlayer extends UserEntity {

    @JsonIgnore
    private Session socketSession;

    public PoolPlayer(UserEntity user, Session socketSession) {
        super(user);
        this.socketSession = socketSession;
    }

    public Session getSocketSession() {
        return socketSession;
    }
}
