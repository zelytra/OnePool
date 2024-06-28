package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.PoolTeam;

public interface PoolGameInterface {
    void play(GameAction action);
    boolean isValidAction(GameAction action);

    // Add methods to access PoolGameManager properties if needed
    PoolTeam getTeams();
}
