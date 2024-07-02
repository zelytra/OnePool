package fr.zelytra.game.pool.data;

import java.util.List;

public record GameReport(List<GameReportPlayer> victoryPlayer, List<GameReportPlayer> looserPlayer) {

    public GameReportPlayer getFromUsername(String username) {
        for (GameReportPlayer victoryPlayer : victoryPlayer) {
            if (victoryPlayer.username().equals(username)) {
                return victoryPlayer;
            }
        }
        for (GameReportPlayer looserPlayer : looserPlayer) {
            if (looserPlayer.username().equals(username)) {
                return looserPlayer;
            }
        }
        return null;
    }
}
