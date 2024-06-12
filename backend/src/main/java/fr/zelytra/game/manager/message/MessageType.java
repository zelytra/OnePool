package fr.zelytra.game.manager.message;

public enum MessageType {
    CREATE_POOL, // When a player create a pool party
    CONNECT_TO_POOL, // When a player try to join a pool party
    UPDATE_POOL_DATA, // When the party need to be broadcast to all player in the party
}
