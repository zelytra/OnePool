package fr.zelytra.poolpoint;

public class PoolPointCalculator {

    private int poolpoint;
    private int k; // K-factor
    private int totalGamePlayed;

    public PoolPointCalculator(int poolpoint, int totalGamePlayed) {
        this.poolpoint = poolpoint;
        this.totalGamePlayed = totalGamePlayed;
        this.k = getFactorK();
    }

    /**
     *
     * @param partyStatus Win = 1 | Loose = 0 | Draw = 0.5
     * @param opponentElo Poolpoint of opponent
     * @return En+1 = En + K * (W - p(D)) The new elo of the player
     */
    public int computeNewElo(double partyStatus, int opponentElo) {
        return (int) Math.round(poolpoint + k * (partyStatus - getWinProbability(opponentElo)));
    }

    private int getFactorK() {
        // New player with less than 30 games and less than 2300 of score
        if (totalGamePlayed < 30 && poolpoint < 2300) {
            return 40;
        }
        // Old player with less than 2400 of score
        else if (poolpoint < 2400) {
            return 20;
        }
        // Old player with a superior or equal score of 2400
        return 10;
    }

    /**
     * The function p(D) Which represent win probability
     *
     * @param opponentElo Poolpoint of opponent
     * @return p(D) Gain probability
     */
    private double getWinProbability(int opponentElo) {
        int eloDelta = poolpoint - opponentElo; // D
        double winProbaility = 1 / (1 + Math.pow(10, -eloDelta / 400)); // p(D)
        return winProbaility;
    }


}
