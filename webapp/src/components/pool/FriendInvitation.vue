<template>
  <h1>{{ t('pool.friend.selection') }}</h1>
  <div class="friend-invitation-wrapper">
    <h2>Test</h2>
    <div class="send-invite-wrapper">
      <FriendPoolInvite v-for="friend of friends" :status="InviteStatus.SEND" :friend="friend"/>
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

const {t} = useI18n();
const friends = ref<User[]>([])
const currentUser = useUserStore();
//const poolStore = usePoolParty();

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

      friends.value.push(getFriendUser(friend))
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
</style>