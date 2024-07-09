package fr.zelytra.game.pool.game.customs;

import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.PoolBalls;
import fr.zelytra.game.pool.game.PoolGameInterface;
import fr.zelytra.game.pool.game.PoolGameManager;
import fr.zelytra.game.pool.game.PoolVictoryState;

public class ManualPoolGame extends PoolGameManager implements PoolGameInterface {

    @Override
    public void play(GameAction action) {
        this.getHistory().add(action);
    }

    @Override
    public PoolVictoryState winDetection() {
        PoolVictoryState victoryState = PoolVictoryState.NONE;
        boolean isEightIn = isEightIn();
        PoolVictoryState lastActionTeamId = getLastTeamPlay();

        if (isEightIn) {
            return lastActionTeamId.getInvertTeam();
        }

        return victoryState;

    }

    public boolean isEightIn() {
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
