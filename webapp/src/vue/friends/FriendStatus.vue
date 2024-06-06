<template>
  <div class="friend-status-wrapper" :style="{
    '--gradient-start': friend.online ? '#27A27A' : '#E44545',
  }">
    <div class="side-content">
      <img v-if="friend.icon" :src="friend.icon" alt="user-icon"/>
      <p v-if="friend.username">{{ friend.username }}</p>
    </div>
    <div class="side-content">
      <span v-if="friend.online">{{ t('friends.status.online') }}</span>
      <span v-else>{{ t('friends.status.offline') }}</span>
      <span class="status-color"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import {PropType} from "vue";
import {User} from "@/objects/User.ts";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
defineProps({
  friend: {type: Object as PropType<User>, required: true}
})
</script>

<style scoped lang="scss">
.friend-status-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  padding: 12px 16px;
  position: relative;

  &:before {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 15px;
    border: 1px solid transparent;
    background: linear-gradient(110deg, var(--gradient-start), 10%, #222536) border-box;
    -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
  }

  .side-content {
    display: flex;
    align-items: center;
    gap: 12px;

    img{
      width: 33px;
      height: 33px;
      border-radius: 50%;
    }

    p{
      font-weight: 600;
    }

    span {
      color: var(--gradient-start);

      &.status-color {
        width: 11px;
        height: 11px;
        background: var(--gradient-start);
        border-radius: 50%;
      }
    }
  }
}
</style>