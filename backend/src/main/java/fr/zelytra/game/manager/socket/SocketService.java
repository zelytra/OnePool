package fr.zelytra.game.manager.socket;

import fr.zelytra.game.pool.PoolParty;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SocketService {
    private static final ConcurrentHashMap<String, PoolParty> games = new ConcurrentHashMap<>();

    public void createParty(String username) {

    }
}
