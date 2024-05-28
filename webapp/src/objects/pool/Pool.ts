export interface PoolHistory {
  date: Date,
  score: string
  duration: number
}

export interface Pool {
  date: Date
  duration: number
  players: PoolPlayerStats[]
  id:number
}

export interface PoolPlayerStats {
  score: number
  shot: number
  logs: PoolLog[]
}

export interface PoolLog {
  shotId: number
  faults: PoolFaults[]
  ballsIn: number[]
  duration:number
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