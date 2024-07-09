package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;
import fr.zelytra.game.pool.data.PoolBalls;
import fr.zelytra.game.pool.data.PoolFault;
import fr.zelytra.game.pool.game.customs.AmericanEightPoolGame;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AmericanEightPoolGameTest {

    private AmericanEightPoolGame game;

    @BeforeEach
    public void setup() {
        game = new AmericanEightPoolGame();
        game.getTeams().team1().add("player1");
        game.getTeams().team1().add("player3");
        game.getTeams().team2().add("player2");
        game.getTeams().team2().add("player4");
        game.initGame();
    }

    @Test
    void testPlay() {
        GameAction action = new GameAction(1, new ArrayList<>(), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(1, game.getHistory().size());
        assertEquals(action, game.getHistory().getFirst());
    }

    @Test
    void testPlay_nextPlayerNoBall() {
        GameAction action = new GameAction(1, new ArrayList<>(), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(1, game.getHistory().size());
        assertNotNull(game.getCurrentAction());
        assertEquals("player2", game.getCurrentAction().username());
    }

    @Test
    void testPlay_nextPlayerBallInPocketNoFault_playAgain() {
        GameAction action = new GameAction(1, List.of(PoolBalls.ONE), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(1, game.getHistory().size());
        assertNotNull(game.getCurrentAction());
        assertEquals("player1", game.getCurrentAction().username());
    }

    @Test
    void testPlay_nextPlayerBallInPocketFault_nextPlayer() {
        GameAction action = new GameAction(1, List.of(PoolBalls.ONE), List.of(PoolFault.NOT_GOOD_FAMILY), "player1");
        game.play(action);

        assertEquals(1, game.getHistory().size());
        assertNotNull(game.getCurrentAction());
        assertEquals("player2", game.getCurrentAction().username());
    }

    @Test
    void testWinDetectionNoAction() {
        assertEquals(PoolVictoryState.NONE, game.winDetection());
    }

    @Test
    void testWinDetectionEightBallIn() {
        // Prepare the game state to simulate an eight ball in
        GameAction action = new GameAction(1, List.of(PoolBalls.EIGHT), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }

    @Test
    void testWinDetection_allInOne() {
        // Prepare the game state to simulate all family ball in the pocket and eight
        GameAction action = new GameAction(1, List.of(
                PoolBalls.ONE,
                PoolBalls.TWO,
                PoolBalls.THREE,
                PoolBalls.FOUR,
                PoolBalls.FIVE,
                PoolBalls.SIX,
                PoolBalls.SEVEN,
                PoolBalls.EIGHT)
                , new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM1, game.winDetection());
    }

    @Test
    void testWinDetectionStrippedAllPocketedAndEightIn() {
        // Simulate all stripped balls and the eight ball pocketed
        GameAction action = new GameAction(1, List.of(
                PoolBalls.NINE, PoolBalls.TEN, PoolBalls.ELEVEN,
                PoolBalls.TWELVE, PoolBalls.THIRTEEN, PoolBalls.FOURTEEN, PoolBalls.FIFTEEN, PoolBalls.EIGHT
        ), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM1, game.winDetection());
    }

    @Test
    void testWinDetectionStrippedAllPocketedAndEightInForTeam2() {
        // Simulate all stripped balls and the eight ball pocketed for Team 2
        GameAction action = new GameAction(1, List.of(
                PoolBalls.NINE, PoolBalls.TEN, PoolBalls.ELEVEN,
                PoolBalls.TWELVE, PoolBalls.THIRTEEN, PoolBalls.FOURTEEN, PoolBalls.FIFTEEN
        ), new ArrayList<>(), "player2");
        game.play(action);
        action = new GameAction(2, List.of(PoolBalls.EIGHT), new ArrayList<>(), "player2");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }

    @Test
    void testWinDetectionStrippedAllPocketedAndEightInForTeam1() {
        // Simulate all stripped balls and the eight ball pocketed for Team 2
        GameAction action = new GameAction(1, List.of(
                PoolBalls.NINE, PoolBalls.TEN, PoolBalls.ELEVEN,
                PoolBalls.TWELVE, PoolBalls.THIRTEEN, PoolBalls.FOURTEEN, PoolBalls.FIFTEEN
        ), new ArrayList<>(), "player2");
        game.play(action);
        action = new GameAction(2, List.of(
                PoolBalls.ONE, PoolBalls.TWO, PoolBalls.THREE,
                PoolBalls.FOUR, PoolBalls.FIVE, PoolBalls.SIX, PoolBalls.SEVEN
        ), new ArrayList<>(), "player1");
        game.play(action);

        action = new GameAction(3, List.of(PoolBalls.EIGHT), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM1, game.winDetection());
    }

    @Test
    void testWinDetectionFullAllPocketedAndEightInForTeam2() {
        // Simulate all full balls and the eight ball pocketed for Team 2
        GameAction action = new GameAction(1, List.of(
                PoolBalls.ONE, PoolBalls.TWO, PoolBalls.THREE,
                PoolBalls.FOUR, PoolBalls.FIVE, PoolBalls.SIX, PoolBalls.SEVEN
        ), new ArrayList<>(), "player2");
        game.play(action);
        action = new GameAction(2, List.of(PoolBalls.EIGHT), new ArrayList<>(), "player2");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }


    @Test
    void testWinDetection_classicGame() {
        GameAction action = new GameAction(1, List.of(PoolBalls.ONE), new ArrayList<>(), "player1");
        game.play(action);
        action = new GameAction(2, List.of(PoolBalls.NINE), new ArrayList<>(), "player2");
        game.play(action);
        action = new GameAction(3, List.of(PoolBalls.FIFTEEN), List.of(PoolFault.NOT_GOOD_FAMILY), "player3");
        game.play(action);
        action = new GameAction(4, new ArrayList<>(), new ArrayList<>(), "player4");
        game.play(action);
        action = new GameAction(5, List.of(PoolBalls.TWO, PoolBalls.THREE, PoolBalls.FOUR, PoolBalls.FIVE), new ArrayList<>(), "player1");
        game.play(action);
        action = new GameAction(6, new ArrayList<>(), new ArrayList<>(), "player2");
        game.play(action);
        action = new GameAction(7, List.of(PoolBalls.SIX, PoolBalls.SEVEN, PoolBalls.EIGHT), new ArrayList<>(), "player3");
        game.play(action);
        assertEquals(PoolVictoryState.TEAM1, game.winDetection());
    }

    @Test
    void testWinDetectionEightBallInWithFault() {
        // Prepare the game state to simulate an eight ball in with a fault
        GameAction action = new GameAction(1, List.of(PoolBalls.EIGHT), List.of(PoolFault.WHITE_IN), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }

    @Test
    void testWinDetectionEightBallInWithFaults() {
        // Prepare the game state to simulate an eight ball in with multiple faults
        GameAction action = new GameAction(1, List.of(PoolBalls.EIGHT), List.of(PoolFault.WHITE_IN, PoolFault.NOT_GOOD_FAMILY), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }

    @Test
    void testWinDetectionEightBallNoCall() {
        // Prepare the game state to simulate an eight ball pocketed without calling it
        GameAction action = new GameAction(1, List.of(PoolBalls.EIGHT), List.of(PoolFault.EIGHT_NO_CALL), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }

    @Test
    void testWinDetectionEightBallOut() {
        // Prepare the game state to simulate an eight ball out fault
        GameAction action = new GameAction(1, new ArrayList<>(), List.of(PoolFault.EIGHT_OUT), "player1");
        game.play(action);

        assertEquals(PoolVictoryState.TEAM2, game.winDetection());
    }

    @Test
    void testIsEightIn() {
        GameAction action = new GameAction(1, List.of(PoolBalls.EIGHT), new ArrayList<>(), "player1");
        game.play(action);

        assertTrue(game.isEightIn());
    }

    @Test
    void testIsEightNotIn() {
        GameAction action = new GameAction(1, List.of(PoolBalls.ONE), new ArrayList<>(), "player1");
        game.play(action);

        assertFalse(game.isEightIn());
    }

    @Test
    void testGetStrippedCount() {
        GameAction action = new GameAction(1, List.of(PoolBalls.NINE, PoolBalls.TEN), new ArrayList<>(), "player1");
        game.play(action);

        assertEquals(2, game.getStrippedCount());
    }

    @Test
    void testGetFullCount() {
        GameAction action = new GameAction(1, List.of(PoolBalls.ONE, PoolBalls.TWO), new ArrayList<>(), "player1");
        game.getHistory().add(action);

        assertEquals(2, game.getFullCount());
    }
}