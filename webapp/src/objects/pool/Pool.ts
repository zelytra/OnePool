import {User} from "@/objects/User.ts";
import foot from "@/assets/icons/foot.svg"
import tennis from "@/assets/icons/tennis.svg"
import voiceHover from "@/assets/icons/voice-hover.svg"
import air from "@/assets/icons/air.svg"
import personAlert from "@/assets/icons/person-alert.svg"
import changeHistory from "@/assets/icons/change-history.svg"
import bottomRightClick from "@/assets/icons/bottom-right-click.svg"
import crisisAlert from "@/assets/icons/crisis-alert.svg"
import whereToVote from "@/assets/icons/where-to-vote.svg"
import {usePoolParty} from "@/objects/stores/PoolStore.ts";

export interface Pool {
  uuid: string
  gameOwner?: User
  players: User[]
  rules: GameRule | null
  state: GameState
  maxPlayerAmount?: number
  game: Game
  gameReport?: GameReport
}

export interface Game {
  startingTime?: number
  endingTime?: number
  history: GameAction[]
  currentAction: GameAction
  teams: PoolTeams
  paused?: boolean
  victoryState: PoolVictoryState
}

export interface GameAction {
  roundId: number
  balls: number[]
  faults: PoolFault[]
  username: string
}

export interface PoolTeams {
  team1: string[]
  team2: string[]
}

export interface GameReport {
  victoryPlayer: GameReportPlayer[]
  looserPlayer: GameReportPlayer[]
}

export interface GameReportPlayer {
  previousPP: number
  pp: number
  username: string
}

export interface PoolSilentJoin{
  username:string
  sessionId:string
}

export enum GameRule {
  AMERICAN_8 = "AMERICAN_8",
  MANUAL = "MANUAL"
}

export enum PoolVictoryState {
  NONE = "NONE",
  TEAM1 = "TEAM1",
  TEAM2 = "TEAM2"
}

export enum GameState {
  SETUP = "SETUP",
  INVITE_PLAYER = "INVITE_PLAYER",
  TEAMING_PLAYERS = "TEAMING_PLAYERS",
  RUNNING = "RUNNING",
  END = "END"
}

export enum PoolFault {
  WHITE_IN = "WHITE_IN",
  NO_BAND = "NO_BAND",
  WHITE_DOUBLE_CONTACT = "WHITE_DOUBLE_CONTACT",
  BALL_OUT = "BALL_OUT",
  PLAYER_NO_GROUND_TOUCH = "PLAYER_NO_GROUND_TOUCH",
  PLAYER_MISTAKE = "PLAYER_MISTAKE",
  EIGHT_NO_CALL = "EIGHT_NO_CALL",
  NOT_GOOD_FAMILY = "NOT_GOOD_FAMILY",
  EIGHT_OUT = "EIGHT_OUT"
}

export function getFaultIcon(fault: PoolFault) {
  switch (fault) {
    case PoolFault.PLAYER_NO_GROUND_TOUCH:
      return foot;
    case PoolFault.BALL_OUT:
    case PoolFault.EIGHT_OUT:
      return tennis;
    case PoolFault.EIGHT_NO_CALL:
      return voiceHover;
    case PoolFault.NO_BAND:
      return air;
    case PoolFault.PLAYER_MISTAKE:
      return personAlert;
    case PoolFault.WHITE_DOUBLE_CONTACT:
      return changeHistory;
    case PoolFault.WHITE_IN:
      return bottomRightClick;
    case PoolFault.NOT_GOOD_FAMILY:
      return whereToVote;
    default:
      return crisisAlert;

  }
}

export function getScoreTeam(teamNumber: number): number {
  let score = 0;
  for (let action of usePoolParty().pool.game.history) {
    const teams: PoolTeams = usePoolParty().pool.game.teams;
    if (teamNumber == 1 && teams.team1.includes(action.username)) {
      score += action.balls.length
    } else if (teamNumber == 2 && teams.team2.includes(action.username)) {
      score += action.balls.length
    }
  }
  return score;
}