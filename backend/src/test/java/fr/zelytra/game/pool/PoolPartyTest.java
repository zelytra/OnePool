package fr.zelytra.game.pool;

import fr.zelytra.game.manager.socket.PoolSocketService;
import fr.zelytra.game.pool.data.GameRules;
import fr.zelytra.game.pool.data.GameStatus;
import fr.zelytra.game.pool.data.PoolTeam;
import fr.zelytra.game.pool.game.customs.AmericanEightPoolGame;
import fr.zelytra.user.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
public class PoolPartyTest {

    @InjectMocks
    PoolSocketService poolSocketService;

    UserEntity userEntity;

    PoolPlayer poolPlayer;

    @InjectMocks
    PoolParty poolParty;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        poolPlayer = mock(PoolPlayer.class);
        when(poolPlayer.getAuthUsername()).thenReturn("test_user");
        poolParty = new PoolParty(poolPlayer);
    }

    @Test
    public void testGetPlayers() {
        List<PoolPlayer> players = poolParty.getPlayers();
        assertNotNull(players);
        assertEquals(1, players.size());
        assertEquals(poolPlayer, players.getFirst());
    }

    @Test
    public void testGetGameOwner() {
        assertEquals(poolPlayer, poolParty.getGameOwner());
    }

    @Test
    public void testSetGameOwner() {
        PoolPlayer newOwner = mock(PoolPlayer.class);
        poolParty.setGameOwner(newOwner);
        assertEquals(newOwner, poolParty.getGameOwner());
    }

    @Test
    public void testSetRules() {
        poolParty.setRules(GameRules.AMERICAN_8);
        assertEquals(GameRules.AMERICAN_8, poolParty.getRules());
        assertInstanceOf(AmericanEightPoolGame.class, poolParty.getGame());
    }

    @Test
    public void testSetCurrentAction() {
        /*
        GameAction action = mock(GameAction.class);
        poolParty.setRules(GameRules.AMERICAN_8);
        poolParty.setCurrentAction(action);
        assertEquals(action, poolParty.getGame().getCurrentAction());
         */
    }

    @Test
    public void testSetTeams() {
        String player1 = "player1";
        String player2 = "player2";
        List<String> team1 = List.of(player1);
        List<String> team2 = List.of(player2);
        PoolTeam teams = new PoolTeam(team1, team2);

        poolParty.setRules(GameRules.AMERICAN_8);
        boolean result = poolParty.setTeams(teams);
        assertTrue(result);
        assertEquals(team1, poolParty.getGame().getTeams().team1());
        assertEquals(team2, poolParty.getGame().getTeams().team2());
    }

    @Test
    public void testSetState() {
        poolParty.setRules(GameRules.AMERICAN_8);
        poolParty.setState(GameStatus.TEAMING_PLAYERS);
        assertEquals(GameStatus.TEAMING_PLAYERS, poolParty.getState());
    }

    @Test
    public void testRemovePlayer() {
        /*
        UserEntity user = mock(UserEntity.class);
        Session session = mock(Session.class);
        when(user.getAuthUsername()).thenReturn("user1");
        PoolPlayer playerToRemove = new PoolPlayer(user,session);

        poolParty.getPlayers().add(playerToRemove);
        boolean removed = poolParty.removePlayer(user);
        assertTrue(removed);
        assertFalse(poolParty.getPlayers().contains(playerToRemove));
         */
    }

    @Test
    @Transactional
    public void testGetGameReport() {
        /*
        PoolVictoryState victoryState = PoolVictoryState.TEAM1;
        poolParty.setRules(GameRules.AMERICAN_8);

        PoolPlayer player1 = mock(PoolPlayer.class);
        PoolPlayer player2 = mock(PoolPlayer.class);
        when(player1.getAuthUsername()).thenReturn("player1");
        when(player2.getAuthUsername()).thenReturn("player2");

        poolParty.getPlayers().add(player1);
        poolParty.getPlayers().add(player2);

        GameReport report = poolParty.winHandler(victoryState);
        assertNotNull(report);
        assertEquals(GameStatus.END, poolParty.getState());
        */
    }
}