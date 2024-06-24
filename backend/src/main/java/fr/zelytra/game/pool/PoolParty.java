package fr.zelytra.game.pool;

import fr.zelytra.user.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PoolParty {

    private final List<PoolPlayer> players = new ArrayList<>();
    private final String uuid = UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    private PoolPlayer gameOwner;
    private GameRules rules;
    private GameStatus state;
    private int maxPlayerAmount;

    public PoolParty(PoolPlayer user) {
        this.gameOwner = user;
        players.add(user);
        state = GameStatus.TEAMING_PLAYERS;
    }

    public List<PoolPlayer> getPlayers() {
        return players;
    }

    public PoolPlayer getGameOwner() {
        return gameOwner;
    }

    public void setGameOwner(PoolPlayer gameOwner) {
        this.gameOwner = gameOwner;
    }

    public GameRules getRules() {
        return rules;
    }

    public void setRules(GameRules rules) {
        this.rules = rules;
        this.maxPlayerAmount = rules.maxTotalPlayer;
    }

    public GameStatus getState() {
        return state;
    }

    public void setState(GameStatus state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMaxPlayerAmount() {
        return maxPlayerAmount;
    }

    /**
     * Removes a player from the pool party. If the player is the game owner,
     * replaces the game owner with another player from the list.
     *
     * @param user the user to remove
     * @return true if the user was removed, false otherwise
     */
    public boolean removePlayer(UserEntity user) {
        if (players.remove(user)) {
            if (user.equals(gameOwner) && !players.isEmpty()) {
                gameOwner = players.getFirst();
            }
            return true;
        }
        return false;
    }
}
