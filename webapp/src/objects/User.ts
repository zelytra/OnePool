export interface Friend {
  icon: string
  username: string
  online: boolean
}

export interface User {
  authUsername: string
  username: string
  icon: string
  online: boolean
  createdAt: Date
  pp: number
  lang: string
//history: PoolHistory[],
}

export interface SimpleUser {
  icon: string
  username: string
  authUsername: string
}
