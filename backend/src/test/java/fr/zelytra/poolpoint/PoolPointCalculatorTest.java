package fr.zelytra.poolpoint;

import fr.zelytra.poolpoint.simulation.PoolPlayerSimulator;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class PoolPointCalculatorTest {

    @Test
    @Transactional
    void simulation() {
        PoolPlayerSimulator poolPlayerSimulator = new PoolPlayerSimulator(101, 5000);
        Log.info(poolPlayerSimulator);
        poolPlayerSimulator.generateCSV("game_history.csv");
    }

    @Test
    void getFactorK_lessThan30Games() {
        int user1PP = 600;
        int user1GamesPlayed = 25;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(40, poolPointCalculator.getFactorK());
    }

    @Test
    void getFactorK_lessThan2400AndMoreThan30Games() {
        int user1PP = 600;
        int user1GamesPlayed = 80;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(20, poolPointCalculator.getFactorK());
    }

    @Test
    void getFactorK_MoreThan2400() {
        int user1PP = 2401;
        int user1GamesPlayed = 80;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(10, poolPointCalculator.getFactorK());
    }

    @Test
    void getWinProbability_deltaLessThan400() {
        int user1PP = 1800;
        int user1GamesPlayed = 80;
        int user2PP = 1600;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(0.76, poolPointCalculator.getWinProbability(user2PP));
        assertEquals(20, poolPointCalculator.getFactorK());
    }

    @Test
    void getWinProbability_deltaMoreThan400() {
        int user1PP = 2600;
        int user1GamesPlayed = 80;
        int user2PP = 1600;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(0.909, poolPointCalculator.getWinProbability(user2PP));
        assertEquals(10, poolPointCalculator.getFactorK());
    }

    @Test
    void computeNewElo_win() {
        int user1PP = 2600;
        int user1GamesPlayed = 80;
        int user2PP = 1600;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(0.909, poolPointCalculator.getWinProbability(user2PP));
        assertEquals(10, poolPointCalculator.getFactorK());
        assertEquals(2601, poolPointCalculator.computeNewElo(1, user2PP));

    }

    @Test
    void computeNewElo_loose() {
        int user1PP = 2600;
        int user1GamesPlayed = 80;
        int user2PP = 1600;
        PoolPointCalculator poolPointCalculator = new PoolPointCalculator(user1PP, user1GamesPlayed);
        assertEquals(0.909, poolPointCalculator.getWinProbability(user2PP));
        assertEquals(10, poolPointCalculator.getFactorK());
        assertEquals(2591, poolPointCalculator.computeNewElo(0, user2PP));

    }


}
