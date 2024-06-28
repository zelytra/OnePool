package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.PoolTeam;

import java.util.ArrayList;
import java.util.List;

public class PoolGameManager {

    private final PoolTeam teams = new PoolTeam(new ArrayList<>(), new ArrayList<>());
    private long startingTime;
    private long endTime;
    private final List<GameAction> history = new ArrayList<>();
    private String userPlayingRound;
    private boolean paused = false;

    public PoolGameManager() {
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public PoolTeam getTeams() {
        return teams;
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

    public String getUserPlayingRound() {
        return userPlayingRound;
    }

    public boolean isPaused() {
        return paused;
    }
}
