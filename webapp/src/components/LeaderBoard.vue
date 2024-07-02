<template>
  <section class="leaderboard">
    <div class="header-wrapper">
      <AlertCard color="#44FBF0" v-if="user">
        <p>{{ t('leaderboard.self.ranking.line1') }} <strong>{{ user.position }} {{
            t('leaderboard.self.ranking.th')
          }}</strong>
          {{ t('leaderboard.self.ranking.line2') }}</p>
        <p>{{ t('leaderboard.self.ranking.line3') }} <strong>{{ user.pp }} {{
            t('leaderboard.table.column.point')
          }}</strong>
        </p>
      </AlertCard>
      <div class="filter-wrapper">

      </div>
    </div>
    <table>
      <thead>
      <tr>
        <th>{{ t('leaderboard.table.column.username') }}</th>
        <th>{{ t('leaderboard.table.column.point') }}</th>
        <th>{{ t('leaderboard.table.column.rank') }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="player in players.sort((a,b)=> a.position - b.position)">
        <td>{{ player.username }}</td>
        <td class="point">{{ formatNumberWithSpaces(player.pp) }}</td>
        <td :class="{'rank':true,first:player.position == 1,second:player.position == 2,third:player.position==3}">
          #{{ player.position }}
        </td>
      </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup lang="ts">
import {formatNumberWithSpaces, PlayerLeaderboard} from "@/objects/pool/Leaderboard.ts";
import {onMounted, ref} from "vue";
import AlertCard from "@/vue/templates/AlertCard.vue";
import {useI18n} from "vue-i18n";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AxiosResponse} from "axios";

const players = ref<PlayerLeaderboard[]>([])
const user = ref<PlayerLeaderboard | undefined>();

const {t} = useI18n()

onMounted(() => {
  new HTTPAxios("leaderboard/all").get().then((response: AxiosResponse) => {
    players.value = response.data;
    players.value.sort((a, b) => b.pp - a.pp).forEach((p, index) => {
      p.position = index + 1
    })
    console.log(players.value)
  })
  new HTTPAxios("leaderboard/self").get().then((response: AxiosResponse) => {
    user.value = response.data;
  })
})

</script>


<style scoped lang="scss">
.leaderboard {
  box-sizing: border-box;

  .header-wrapper {
    width: 90%;
    display: flex;
    flex-direction: column;
    gap: 30px;
    margin: 30px auto auto;
  }

  table {
    width: 100%;
    text-align: center;
    border-collapse: collapse;
    table-layout: fixed;

    thead {
      border-bottom: 2px var(--bar) solid;

      th {
        padding: 4px;
        color: var(--secondary-text)
      }
    }

    tbody {
      background: url("@/assets/icons/grid.svg") no-repeat;

      &:before {
        content: "@";
        display: block;
        line-height: 10px;
        width: 100%;
        text-indent: -99999px;
      }

      tr {
        height: 40px;

        td {
          &.point {
            color: var(--green);
          }

          &.rank {
            color: var(--secondary-text);

            &.first {
              color: var(--green);
            }

            &.second {
              color: var(--blue);
            }

            &.third {
              color: var(--dark-blue);
            }
          }
        }
      }
    }
  }
}
</style>