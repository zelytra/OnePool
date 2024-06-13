package fr.zelytra.game.manager.socket;

import fr.zelytra.game.pool.PoolParty;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@QuarkusTest
class SocketServiceTest {

    @InjectMocks
    SocketService socketService;

    @InjectMock
    UserService userService;

    @BeforeEach
    @Transactional
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createParty() {
        when(userService.getUserByName("user1")).thenReturn(new UserEntity("user1"));
        socketService.createParty("user1");
        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(1, games.size());
    }

    @Test
    void createParty_unknownUSer() {
        socketService.createParty("user1");
        ConcurrentMap<String, PoolParty> games = socketService.getGames();
        assertEquals(0, games.size());
    }

    @AfterEach
    @Transactional
    void cleanDataBase() {
        UserEntity.deleteAll();
    }
}