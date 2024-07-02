package fr.zelytra.game.pool.game;

public enum PoolVictoryState {
    NONE,
    TEAM1,
    TEAM2;

    public PoolVictoryState getInvertTeam() {
        if (this == TEAM1) {
            return TEAM2;
        } else {
            return TEAM1;
        }
    }
}
