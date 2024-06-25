import {User} from "@/objects/User.ts";

export interface Pool {
  uuid: string
  gameOwner?: User
  players: User[]
  rules: GameRule | null
  state: GameState
  maxPlayerAmount?: number
  teams: PoolTeams
}

export interface PoolTeams {
  team1: string[]
  team2: string[]
}

export enum GameRule {
  AMERICAN_8 = "AMERICAN_8"
}

export enum GameState {
  SETUP = "SETUP",
  INVITE_PLAYER = "INVITE_PLAYER",
  TEAMING_PLAYERS = "TEAMING_PLAYERS",
  RUNNING = "RUNNING",
  END = "END"
}