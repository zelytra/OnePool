<template>
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
    <PlayerCard v-for="user of noTeamList" :key="user.username"
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
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import ButtonCard from "@/vue/templates/ButtonCard.vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import PlayerCard from "@/vue/templates/PlayerCard.vue";
import GamePlayerSlot from "@/vue/templates/GamePlayerSlot.vue";
import {User} from "@/objects/User.ts";
import {computed} from "vue";

const {t} = useI18n();
const pool = usePoolParty();

let touchedPlayer: User | null = null;

function startDrag(evt: DragEvent, user: User) {
  evt.dataTransfer!.dropEffect = 'move';
  evt.dataTransfer!.effectAllowed = 'move';
  evt.dataTransfer!.setData("user", JSON.stringify(user));
}

function startDragFromSlot(evt: DragEvent, player: User) {
  evt.dataTransfer!.dropEffect = 'move';
  evt.dataTransfer!.effectAllowed = 'move';
  evt.dataTransfer!.setData("playerFromSlot", JSON.stringify(player));
}

function startTouch(evt: TouchEvent, user: User) {
  touchedPlayer = user;
}

function startTouchFromSlot(evt: TouchEvent, player: User) {
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
    const player = pool.pool.players.find((x) => x.authUsername == user.authUsername)!;
    const targetPlayer = pool.pool.players.find(x => x.teamId === teamId && x.slotIndex === slotIndex);

    if (targetPlayer) {
      const tempTeamId = targetPlayer.teamId;
      const tempSlotIndex = targetPlayer.slotIndex;
      targetPlayer.teamId = player.teamId;
      targetPlayer.slotIndex = player.slotIndex;
      player.teamId = tempTeamId;
      player.slotIndex = tempSlotIndex;
    } else {
      player.teamId = teamId;
      player.slotIndex = slotIndex;
    }
  } else if (playerFromSlotJson) {
    const playerFromSlot: User = JSON.parse(playerFromSlotJson);
    const player = pool.pool.players.find((x) => x.authUsername == playerFromSlot.authUsername)!;
    const targetPlayer = pool.pool.players.find(x => x.teamId === teamId && x.slotIndex === slotIndex);

    if (targetPlayer) {
      const tempTeamId = targetPlayer.teamId;
      const tempSlotIndex = targetPlayer.slotIndex;
      targetPlayer.teamId = player.teamId;
      targetPlayer.slotIndex = player.slotIndex;
      player.teamId = tempTeamId;
      player.slotIndex = tempSlotIndex;
    } else {
      player.teamId = teamId;
      player.slotIndex = slotIndex;
    }
  }
}

const noTeamList = computed(() => pool.pool.players.filter(x => !x.teamId));

const team1 = computed(() => pool.pool.players.filter(x => x.teamId === 1));
const team2 = computed(() => pool.pool.players.filter(x => x.teamId === 2));

const createTeamWithEmptySlots = (team: User[]) => {
  const slots = Array.from({length: 3}, (_, index) => team.find(player => player.slotIndex === index) || {username: ''});
  return slots;
};

const team1WithEmptySlots = computed(() => createTeamWithEmptySlots(team1.value));
const team2WithEmptySlots = computed(() => createTeamWithEmptySlots(team2.value));

function randomizeTeams() {
  const players = pool.pool.players;
  const shuffledPlayers = players.sort(() => Math.random() - 0.5);

  // Assuming you want to split them into two teams
  const midIndex = Math.ceil(shuffledPlayers.length / 2);
  const team1Players = shuffledPlayers.slice(0, midIndex);
  const team2Players = shuffledPlayers.slice(midIndex);

  team1Players.forEach((player, index) => {
    player.teamId = 1;
    player.slotIndex = index;
  });

  team2Players.forEach((player, index) => {
    player.teamId = 2;
    player.slotIndex = index;
  });
}

</script>

<style scoped lang="scss">
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
</style>
