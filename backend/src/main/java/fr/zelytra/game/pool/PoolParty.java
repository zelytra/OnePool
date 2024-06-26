package fr.zelytra.game.pool;

import fr.zelytra.game.manager.socket.PoolSocketService;
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
    private int maxPlayerAmount;
    private final PoolTeam teams;

    public PoolParty(PoolPlayer user) {
        this.gameOwner = user;
        players.add(user);
        state = GameStatus.SETUP;
        teams = new PoolTeam(new ArrayList<>(), new ArrayList<>());
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

    public boolean setTeams(PoolTeam teams) {
        // Limit player amount by rules
        if (teams.team1().size() + teams.team2().size() > maxPlayerAmount) {
            return false;
        }
        this.teams.team1().clear();
        this.teams.team2().clear();
        this.teams.team1().addAll(teams.team1());
        this.teams.team2().addAll(teams.team2());
        return true;
    }

    public GameStatus getState() {
        return state;
    }

    public boolean setState(GameStatus state) {
        // No empty teams
        if (this.state == GameStatus.TEAMING_PLAYERS && state == GameStatus.RUNNING) {
            if (this.teams.team1().isEmpty() || this.teams.team2().isEmpty()) {
                PoolSocketService.broadcastNotificationToParty(this, NotificationMessageKey.EMPTY_TEAM);
                return false;
            }
        }
        this.state = state;
        return true;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMaxPlayerAmount() {
        return maxPlayerAmount;
    }

    public PoolTeam getTeams() {
        return teams;
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
