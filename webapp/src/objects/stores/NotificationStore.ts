import {defineStore} from "pinia";
import {HTTPAxios} from "@/objects/utils/HTTPAxios.ts";
import {AlertType, useAlertStore} from "@/vue/alerts/AlertStore.ts";
import {WebSocketMessage, WebSocketMessageType} from "@/objects/pool/WebSocet.ts";
import {tsi18n} from "@/objects/i18n";
import eventBus from "@/objects/bus/EventBus.ts";

const {t} = tsi18n.global;

export enum NotificationType {
  INVITE_TO_GAME = "INVITE_TO_GAME",
  INVITE_TO_FRIEND = "INVITE_TO_FRIEND",
  MESSAGE = "MESSAGE"
}

export interface NotificationMessage {
  data: {
    type: NotificationType,
    data: unknown
  }
  users: string[]
}

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
            handleNotification(message.data as NotificationMessage)
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

    function handleNotification(notificationMessage: NotificationMessage) {
      switch (notificationMessage.data.type) {
        case NotificationType.INVITE_TO_FRIEND: {
          eventBus.emit('refreshFriendInvites');
          break
        }
        case NotificationType.INVITE_TO_GAME: {
          useAlertStore().send({
            type: AlertType.VALID,
            title: t('notification.joinParty'),
            event: "joinPoolParty",
            content: "",
            data: notificationMessage.data.data,
            timeout: 10000
          })
          break
        }
        case NotificationType.MESSAGE: {
          useAlertStore().send({
            type: AlertType.VALID,
            title: t(notificationMessage.data.data as unknown as string),
            content: "",
            timeout: 3000
          })
          break
        }
        default: {
          throw new Error(
            "Failed to handle this message type : " + notificationMessage.data.type,
          );
        }
      }
    }

    function send(notification: NotificationMessage) {
      if (!socket) return;
      const message: WebSocketMessage = {
        data: notification,
        messageType: WebSocketMessageType.SEND_NOTIFICATION,
      };
      socket.send(JSON.stringify(message));
    }

    return {
      init,
      send,
      closeSocket
    };
  });
