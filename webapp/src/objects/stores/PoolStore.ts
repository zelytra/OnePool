import {defineStore} from "pinia";
import {PoolSocket} from "@/objects/pool/PoolSocket.ts";
import {GameState, Pool, PoolVictoryState} from "@/objects/pool/Pool.ts";

export const usePoolParty =
  defineStore('poolparty', () => {
    const poolSocket: PoolSocket = new PoolSocket()
    let pool: Pool = getRawPool();

    function getRawPool():Pool {
     return  {
        uuid: "",
        players: [],
        rules: null,
        state: GameState.SETUP,
        game: {
          victoryState: PoolVictoryState.NONE,
          history: [],
          currentAction: {
            roundId: 0,
            balls: [],
            faults: [],
            username: ""
          },
          teams: {
            team1: [],
            team2: []
          }
        }
      }
    }

    return {
      poolSocket,
      pool,
      getRawPool
    };
  });
