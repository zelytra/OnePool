<template>
  <div :class="{
    'friend-invite-wrapper':true,
     'send':status == InviteStatus.SEND,
     'pending':status == InviteStatus.PENDING,
     'accepted':status == InviteStatus.ACCEPT,
    }"
  >
    <p>{{ friend.username }}</p>
    <img v-if="status == InviteStatus.SEND" src="@/assets/icons/plus-circle.svg" alt="send"/>
    <img v-if="status == InviteStatus.PENDING" src="@/assets/icons/schedule-send.svg" alt="pending"/>
    <img v-if="status == InviteStatus.ACCEPT" src="@/assets/icons/task-alt.svg" alt="accepted"/>
  </div>
</template>

<script setup lang="ts">
import {PropType} from "vue";
import {InviteStatus, User} from "@/objects/User.ts";

defineProps({
  friend: {
    type: Object as PropType<User>,
    required: true
  },
  status: {
    type: Object as PropType<InviteStatus>,
    required: true
  }
})
</script>

<style scoped lang="scss">
.friend-invite-wrapper{
  padding: 16px;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
}
</style>