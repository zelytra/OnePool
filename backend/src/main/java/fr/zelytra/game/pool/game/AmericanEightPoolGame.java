package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;

public class AmericanEightPoolGame extends PoolGameManager implements PoolGameInterface {

    public AmericanEightPoolGame() {
        super();
    }

    @Override
    public void play(GameAction action) {

    }

    @Override
    public boolean isValidAction(GameAction action) {
        return false;
    }


}
