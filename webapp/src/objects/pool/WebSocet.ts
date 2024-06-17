export interface WebSocketMessage {
  messageType: WebSocketMessageType;
  data: any; // Anytype of data that backend can handle
}

export enum WebSocketMessageType {
  CREATE_POOL = "CREATE_POOL",
  CONNECT_TO_POOL = "CONNECT_TO_POOL",
  UPDATE_POOL_DATA = "UPDATE_POOL_DATA"
}
