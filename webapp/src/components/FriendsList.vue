<template>
  <section class="friends">
    <div class="header-wrapper">
      <AlertCard color="#27A27A">
        <div class="friends-action">
          <p>{{ t('friends.action.add') }}</p>
          <img src="@/assets/icons/add-friend.svg" alt="add friend button"/>
        </div>
      </AlertCard>
    </div>
    <hr/>
    <h1>{{ t('friends.title') }}</h1>
    <div class="friends-list-wrapper">
      <FriendStatus v-for="friend in friends.sort((a,b)=>(b.online?1:0) - (a.online?1:0))" :friend="friend"/>
    </div>
  </section>
</template>

<script setup lang="ts">
import {Friend} from "@/objects/User.ts";
import {onMounted, ref} from "vue";
import AlertCard from "@/vue/templates/AlertCard.vue";
import {useI18n} from "vue-i18n";
import FriendStatus from "@/vue/friends/FriendStatus.vue";
import logo from "@/assets/icons/logo-back.svg"

const {t} = useI18n()
const friends = ref<Friend[]>([])

onMounted(() => {
  for (let x = 1; x <= 8; x++) {
    friends.value.push({
      online: Math.round(Math.random() * 10000) % 2 == 1,
      icon: logo,
      username: "Zelytra"
    })
  }
})
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