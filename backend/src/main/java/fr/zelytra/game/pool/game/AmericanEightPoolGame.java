package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.PoolBalls;
import fr.zelytra.game.pool.data.PoolFault;

import java.util.ArrayList;
import java.util.List;

public class AmericanEightPoolGame extends PoolGameManager implements PoolGameInterface {

    public AmericanEightPoolGame() {
        super();
    }

    @Override
    public void play(GameAction action) {
        this.getHistory().add(action);
        this.setCurrentAction(new GameAction(this.getHistory().size() - 1, new ArrayList<>(), new ArrayList<>(), getNextPlayer()));
    }

    @Override
    public PoolVictoryState winDetection() {
        PoolVictoryState victoryState = PoolVictoryState.NONE;
        int strippedCount = getStrippedCount();
        int fullCount = getFullCount();
        boolean isEightIn = isEightIn();
        PoolVictoryState lastActionTeamId = getLastTeamPlay();

        // Do nothing any action to analyze
        if (lastActionTeamId == PoolVictoryState.NONE) {
            return PoolVictoryState.NONE;
        }

        GameAction lastAction = this.getHistory().getLast();

        // Eight before family clean
        if (isEightIn && (strippedCount < 7 || fullCount < 7)) {
            return lastActionTeamId.getInvertTeam();
        }

        // Eight out of the table
        if (lastAction.faults().contains(PoolFault.EIGHT_OUT)) {
            return lastActionTeamId.getInvertTeam();
        }

        // If fault is commit at the same time as eight in (contain also white in during the shot)
        if (isEightIn && !lastAction.faults().isEmpty()) {
            return lastActionTeamId.getInvertTeam();
        }

        // Eight not call
        if (lastAction.faults().contains(PoolFault.EIGHT_NO_CALL)) {
            return lastActionTeamId.getInvertTeam();
        }

        if (strippedCount == 7 && isEightIn()) {
            if (isTeamStripped(lastActionTeamId)) {
                return PoolVictoryState.TEAM1;
            } else {
                return PoolVictoryState.TEAM2;
            }
        } else if (fullCount == 7 && isEightIn()) {
            if (isTeamStripped(lastActionTeamId)) {
                return PoolVictoryState.TEAM2;
            } else {
                return PoolVictoryState.TEAM1;
            }
        }

        return victoryState;
    }

    private boolean isTeamStripped(PoolVictoryState teamId) {
        List<GameAction> teamActions = new ArrayList<>();
        if (teamId == PoolVictoryState.TEAM1) {
            teamActions.addAll(getTeamActions(getTeams().team1()));
        } else {
            teamActions.addAll(getTeamActions(getTeams().team2()));
        }

        int strippedCount = 0;
        int fullCount = 0;
        for (GameAction action : teamActions) {
            for (PoolBalls ball : action.balls()) {
                if (ball.number >= 9) {
                    strippedCount++;
                } else {
                    fullCount++;
                }
            }
        }

        return strippedCount > fullCount;
    }

    private int getStrippedCount() {
        int strippedCount = 0;
        for (GameAction action : getHistory()) {
            for (PoolBalls ball : action.balls()) {
                if (ball.number >= 9) {
                    strippedCount++;
                }
            }
        }
        return strippedCount;
    }

    private int getFullCount() {
        int fullCount = 0;
        for (GameAction action : getHistory()) {
            for (PoolBalls ball : action.balls()) {
                if (ball.number < 8) {
                    fullCount++;
                }
            }
        }
        return fullCount;
    }

    private boolean isEightIn() {
        for (GameAction action : getHistory()) {
            for (PoolBalls ball : action.balls()) {
                if (ball.number == 8) {
                    return true;
                }
            }
        }
        return false;
    }

}
