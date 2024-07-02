package fr.zelytra.game.pool.data;

import java.util.List;

public record GameAction(int roundId, List<PoolBalls> balls, List<PoolFault> faults, String username) {
}
