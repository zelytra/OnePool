<template>
  <section>
    <h1>{{ t('pool.rules.selection') }}</h1>
    <div class="button-section">
      <ButtonCard color="#D25A34" @click="randomizeTeams">
        <div class="button-content main">
          <img src="@/assets/icons/diversity.svg" alt="random teaming"/>
          <p style="color: #D25A34">Randomisé</p>
        </div>
      </ButtonCard>
      <ButtonCard color="#44A03B" width="50%">
        <div class="button-content">
          <p style="color: #44A03B">Classées</p>
        </div>
      </ButtonCard>
    </div>
    <div class="player-list">
      <PlayerCard v-for="user in noTeamList" :key="user.username"
                  draggable="true"
                  @dragstart="startDrag($event, user)"
                  @touchstart="startTouch($event, user)"
                  style="touch-action: none">
        {{ user.authUsername }}
      </PlayerCard>
    </div>
    <hr/>
    <div class="player-selector">
      <div class="team">
        <h3>Equipe 1</h3>
        <GamePlayerSlot v-for="(player, index) in team1WithEmptySlots" :key="'team1-' + index"
                        draggable="true"
                        @dragstart="startDragFromSlot($event, player)"
                        @drop="onDrop($event, 1, index)"
                        @dragover="allowDrop($event)"
                        @touchstart="startTouchFromSlot($event, player)"
                        @touchend="onTouchEnd($event, 1, index)" style="touch-action: none">
          <PlayerCard v-if="player.username" color="#EE9E27">
            {{ player.username }}
          </PlayerCard>
        </GamePlayerSlot>
      </div>
      <div class="team">
        <h3>Equipe 2</h3>
        <GamePlayerSlot v-for="(player, index) in team2WithEmptySlots" :key="'team2-' + index"
                        draggable="true"
                        @dragstart="startDragFromSlot($event, player)"
                        @drop="onDrop($event, 2, index)"
                        @dragover="allowDrop($event)"
                        @touchstart="startTouchFromSlot($event, player)"
                        @touchend="onTouchEnd($event, 2, index)" style="touch-action: none">
          <PlayerCard v-if="player.username" color="#EE2727">
            {{ player.username }}
          </PlayerCard>
        </GamePlayerSlot>
      </div>
    </div>
    <AlertCard color="#27A27A" @click="poolStore.poolSocket.setGameStatus(GameState.RUNNING)">
      <p class="button-title">{{ t('pool.action.continue') }}</p>
    </AlertCard>
  </section>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import ButtonCard from "@/vue/templates/ButtonCard.vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import PlayerCard from "@/vue/templates/PlayerCard.vue";
import GamePlayerSlot from "@/vue/templates/GamePlayerSlot.vue";
import {User} from "@/objects/User.ts";
import {computed, reactive, watch} from "vue";
import 'drag-drop-touch';
import {GameState, PoolTeams} from "@/objects/pool/Pool.ts";
import AlertCard from "@/vue/templates/AlertCard.vue";

const {t} = useI18n();
const poolStore = usePoolParty();
const noTeamList = computed(() => poolStore.pool.players.filter(x => !poolStore.pool.teams.team1.includes(x.authUsername) && !poolStore.pool.teams.team2.includes(x.authUsername)));

const team1 = computed(() => poolStore.pool.players.filter(x => poolStore.pool.teams.team1.includes(x.authUsername)));
const team2 = computed(() => poolStore.pool.players.filter(x => poolStore.pool.teams.team2.includes(x.authUsername)));

const team1WithEmptySlots = computed(() => createTeamWithEmptySlots(team1.value));
const team2WithEmptySlots = computed(() => createTeamWithEmptySlots(team2.value));

let touchedPlayer: User | null;

// Reactive state to track the frontend team assignments
const frontendTeams = reactive<PoolTeams>({
  team1: [],
  team2: []
});

watch(() => poolStore.pool.teams, (teams) => {
  frontendTeams.team1 = [...teams.team1];
  frontendTeams.team2 = [...teams.team2];
}, {immediate: true, deep: true});

function startDrag(evt: DragEvent, user: User) {
  evt.dataTransfer!.dropEffect = 'move';
  evt.dataTransfer!.effectAllowed = 'move';
  evt.dataTransfer!.setData("user", JSON.stringify(user));
}

function startDragFromSlot(evt: DragEvent, player: { username: string }) {
  evt.dataTransfer!.dropEffect = 'move';
  evt.dataTransfer!.effectAllowed = 'move';
  evt.dataTransfer!.setData("playerFromSlot", JSON.stringify(player));
}

function startTouch(_evt: TouchEvent, user: User) {
  touchedPlayer = user;
}

function startTouchFromSlot(_evt: TouchEvent, player: User) {
  touchedPlayer = player;
}

function allowDrop(evt: DragEvent) {
  evt.preventDefault();
}

function onDrop(evt: DragEvent, teamId: number, slotIndex: number) {
  evt.preventDefault();
  const userJson = evt.dataTransfer!.getData('user');
  const playerFromSlotJson = evt.dataTransfer!.getData('playerFromSlot');
  handleDrop(userJson, playerFromSlotJson, teamId, slotIndex);
}

function onTouchEnd(_evt: TouchEvent, teamId: number, slotIndex: number) {
  if (touchedPlayer) {
    const playerJson = JSON.stringify(touchedPlayer);
    handleDrop(playerJson, null, teamId, slotIndex);
    touchedPlayer = null;
  }
}

function handleDrop(userJson: string | null, playerFromSlotJson: string | null, teamId: number, slotIndex: number) {
  if (userJson) {
    const user: User = JSON.parse(userJson);
    updateTeamAssignment(user.authUsername, teamId, slotIndex);
  } else if (playerFromSlotJson) {
    const playerFromSlot: User = JSON.parse(playerFromSlotJson);
    updateTeamAssignment(playerFromSlot.authUsername, teamId, slotIndex);
  }
}

function updateTeamAssignment(username: string, teamId: number, slotIndex: number) {
  // Remove from current teams
  frontendTeams.team1 = frontendTeams.team1.filter(user => user !== username);
  frontendTeams.team2 = frontendTeams.team2.filter(user => user !== username);

  // Add to the new team
  if (teamId === 1) {
    frontendTeams.team1.splice(slotIndex, 0, username);
  } else if (teamId === 2) {
    frontendTeams.team2.splice(slotIndex, 0, username);
  }

  // Emit the changes to the backend
  const teams: PoolTeams = {
    team1: [...frontendTeams.team1],
    team2: [...frontendTeams.team2]
  };
  poolStore.poolSocket.setPlayersTeam(teams);
}

function createTeamWithEmptySlots(team: User[]) {
  return Array.from({length: 3}, (_, index) => team[index] || {username: ''} as User);
}

function randomizeTeams() {
  const players = poolStore.pool.players.map(player => player.authUsername);
  const shuffledPlayers = players.sort(() => Math.random() - 0.5);

  const midIndex = Math.ceil(shuffledPlayers.length / 2);
  const team1Players = shuffledPlayers.slice(0, midIndex);
  const team2Players = shuffledPlayers.slice(midIndex);

  const teams: PoolTeams = {
    team1: team1Players,
    team2: team2Players
  };
  poolStore.poolSocket.setPlayersTeam(teams);
}

watch(
    () => [frontendTeams.team1, frontendTeams.team2],
    ([newTeam1, newTeam2], [oldTeam1, oldTeam2]) => {
      const teamsChanged = JSON.stringify(newTeam1) !== JSON.stringify(oldTeam1) || JSON.stringify(newTeam2) !== JSON.stringify(oldTeam2);

      if (teamsChanged) {
        const teams: PoolTeams = {
          team1: newTeam1,
          team2: newTeam2
        };
        poolStore.poolSocket.setPlayersTeam(teams);
      }
    },
    {deep: true}
);
</script>


<style scoped lang="scss">
section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

h1 {
  text-align: center;
}

.button-section {
  display: flex;
  align-items: center;
  gap: 9px;

  .button-content {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 14px;

    img {
      width: 25px;
      height: 25px;
    }
  }

  p {
    font-size: 18px;
    font-weight: 700;
  }
}

.player-list {
  min-height: 94px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
  overflow: hidden;
  overflow-x: auto;
}

.player-selector {
  display: flex;
  width: 100%;
  justify-content: space-around;

  .team {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
}

p.button-title {
  color: var(--primary);
  font-weight: 800;
  font-size: 25px;
  cursor: pointer;
}
</style>
