<template>
  <section class="friends">
    <div class="header-wrapper">
      <AlertCard color="#27A27A" @click="isResearchOpen=true">
        <div class="friends-action">
          <p>{{ t('friends.action.add') }}</p>
          <img src="@/assets/icons/add-friend.svg" alt="add friend button"/>
        </div>
      </AlertCard>
      <AlertCard color="#27A27A" v-for="invite of pendingInvitation" @click="acceptInvite(invite.authUsername)">
        <p><strong>{{ invite.username }}</strong> {{ t('friends.alert.invite.send') }}</p>
      </AlertCard>
    </div>
    <hr/>
    <h1>{{ t('friends.title') }}</h1>
    <div class="friends-list-wrapper">
      <FriendStatus v-for="friend in friends.sort((a,b)=>(b.online?1:0) - (a.online?1:0))" :friend="friend"/>
    </div>
    <FriendSearchModale v-model:is-open="isResearchOpen"/>
  </section>
</template>

<script setup lang="ts">
import {Friend, InviteStatus, User} from "@/objects/User.ts";
import {onMounted, ref} from "vue";
import AlertCard from "@/vue/templates/AlertCard.vue";
import {useI18n} from "vue-i18n";
import FriendStatus from "@/vue/friends/FriendStatus.vue";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AxiosResponse} from "axios";
import FriendSearchModale from "@/vue/modale/FriendSearchModale.vue";
import {useUserStore} from "@/objects/stores/UserStore.ts";
import {AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";

const {t} = useI18n()
const friends = ref<User[]>([])
const pendingInvitation = ref<User[]>([])
const isResearchOpen = ref<boolean>(false)
const currentUser = useUserStore();
const alert = useAlertStore();

onMounted(() => {
  loadFriendList()
})

function loadFriendList() {
  new HTTPAxios("friends/list").get().then((response: AxiosResponse) => {
    const friendsList: Friend[] = response.data;
    friends.value = []
    pendingInvitation.value = []
    for (let friend of friendsList) {
      if (friend.status != InviteStatus.ACCEPT) {
        if (!isRequester(friend)) {
          pendingInvitation.value.push(getFrienUser(friend))
        }
        continue;
      }

      friends.value.push(getFrienUser(friend))
    }
  })
}

// Extract the right user from the two
function getFrienUser(friend: Friend): User {
  if (friend.user1.authUsername == currentUser.user.authUsername) {
    return friend.user2;
  }
  return friend.user1;
}

function isRequester(friendship: Friend): boolean {
  return friendship.user1.authUsername == currentUser.user.authUsername;
}

function acceptInvite(username: string) {
  new HTTPAxios("friends/invite/accept/" + username).post().then(() => {
    alert.send({
      content: "",
      timeout: 1000,
      title: t('friends.alert.invite.accept'),
      type: AlertType.VALID
    })
    loadFriendList();
  })
}
</script>

<style scoped lang="scss">
.friends {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .friends-action {
    display: flex;
    align-items: center;
    gap: 19px;
    cursor: pointer;
  }

  .header-wrapper {
    width: 90%;
    display: flex;
    flex-direction: column;
    gap: 30px;
    margin: 30px auto auto;
  }

  .friends-list-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 90%;
    margin-top: 20px;
    gap: 10px;
    box-sizing: border-box;
  }


}
</style>