package fr.zelytra.game.manager.message;

public enum MessageType {
    //Pool message types
    CREATE_POOL, // When a player create a pool party
    CONNECT_TO_POOL, // When a player try to join a pool party
    UPDATE_POOL_DATA, // When the party need to be broadcast to all player in the party
    SET_RULES,
    CHANGE_GAME_STATES,
    UPDATE_TEAMS,
    UPDATE_GAME_ACTION,
    PLAY_GAME_ACTION,

    //Notification message types
    INIT_NOTIFICATION,
    SEND_NOTIFICATION
}
