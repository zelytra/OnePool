import { createApp } from 'vue'
import App from "@/App.vue";
import "@/assets/style.scss"
import "@/assets/font.scss"
import "@/assets/text.scss"
import en from "@/assets/locales/en.json";
import fr from "@/assets/locales/fr.json";
import es from "@/assets/locales/es.json";
import de from "@/assets/locales/de.json";
import {createI18n} from "vue-i18n";
import router from "@/router";
import {createPinia} from "pinia";
import {keycloakStore} from "@/objects/stores/LoginStates.ts";

export const i18n = createI18n({
  legacy: false, // you must set `false`, to use Composition API
  locale: "fr", // set locale
  fallbackLocale: "en", // set fallback locale
  messages: {fr, en, es, de},
});

const app = createApp(App);
keycloakStore.init(window.location.origin);
app.directive("click-outside", {
  mounted(el, binding) {
    el.clickOutsideEvent = function (event: any) {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event, el);
      }
    };
    window.requestAnimationFrame(() => {
      document.body.addEventListener("click", el.clickOutsideEvent);
    });
  },
  unmounted(el) {
    document.body.removeEventListener("click", el.clickOutsideEvent);
  },
});
app.use(createPinia());
app.use(router);
app.use(i18n);
app.mount("#app");