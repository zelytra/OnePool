package fr.zelytra.game.pool;

import fr.zelytra.game.manager.socket.PoolSocketService;
import fr.zelytra.game.pool.data.GameRules;
import fr.zelytra.game.pool.data.GameStatus;
import fr.zelytra.game.pool.data.PoolTeam;
import fr.zelytra.game.pool.game.AmericanEightPoolGame;
import fr.zelytra.game.pool.game.PoolGameInterface;
import fr.zelytra.notification.NotificationMessageKey;
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
    private PoolGameInterface game;

    public PoolParty(PoolPlayer user) {
        this.gameOwner = user;
        players.add(user);
        state = GameStatus.SETUP;
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

    public void setGame() {
        switch (rules) {
            case AMERICAN_8:
                game = new AmericanEightPoolGame();
                break;
            default:
                throw new IllegalArgumentException("Unsupported game type: " + rules);
        }

    }

    public void setRules(GameRules rules) {
        this.rules = rules;
        setGame();
    }

    public boolean setTeams(PoolTeam teams) {
        // Limit player amount by rules
        if (teams.team1().size() + teams.team2().size() > rules.maxTotalPlayer) {
            return false;
        }
        this.game.getTeams().team1().clear();
        this.game.getTeams().team2().clear();
        this.game.getTeams().team1().addAll(teams.team1());
        this.game.getTeams().team2().addAll(teams.team2());
        return true;
    }

    public GameStatus getState() {
        return state;
    }

    public boolean setState(GameStatus state) {
        // No empty teams
        if (this.state == GameStatus.TEAMING_PLAYERS && state == GameStatus.RUNNING) {
            if (this.game.getTeams().team1().isEmpty() || this.game.getTeams().team2().isEmpty()) {
                PoolSocketService.broadcastNotificationToParty(this, NotificationMessageKey.EMPTY_TEAM);
                return false;
            }
            game.initGame();
        }
        this.state = state;
        return true;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMaxPlayerAmount() {
        if (rules == null) {
            return 0;
        }
        return rules.maxTotalPlayer;
    }

    public PoolGameInterface getGame() {
        return game;
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
