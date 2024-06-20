package fr.zelytra.game.manager.socket;

import fr.zelytra.game.pool.PoolParty;
import fr.zelytra.game.pool.PoolPlayer;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import jakarta.websocket.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class PoolSocketServiceTest {

    @InjectMocks
    PoolSocketService socketService;

    @InjectMock
    UserService userService;

    private Session session;
    private Session session2;

    @BeforeEach
    @Transactional
    void init() {
        MockitoAnnotations.openMocks(this);
        session = Mockito.mock(Session.class);
        session2 = Mockito.mock(Session.class);
    }

    @Test
    void createParty() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        PoolParty poolParty = socketService.createParty("user1",session);
        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(1, games.size());
        assertEquals(1, poolParty.getPlayers().size());
        assertNotNull(poolParty.getGameOwner());
    }

    @Test
    void createParty_unknownUser() {
        socketService.createParty("user1",session);
        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(0, games.size());
    }

    @Test
    void joinPool() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        when(userService.getUserByName("user2")).thenReturn(new UserEntity("user2"));
        PoolParty poolParty = socketService.createParty("user1",session);
        socketService.joinPool("user2", poolParty.getUuid(),session2);

        assertEquals(2, poolParty.getPlayers().size());
    }

    @Test
    void joinPool_emptySessionId() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        when(userService.getUserByName("user2")).thenReturn(new UserEntity("user2"));
        socketService.joinPool("user1", "",session);
        socketService.joinPool("user2", " ",session2);

        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(2, games.size());
    }

    @Test
    void joinPool_sameUserConnectToAnotherSession() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        socketService.joinPool("user1", "",session);
        socketService.joinPool("user1", "",session);

        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(1, games.size());
    }

    @Test
    void joinPool_unknownUser() {
        socketService.joinPool("user3", "",session);
        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(0, games.size());
    }

    @Test
    void joinPool_unknownPoolId() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        when(userService.getUserByName("user2")).thenReturn(new UserEntity("user2"));
        PoolParty poolParty = socketService.createParty("user1",session);
        socketService.joinPool("user2", "ABCDEF",session2);

        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(1, games.size());
        assertEquals(1, poolParty.getPlayers().size());
    }

    @Test
    void getPoolPartyByPlayer() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        PoolParty poolParty = socketService.createParty("user1",session);

        PoolParty retrieveParty = socketService.getPoolPartyByPlayer("user1");

        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(1, games.size());

        assertNotNull(retrieveParty);
        assertEquals(poolParty, retrieveParty);
    }

    @Test
    void getPoolPartyByPlayer_unknownUser() {
        PoolParty retrieveParty = socketService.getPoolPartyByPlayer("user1");
        assertNull(retrieveParty);
    }

    @Test
    void getPlayerFromPool() {
        UserEntity user1 = new UserEntity("user1");
        //when(session).thenReturn(Mockito);
        PoolPlayer poolPlayer = new PoolPlayer(user1,session);

        when(userService.getUserByName("user1")).thenReturn(user1);
        socketService.createParty("user1",poolPlayer.getSocketSession());

        PoolPlayer user = socketService.getPlayerFromPool("user1");

        assertNotNull(user);
        assertEquals(poolPlayer.getSocketSession().getId(), user.getSocketSession().getId());
    }

    @Test
    void getPlayerFromPool_unknownUser() {
        UserEntity user = socketService.getPlayerFromPool("user1");
        assertNull(user);
    }

    @Test
    void isPlayerInPool() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        socketService.createParty("user1",session);
        assertTrue(socketService.isPlayerInPool("user1"));
    }

    @Test
    void removePlayerFromPool() {
        UserEntity user2 = new UserEntity("user2");
        PoolPlayer poolPlayer = new PoolPlayer(user2,session2);

        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        when(userService.getUserByName("user2")).thenReturn(user2);
        PoolParty poolParty = socketService.createParty("user1",session);
        socketService.joinPool("user2", poolParty.getUuid(),poolPlayer.getSocketSession());


        boolean removed = socketService.removePlayerFromPool(poolPlayer, poolParty.getUuid());
        assertTrue(removed);
        assertEquals(1, poolParty.getPlayers().size());
    }

    @Test
    void removePlayerFromPool_poolDontExist() {
        UserEntity user2 = new UserEntity("user2");
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        when(userService.getUserByName("user2")).thenReturn(user2);
        PoolParty poolParty = socketService.createParty("user1",session);
        socketService.joinPool("user2", poolParty.getUuid(),session2);
        PoolPlayer poolPlayer = new PoolPlayer(user2,session2);

        boolean removed = socketService.removePlayerFromPool(poolPlayer, "ABCDEF");
        assertFalse(removed);
        assertEquals(2, poolParty.getPlayers().size());
    }

    @Test
    void removePlayerFromPool_userNotInPool() {
        UserEntity user2 = new UserEntity("user2");
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        when(userService.getUserByName("user2")).thenReturn(user2);
        PoolParty poolParty1 = socketService.createParty("user1",session);
        PoolParty poolParty2 = socketService.createParty("user2",session2);
        PoolPlayer poolPlayer = new PoolPlayer(user2,session2);

        boolean removed = socketService.removePlayerFromPool(poolPlayer, poolParty1.getUuid());
        assertFalse(removed);
        assertEquals(1, poolParty1.getPlayers().size());
        assertEquals(1, poolParty2.getPlayers().size());
    }

    @AfterEach
    @Transactional
    void cleanDataBase() {
        UserEntity.deleteAll();
    }
}