package fr.zelytra.game.manager.message;

import io.quarkus.logging.Log;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.concurrent.*;

public class SocketTimeOutManager {

    private static final ConcurrentMap<String, Future<?>> sessionTimeoutTasks = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SocketTimeOutManager() {
    }

    public void initLogin(Session session) {
        // Start a timeout task
        Future<?> timeoutTask = executor.submit(() -> {
            try {
                // Wait for a certain period for the initial message
                TimeUnit.SECONDS.sleep(1); // 1 seconds timeout
                // If the initial message is not received, close the session
                Log.info("[" + session.getId() + "] Timeout reached. Closing session.");
                session.close();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        });
        sessionTimeoutTasks.put(session.getId(), timeoutTask);
    }

    public void completeLogin(String sessionId){
        // Cancel the timeout task since we've received the message
        Future<?> timeoutTask = sessionTimeoutTasks.remove(sessionId);
        if (timeoutTask != null) {
            timeoutTask.cancel(true);
        }
    }
}
