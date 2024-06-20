import {defineStore} from "pinia";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";
import {WebSocketMessage, WebSocketMessageType} from "@/objects/pool/WebSocet.ts";
import {tsi18n} from "@/objects/i18n";

const {t} = tsi18n.global;

export const useNotification =
  defineStore('notifications', () => {
    let socket: WebSocket

    async function init(username: string) {
      if (socket && socket.readyState >= 2) {
        socket.close();
      }

      await new HTTPAxios("socket/register").get().then((response) => {
        socket = new WebSocket(
          import.meta.env.VITE_BACKEND_HOST + "/notifications/" + response.data);
      }).catch(() => {
        useAlertStore().send({
          content: t('alert.websocketAuthFailed.content'),
          title: t('alert.websocketAuthFailed.title'),
          type: AlertType.ERROR
        })
      })

      if (!socket) return;

      // Send username to backend for initialization
      socket.onopen = () => {
        if (!socket) return;
        const message: WebSocketMessage = {
          data: username,
          messageType: WebSocketMessageType.INIT_NOTIFICATION,
        };
        socket.send(JSON.stringify(message));
      };

      socket.onmessage = (ev: MessageEvent<string>) => {
        const message: WebSocketMessage = JSON.parse(ev.data) as WebSocketMessage;
        switch (message.messageType) {
          case WebSocketMessageType.SEND_NOTIFICATION: {

            break
          }
          default: {
            throw new Error(
              "Failed to handle this message type : " + message.messageType,
            );
          }
        }
      };

      socket.onerror = () => {
        useAlertStore().send({
          content: t("alert.socket.connectionFailed"),
          title: t("alert.socket.title"),
          type: AlertType.ERROR,
        });
      };

      socket.onclose = () => {
      }
    }

    function closeSocket() {
      socket?.close()
    }

    return {
      init,
      closeSocket
    };
  });
