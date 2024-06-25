import {ref} from "vue";
import {i18n} from "@/main.ts";
import {tsi18n} from "@/objects/i18n/index.ts"
import {User} from "@/objects/User.ts";
import {defineStore} from "pinia";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AxiosResponse} from "axios";
import {useNotification} from "@/objects/stores/NotificationStore.ts";


export const useUserStore = defineStore('user', () => {
  const user = ref<User>({
    authUsername: '',
    createdAt: new Date(),
    icon: '',
    lang: 'en',
    online: false,
    pp: 0,
    username: '',
  });
  const isUserInit = ref<boolean>(false);

  function init(username: string) {
    const browserLang = navigator.language.substring(0, 2);
    user.value = {
      authUsername: username,
      createdAt: new Date(),
      icon: '',
      lang: browserLang,
      online: false,
      pp: 0,
      username: username,
    };
    new HTTPAxios("user/preferences").get().then((response: AxiosResponse) => {
      user.value = {
        ...response.data,
        lang: browserLang,
      }
      useNotification().init(user.value.authUsername);
      isUserInit.value = true;
    })

    i18n.global.locale.value = user.value.lang as "fr" | "en" | "es" | "de" || "en";
    tsi18n.global.locale.value = user.value.lang as "fr" | "en" | "es" | "de" || "en";
  }

  function setUser(newUser: User) {
    user.value = newUser;
  }

  function setLang(lang: string) {
    user.value.lang = lang;
    i18n.global.locale.value = lang as "fr" | "en" | "es" | "de" || "en";
    tsi18n.global.locale.value = lang as "fr" | "en" | "es" | "de" || "en";
  }

  return {
    user,
    isUserInit,
    init,
    setUser,
    setLang,
  };
});
