package fr.zelytra.game.pool.game;

import fr.zelytra.game.pool.data.GameAction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PoolGameManagerTest {

    private PoolGameManager game;

    @BeforeEach
    void setUp() {
        game = new PoolGameManager();
        game.getTeams().team1().add("player1");
        game.getTeams().team1().add("player3");
        game.getTeams().team2().add("player2");
        game.getTeams().team2().add("player4");
        game.initGame();
    }

    @Test
    void getNextPlayer_nextPlayer_next() {
        GameAction action = new GameAction(1, List.of(), new ArrayList<>(), "player1");
        game.getHistory().add(action);
        assertEquals("player2", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player3", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player4", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player1", game.getNextPlayer());
    }

    @Test
    void getNextPlayer_nextPlayerEvenTeam_next() {
        game = new PoolGameManager();
        game.getTeams().team1().add("player1");
        game.getTeams().team1().add("player3");
        game.getTeams().team2().add("player2");
        game.initGame();

        GameAction action = new GameAction(1, List.of(), new ArrayList<>(), "player1");
        game.getHistory().add(action);
        assertEquals("player2", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player3", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player2", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player1", game.getNextPlayer());
        action = new GameAction(1, List.of(), new ArrayList<>(), game.getNextPlayer());
        game.getHistory().add(action);
        assertEquals("player2", game.getNextPlayer());
    }
}