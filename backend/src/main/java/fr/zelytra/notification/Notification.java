package fr.zelytra.notification;

public record Notification<T>(NotificationMessageType type, T data) {
}
