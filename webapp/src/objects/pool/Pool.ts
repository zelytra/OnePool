import {User} from "@/objects/User.ts";

export interface Pool {
  uuid: string
  gameOwner?: User
  players: User[]
  rules: GameRule | null
  state: GameState
  maxPlayerAmount?: number
  game: Game
}

export interface Game {
  startingTime?: number
  endingTime?: number
  history: GameAction[]
  userPlayingRound?: string // Who's turn is
  teams: PoolTeams
  paused?: boolean
}

export interface GameAction {
  balls: number[]
  faults: PoolFaults[]
  username: string
}

export enum PoolFaults {
  WHITE_IN,
  NO_BAND,
  WHITE_DOUBLE_CONTACT,
  BALL_OUT,
  PLAYER_NO_GROUND_TOUCH,
  PLAYER_MISTAKE,
  EIGHT_NO_CALL
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