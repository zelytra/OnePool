package fr.zelytra.game.pool.data;

import java.util.List;

public record GameAction(List<PoolBalls> balls, List<PoolFault> faults, String username) {
}
