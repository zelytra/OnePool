import {defineStore} from "pinia";
import {PoolSocket} from "@/objects/pool/PoolSocket.ts";
import {GameState, Pool} from "@/objects/pool/Pool.ts";

export const usePoolParty =
  defineStore('poolparty', () => {
    const poolSocket: PoolSocket = new PoolSocket()
    let pool: Pool = {
      players: [],
      rules: null,
      state: GameState.SETUP
    }
    return {
      poolSocket,
      pool
    };
  });
