package fr.zelytra.game.pool.data;

public enum GameRules {
    AMERICAN_8(6),
    MANUAL(6);
    //AMERICAN_9,
    //AMERICAN_9_CONTINUE,
    //AMERICAN_10,
    //AMERICAN_14_1_CONTINUE,
    //SPEED_BILLARD

    public final int maxTotalPlayer;

    GameRules(int maxTotalPlayer) {
        this.maxTotalPlayer = maxTotalPlayer;
    }
}
