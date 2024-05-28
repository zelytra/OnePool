import {PoolHistory} from "@/objects/pool/Pool.ts";

export interface Friend {
  icon: string
  username: string
  online: boolean
}

export interface User {
  name: string
  id: number
  creationDate: Date
  icon: string
  online: string
  pp: number
  history: PoolHistory[]
}
