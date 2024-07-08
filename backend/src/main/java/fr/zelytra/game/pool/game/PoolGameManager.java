package fr.zelytra.game.pool.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.GameReport;
import fr.zelytra.game.pool.data.PoolTeam;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.persistence.GeneratedValue;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "PoolGames")
public class PoolGameManager {

    @BsonId
    @GeneratedValue
    private int id;

    private final PoolTeam teams = new PoolTeam(new ArrayList<>(), new ArrayList<>());
    private final long startingTime;
    private long endTime;
    private final List<GameAction> history = new ArrayList<>();
    private GameAction currentAction;
    private boolean paused = false;
    private PoolVictoryState victoryState = PoolVictoryState.NONE;
    private GameReport gameReport;

    public PoolGameManager() {
        startingTime = System.currentTimeMillis();
    }

    public void initGame() {
        this.currentAction = new GameAction(0, new ArrayList<>(), new ArrayList<>(), this.getTeams().team1().getFirst());
    }

    public PoolVictoryState getLastTeamPlay() {
        if (history.isEmpty()) {
            return PoolVictoryState.NONE;
        }

        if (teams.team2().contains(history.getLast().username())) {
            return PoolVictoryState.TEAM2;
        } else {
            return PoolVictoryState.TEAM1;
        }
    }

    public List<GameAction> getTeamActions(List<String> teamNames) {
        List<GameAction> actions = new ArrayList<>();
        for (String userName : teamNames) {
            for (GameAction action : history) {
                if (action.username().equals(userName)) {
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @JsonIgnore
    @BsonIgnore
    public String getNextPlayer() {
        // Get the list of players from both teams
        List<String> team1 = teams.team1();
        List<String> team2 = teams.team2();

        // Check if history is empty and return the first player from team1 if so
        if (history.isEmpty()) {
            return team1.getFirst();
        }

        // Get the last player who played from the history
        GameAction lastAction = history.getLast();
        String lastPlayer = lastAction.username();

        // Determine the team of the last player
        boolean lastPlayerFromTeam1 = team1.contains(lastPlayer);

        // Find the next player from the opposite team who hasn't played yet in this round
        List<String> currentTeam = lastPlayerFromTeam1 ? team2 : team1;
        for (String player : currentTeam) {
            boolean hasPlayed = false;
            for (GameAction action : history) {
                if (action.username().equals(player)) {
                    hasPlayed = true;
                    break;
                }
            }
            if (!hasPlayed) {
                return player;
            }
        }

        // If all players from the current team have played, reset and return the first player from the opposite team
        return currentTeam.getFirst();
    }


    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public PoolVictoryState getVictoryState() {
        return victoryState;
    }

    public void setVictoryState(PoolVictoryState victoryState) {
        this.victoryState = victoryState;
    }

    public PoolTeam getTeams() {
        return teams;
    }

    public GameAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(GameAction action) {
        currentAction = action;
    }

    public long getStartingTime() {
        return startingTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public List<GameAction> getHistory() {
        return history;
    }

    public boolean isPaused() {
        return paused;
    }

    public GameReport getGameReport() {
        return gameReport;
    }

    public void setGameReport(GameReport gameReport) {
        this.gameReport = gameReport;
    }
}
