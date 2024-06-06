import {createRouter, createWebHistory, RouteMeta} from "vue-router";
import Home from "../components/Home.vue";
import UserProfile from "@/components/UserProfile.vue";
import FriendsList from "@/components/FriendsList.vue";
import LeaderBoard from "@/components/LeaderBoard.vue";
import PoolGame from "@/components/PoolGame.vue";
import pool from "@/assets/icons/pool.svg"
import leaderboard from "@/assets/icons/leaderboard.svg"
import friends from "@/assets/icons/friends.svg"
import profile from "@/assets/icons/profile.svg"
import {tsi18n} from "@/objects/i18n";

const {t} = tsi18n.global;

declare module 'vue-router' {
  interface RouteMeta {
    icon?: string,
    role?: string,
    tooltip?: string
    requiresAuth?: boolean
    displayInNav: boolean,
    subTitle?: string
  }
}

export const routes = [
  {
    path: "/",
    name: t('router.home'),
    component: Home,
    meta: {
      displayInNav: false
    } as RouteMeta
  },
  {
    path: "/pool",
    name: t('router.pool.title'),
    component: PoolGame,
    meta: {
      displayInNav: true,
      icon: pool,
      subTitle: t('router.pool.subtitle'),
    } as RouteMeta
  },
  {
    path: "/leaderboard",
    name: t('router.leaderboard.title'),
    component: LeaderBoard,
    meta: {
      displayInNav: true,
      icon: leaderboard,
      subTitle: t('router.leaderboard.subtitle'),
    } as RouteMeta
  },
  {
    path: "/friends",
    name: t('router.friends.title'),
    component: FriendsList,
    meta: {
      displayInNav: true,
      icon: friends,
      subTitle: t('router.friends.subtitle'),
    } as RouteMeta
  },
  {
    path: "/profile/:id",
    name: t('router.profile.title'),
    component: UserProfile,
    meta: {
      displayInNav: true,
      icon: profile,
      subTitle: t('router.profile.subtitle'),
    } as RouteMeta
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
