export interface WebSocketMessage {
  messageType: WebSocketMessageType;
  data: any; // Anytype of data that backend can handle
}

export enum WebSocketMessageType {
  CREATE_POOL = "CREATE_POOL",
  CONNECT_TO_POOL = "CONNECT_TO_POOL",
  UPDATE_POOL_DATA = "UPDATE_POOL_DATA",
  SET_RULES = "SET_RULES",
  CHANGE_GAME_STATES = "CHANGE_GAME_STATES",
  INIT_NOTIFICATION = "INIT_NOTIFICATION",
  SEND_NOTIFICATION = "SEND_NOTIFICATION",
  UPDATE_TEAMS = "UPDATE_TEAMS"
}
