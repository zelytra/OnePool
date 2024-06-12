package fr.zelytra.game.manager.message;

public record SocketMessage<T>(MessageType messageType, T data) {

    @Override
    public String toString() {
        return messageType().name() + " " + data.getClass();
    }
}
