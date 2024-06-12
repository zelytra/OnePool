package fr.zelytra.game.manager.socket;

import fr.zelytra.game.pool.PoolParty;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SocketService {

    @Inject
    UserService userService;

    private static final ConcurrentHashMap<String, PoolParty> games = new ConcurrentHashMap<>();

    public void createParty(String username) {
        Log.info("[SocketService.createParty] Creating party: " + username);
        UserEntity user = userService.getUserByName(username);

        if (user == null) {
            Log.warn("[SocketService.createParty] User not found: " + username);
            return;
        }

        PoolParty poolParty = new PoolParty(user);
        games.put(username, poolParty);
    }
}
