package fr.zelytra.notification;

public enum NotificationMessageKey {
    EMPTY_TEAM("notification.emptyTeam");

    private final String key;

    NotificationMessageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}

