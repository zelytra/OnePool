package fr.zelytra.game.pool;

import fr.zelytra.game.manager.message.SocketTimeOutManager;
import fr.zelytra.game.manager.socket.PoolSocketService;
import fr.zelytra.game.pool.data.*;
import fr.zelytra.game.pool.game.AmericanEightPoolGame;
import fr.zelytra.game.pool.game.PoolGameInterface;
import fr.zelytra.game.pool.game.PoolVictoryState;
import fr.zelytra.notification.NotificationMessageKey;
import fr.zelytra.poolpoint.PoolPointCalculator;
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
    private GameReport gameReport;

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

    public void setCurrentAction(GameAction action) {
        this.game.setCurrentAction(action);
    }

    public GameReport getGameReport() {
        return gameReport;
    }

    public void setGameReport(GameReport gameReport) {
        this.gameReport = gameReport;
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

    //TODO UT
    public boolean setState(GameStatus nextState) {
        // No empty teams
        if (this.state == GameStatus.TEAMING_PLAYERS && nextState == GameStatus.RUNNING) {
            if (this.game.getTeams().team1().isEmpty() || this.game.getTeams().team2().isEmpty()) {
                PoolSocketService.broadcastNotificationToParty(this, NotificationMessageKey.EMPTY_TEAM);
                return false;
            }
            game.initGame();
        } else if (nextState == GameStatus.END) {
            //Init timeout on game end
            for (PoolPlayer player : players) {
                new SocketTimeOutManager(60).init(player.getSocketSession());
            }
        }
        this.state = nextState;
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
        // If game is running don't remove the player from the list but just setting is session to null;
        if (state == GameStatus.RUNNING) {
            players.get(players.indexOf(user)).resetSession();
            return true;
        }

        if (players.remove(user)) {
            if (user.equals(gameOwner) && !players.isEmpty()) {
                gameOwner = players.getFirst();
            }
            return true;
        }

        return false;
    }

    public GameReport getGameReport(PoolVictoryState victoryState) {
        game.setVictoryState(victoryState);
        setState(GameStatus.END);
        GameReport gameReport = new GameReport(new ArrayList<>(), new ArrayList<>());
        for (PoolPlayer player : players) {
            PoolPointCalculator poolPointCalculator = new PoolPointCalculator(player.getPp(), player.getGamePlayed());
            List<Integer> opponentPPs = getPoolPlayersByTeam(victoryState.getInvertTeam()).stream().map(UserEntity::getPp).toList();

            if ((victoryState == PoolVictoryState.TEAM1 && game.getTeams().team1().contains(player.getAuthUsername())) ||
                    (victoryState == PoolVictoryState.TEAM2 && game.getTeams().team2().contains(player.getAuthUsername()))) {
                // Player is in the winning team
                int newPlayerPP = poolPointCalculator.computeNewElo(1.0, opponentPPs);
                gameReport.victoryPlayer().add(new GameReportPlayer(player.getPp(), newPlayerPP, player.getUsername()));
            } else {
                // Player is in the losing team
                int newPlayerPP = poolPointCalculator.computeNewElo(0, opponentPPs);
                gameReport.looserPlayer().add(new GameReportPlayer(player.getPp(), newPlayerPP, player.getUsername()));
            }
        }
        return gameReport;
    }

    public List<PoolPlayer> getPoolPlayersByTeam(PoolVictoryState poolVictoryState) {
        List<PoolPlayer> poolPlayers = new ArrayList<>();
        for (PoolPlayer player : players) {
            if (poolVictoryState == PoolVictoryState.TEAM1 && game.getTeams().team1().contains(player.getAuthUsername())) {
                poolPlayers.add(player);
            } else if (poolVictoryState == PoolVictoryState.TEAM2 && game.getTeams().team2().contains(player.getAuthUsername())) {
                poolPlayers.add(player);
            }
        }
        return poolPlayers;
    }

    public PoolPlayer getPoolPlayerByName(String username) {
        for (PoolPlayer player : players) {
            if (player.getAuthUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }
}
