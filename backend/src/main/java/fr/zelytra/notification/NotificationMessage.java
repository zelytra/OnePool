package fr.zelytra.notification;

import java.util.List;

public record NotificationMessage<T>(List<String> users, T data) {
}
