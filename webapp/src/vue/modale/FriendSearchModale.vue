<template>
  <div class="modal-wrapper" v-if="isOpen">
    <AlertCard color="" class="oskour" v-click-outside="()=>isOpen=false">
      <input type="text" v-model="inputResearch"/>
      <div class="results-wrapper">
        <AlertCard color="#44FBF0" v-for="user of searchResult" @click="inviteUser(user.authUsername)">
          <div class="user-wrapper">
            <img :src="user.icon" v-if="user.icon" alt="user-icon"/>
            <p>{{ user.username }}</p>
            <img src="@/assets/icons/add-friend.svg" alt="add friend button"/>
          </div>
        </AlertCard>
      </div>
    </AlertCard>
  </div>
</template>

<script setup lang="ts">
import AlertCard from "@/vue/templates/AlertCard.vue";
import {ref, watch} from "vue";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AxiosResponse} from "axios";
import {SimpleUser} from "@/objects/User";
import {AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";
import {useI18n} from "vue-i18n";
import {NotificationType, useNotification} from "@/objects/stores/NotificationStore.ts";

const inputResearch = ref<string>("");
const searchResult = ref<SimpleUser[]>([]);
const isOpen = defineModel<boolean>("isOpen")
const alert = useAlertStore();
const {t} = useI18n();
const notification = useNotification();

watch(() => inputResearch.value, () => {
  if (inputResearch.value.length == 0) return;
  new HTTPAxios("user/research/" + inputResearch.value).get().then((response: AxiosResponse) => {
    searchResult.value = response.data
  })
})

function inviteUser(username: string) {
  new HTTPAxios("friends/invite/send/" + username).post().then(() => {
    alert.send({
      content: "",
      timeout: 1000,
      title: t('friends.alert.invite.success.title'),
      type: AlertType.VALID
    })
    notification.send({
      users: [username],
      data: {
        data: null,
        type: NotificationType.INVITE_TO_FRIEND
      }
    })
    isOpen.value=false;
  })
}

</script>

<style scoped lang="scss">
.modal-wrapper {
  position: absolute;
  top: 0;
  width: 100vw;
  height: 100vh;
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5%;
  box-sizing: border-box;
  z-index: 9999;

  input {
    color: var(--primary);
    z-index: 99;
  }

  .results-wrapper {
    height: 400px;
    width: 100%;
    margin-top: 8px;
    box-sizing: border-box;
    z-index: 99;

    .user-wrapper {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
    }
  }


}
</style>