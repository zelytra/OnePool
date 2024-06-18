import {defineStore} from "pinia";
import {PoolSocket} from "@/objects/pool/PoolSocket.ts";
import {Pool} from "@/objects/pool/Pool.ts";

export const usePoolParty =
  defineStore('poolparty', () => {
    const poolSocket: PoolSocket = new PoolSocket()
    let pool: Pool
    return {
      poolSocket,
      pool
    };
  });
