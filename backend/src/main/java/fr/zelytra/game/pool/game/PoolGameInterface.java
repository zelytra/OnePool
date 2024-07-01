package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.PoolTeam;

public interface PoolGameInterface {
    void play(GameAction action);

    boolean isValidAction(GameAction action);

    PoolTeam getTeams();

    void initGame();

    GameAction getCurrentAction();
    void setCurrentAction(GameAction action);
}
