import {User} from "@/objects/User.ts";

export interface Pool {
  gameOwner: User
  players: User[]
  rules: GameRule | null
  state: GameState
}

export enum GameRule {
  AMERICAN_8 = "AMERICAN_8"
}

export enum GameState {
  SETUP = "SETUP",
  TEAMING_PLAYERS = "TEAMING_PLAYERS",
  RUNNING = "RUNNING",
  END = "END"
}