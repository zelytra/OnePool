<template>
  <h1>{{ t('pool.friend.selection') }}</h1>
  <div class="friend-wrapper">
    <h2>Vos amis</h2>
    <div class="invite-wrapper">
      <FriendPoolInvite v-for="friend of filterPlayer(friends)"
                        :status="friend.gameInviteStatus || InviteStatus.REFUSE"
                        :friend="friend" @click="inviteToGame(friend)"
      />
    </div>
  </div>
  <div class="friend-wrapper">
    <h2>Joueurs de la partie</h2>
    <div class="invite-wrapper">
      <FriendPoolInvite v-for="friend of poolStore.pool.players" :status="InviteStatus.ACCEPT" :friend="friend"/>
    </div>
  </div>
  <AlertCard color="#27A27A">
    <p class="button-title">{{ t('pool.action.continue') }}</p>
  </AlertCard>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import AlertCard from "@/vue/templates/AlertCard.vue";
import {onMounted, ref} from "vue";
import {Friend, InviteStatus, User} from "@/objects/User.ts";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AxiosResponse} from "axios";
import {useUserStore} from "@/objects/stores/UserStore.ts";
import FriendPoolInvite from "@/vue/friends/FriendPoolInvite.vue";
import {usePoolParty} from "@/objects/stores/PoolStore.ts";
import {NotificationType, useNotification} from "@/objects/stores/NotificationStore.ts";

const {t} = useI18n();
const friends = ref<User[]>([])
const currentUser = useUserStore();
const poolStore = usePoolParty();
const notification = useNotification();

onMounted(() => {
  loadFriendList()
})

function loadFriendList() {
  new HTTPAxios("friends/list").get().then((response: AxiosResponse) => {
    const friendsList: Friend[] = response.data;
    friends.value = []
    for (let friend of friendsList) {
      if (friend.status != InviteStatus.ACCEPT) {
        continue;
      }
      const user: User = getFriendUser(friend)
      user.gameInviteStatus = InviteStatus.SEND
      friends.value.push(user)
    }
  })
}

// Extract the right user from the two
function getFriendUser(friend: Friend): User {
  if (friend.user1.authUsername == currentUser.user.authUsername) {
    return friend.user2;
  }
  return friend.user1;
}

function inviteToGame(user: User) {
  notification.send({
    users: [user.authUsername],
    data: {
      data: poolStore.pool.uuid,
      type: NotificationType.INVITE_TO_GAME
    }
  });
  user.gameInviteStatus = InviteStatus.PENDING;
}

function filterPlayer(users: User[]): User[] {
  // Extract the usernames of players already in the game
  const playersInGame = new Set(poolStore.pool.players.map(player => player.authUsername));

  // Filter the users who are not already in the game
  return users.filter(user => !playersInGame.has(user.authUsername));
}
</script>

<style scoped lang="scss">
h1 {
  text-align: center;
}

p.button-title {
  color: var(--primary);
  font-weight: 800;
  font-size: 25px;
  cursor: pointer;
}

.friend-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  h2 {
    color: var(--primary);
    font-weight: 600;
    font-size: 20px;
  }


  .invite-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 172px;
    overflow-y: auto;
  }
}
</style>