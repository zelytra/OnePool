import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";
import {tsi18n} from "@/objects/i18n";
import {WebSocketMessage, WebSocketMessageType} from "@/objects/pool/WebSocet.ts";
import {useUserStore} from "@/objects/stores/UserStore.ts";

const {t} = tsi18n.global;

export class PoolSocket {

  public sessionId: string;
  public socket?: WebSocket;
  public userStore = useUserStore()

  constructor() {
    this.sessionId = "";
  }

  async joinSession(sessionId: string) {
    if (this.socket && this.socket.readyState >= 2) {
      this.socket.close();
    }

    await new HTTPAxios("socket/register").get().then((response) => {
      this.socket = new WebSocket(
        import.meta.env.VITE_BACKEND_HOST + "/sessions/" + response.data + "/" + sessionId);
    }).catch(() => {
      useAlertStore().send({
        content: t('alert.websocketAuthFailed.content'),
        title: t('alert.websocketAuthFailed.title'),
        type: AlertType.ERROR
      })
    })

    if (!this.socket) return;

    // Send player data to backend for initialization
    this.socket.onopen = () => {
      if (!this.socket) return;
      const message: WebSocketMessage = {
        data: this.userStore.user.authUsername,
        messageType: WebSocketMessageType.CONNECT_TO_POOL,
      };
      this.socket.send(JSON.stringify(message));
    };

    this.socket.onmessage = (ev: MessageEvent<string>) => {
      const message: WebSocketMessage = JSON.parse(ev.data) as WebSocketMessage;
      switch (message.messageType) {
        default: {
          throw new Error(
            "Failed to handle this message type : " + message.messageType,
          );
        }
      }
    };

    this.socket.onerror = () => {
      useAlertStore().send({
        content: t("alert.socket.connectionFailed"),
        title: t("alert.socket.title"),
        type: AlertType.ERROR,
      });
    };

    this.socket.onclose = () => {
    }
  }
}