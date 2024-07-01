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
    type: String as PropType<InviteStatus>,
    required: true
  }
})
</script>

<style scoped lang="scss">
.friend-invite-wrapper {
  padding: 16px;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  width: 100%;

  p{
    color: var(--primary-text);
    font-weight: 600;
  }

  &.send {
    background: linear-gradient(100deg, #4581DC, #222536);
    cursor: pointer;
  }

  &.pending {
    background: linear-gradient(100deg, #D25A34, #222536);
  }

  &.accepted {
    background: linear-gradient(100deg, #27A27A, #222536);
  }
}
</style>