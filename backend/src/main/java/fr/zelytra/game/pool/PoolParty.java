package fr.zelytra.game.pool;

import fr.zelytra.user.UserService;

import java.util.ArrayList;
import java.util.List;

public class PoolParty {

    private final List<UserService> players = new ArrayList<>();
    private UserService gameOwner;
    private GameRules rules;
    private GameStatus state;

    public PoolParty(UserService user) {
        this.gameOwner = user;
        players.add(user);
        state = GameStatus.SETUP;
    }

    public List<UserService> getPlayers() {
        return players;
    }

    public UserService getGameOwner() {
        return gameOwner;
    }

    public void setGameOwner(UserService gameOwner) {
        this.gameOwner = gameOwner;
    }

    public GameRules getRules() {
        return rules;
    }

    public void setRules(GameRules rules) {
        this.rules = rules;
    }

    public GameStatus getState() {
        return state;
    }

    public void setState(GameStatus state) {
        this.state = state;
    }
}
