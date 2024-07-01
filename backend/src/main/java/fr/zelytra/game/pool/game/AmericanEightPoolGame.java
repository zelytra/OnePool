package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;

public class AmericanEightPoolGame extends PoolGameManager implements PoolGameInterface {

    public AmericanEightPoolGame() {
        super();
    }

    @Override
    public void play(GameAction action) {

        /** Lose game handler
         * If player get 8 not the last
         * If 8 out of the table
         * If 8 + fault in the same round
         * If not call
         * If white + 8
         **/

        // If the player get a good ball continue the shot


    }

    @Override
    public boolean isValidAction(GameAction action) {
        return false;
    }


}
