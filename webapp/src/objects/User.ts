export interface Friend {
  user1: User
  user2: User
  status: InviteStatus
}

export interface CustomUserMetadata {
  gameInviteStatus?: InviteStatus,
  teamId: number
  slotIndex?:number
}

export interface User extends CustomUserMetadata {
  authUsername: string
  username: string
  icon: string
  online: boolean
  createdAt: Date
  pp: number
  lang: string,

//history: PoolHistory[],
}

export interface SimpleUser {
  icon: string
  username: string
  authUsername: string
}

export enum InviteStatus {
  ACCEPT = "ACCEPT",
  SEND = "SEND",
  REFUSE = "REFUSE",
  PENDING = "PENDING"
}
