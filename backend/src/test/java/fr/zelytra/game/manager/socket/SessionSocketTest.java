package fr.zelytra.game.manager.socket;

import fr.zelytra.game.clients.PoolClient;
import fr.zelytra.game.security.SocketSecurityEntity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@QuarkusTest
class SessionSocketTest {

    private PoolClient poolClient;
    private URI uri;

    @TestHTTPResource("/sessions/sessionId")
    URI websocketEndpoint;

    @InjectMock
    PoolSocketService socketService;

    @BeforeEach
    void setup() throws URISyntaxException, DeploymentException, IOException {
        SocketSecurityEntity socketSecurity = new SocketSecurityEntity();
        //PoolParty poolParty = socketService.createParty("user1");
        this.uri = new URI("ws://" + websocketEndpoint.getHost() + ":" + websocketEndpoint.getPort() + "/sessions/" + socketSecurity.getKey() + "/");
        poolClient = new PoolClient();
        ContainerProvider.getWebSocketContainer().connectToServer(poolClient, uri);
    }

    @Test
    void createSession() {
    }
}