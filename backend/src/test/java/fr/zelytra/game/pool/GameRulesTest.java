package fr.zelytra.game.pool;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class GameRulesTest {

    @Test
    void valueOf() {
        assertNotNull(GameRules.valueOf("AMERICAN_8"));
    }
}