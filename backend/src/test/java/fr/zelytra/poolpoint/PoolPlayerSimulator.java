package fr.zelytra.poolpoint;

import fr.zelytra.user.UserEntity;
import io.quarkus.logging.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PoolPlayerSimulator {

    private final int playerAmount;
    private final int iteration;
    private final List<PoolSimulationUser> playerList;
    private final Map<Integer, List<PoolSimulationUser>> gameHistoryMap;

    public PoolPlayerSimulator(int playerAmount, int iteration) {
        this.playerAmount = playerAmount;
        this.iteration = iteration;
        this.playerList = new ArrayList<>();
        this.gameHistoryMap = new HashMap<>();
        generatePlayers();
        runSimulation();
    }

    private void runSimulation() {
        gameHistoryMap.put(0, deepCopy(playerList));
        for (int i = 0; i < iteration; i++) {
            Collections.shuffle(playerList);
            for (int j = 0; j < playerAmount; j += 2) {
                if (j + 1 < playerAmount) {
                    PoolSimulationUser player1 = playerList.get(j);
                    PoolSimulationUser player2 = playerList.get(j + 1);
                    playMatch(player1, player2);
                }
            }
            gameHistoryMap.put(i + 1, deepCopy(playerList));
        }
    }

    private List<PoolSimulationUser> deepCopy(List<PoolSimulationUser> playerList) {
        List<PoolSimulationUser> currentIterationPlayers = new ArrayList<>();
        for (PoolSimulationUser player : playerList) {
            currentIterationPlayers.add(new PoolSimulationUser(player)); // Create a deep copy of the player
        }
        return currentIterationPlayers;
    }

    private void playMatch(PoolSimulationUser player1, PoolSimulationUser player2) {
        boolean player1Wins = isPlayerWinningTheGame(player1.getWeight(), player2.getWeight());
        PoolPointCalculator pointCalculatorPlayer1 = new PoolPointCalculator(player1.getPp(), player1.getGamePlayed());
        PoolPointCalculator pointCalculatorPlayer2 = new PoolPointCalculator(player2.getPp(), player2.getGamePlayed());

        // Increment game amount
        player1.setGamePlayed(player1.getGamePlayed() + 1);
        player2.setGamePlayed(player2.getGamePlayed() + 1);

        // Update PP
        player1.setPp(pointCalculatorPlayer1.computeNewElo(player1Wins ? 1 : 0, player2.getPp()));
        player2.setPp(pointCalculatorPlayer2.computeNewElo(player1Wins ? 0 : 1, player1.getPp()));
    }

    private void generatePlayers() {
        for (int i = 0; i < playerAmount; i++) {
            PoolSimulationUser user = new PoolSimulationUser("User" + i, i == playerAmount-1 ? 20 : (int) (Math.floor(i / 10) + 1));
            playerList.add(user);
        }
    }

    private boolean isPlayerWinningTheGame(int player1Weight, int player2Weight) {
        Log.info(player1Weight + " " + player2Weight);
        return Math.random() < ((double) player1Weight / Math.max(1, player1Weight + player2Weight));
    }

    public void generateCSV(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Get all usernames and sort them
            List<String> usernames = playerList.stream()
                    .map(PoolSimulationUser::getUsername)
                    .sorted(new Comparator<String>() {

                        @Override
                        public int compare(String o1, String o2) {
                            if (o1.length() == o2.length()) {
                                return o1.compareTo(o2);
                            }
                            return o1.length() - o2.length();
                        }
                    })
                    .toList();

            // Write header with sorted usernames
            for (int i = 0; i < usernames.size(); i++) {
                writer.append(usernames.get(i));
                if (i < usernames.size() - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");

            // Write scores for each iteration
            for (Map.Entry<Integer, List<PoolSimulationUser>> entry : gameHistoryMap.entrySet()) {
                List<PoolSimulationUser> players = entry.getValue();
                for (int i = 0; i < usernames.size(); i++) {
                    String username = usernames.get(i);
                    // Find the corresponding player in the current iteration
                    Optional<PoolSimulationUser> optionalUser = players.stream()
                            .filter(p -> p.getUsername().equals(username))
                            .findFirst();
                    // Write the score if found, otherwise write 0
                    if (optionalUser.isPresent()) {
                        writer.append(String.valueOf(optionalUser.get().getPp()));
                    } else {
                        writer.append("0");
                    }
                    if (i < usernames.size() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        playerList.sort(Comparator.comparingInt(UserEntity::getPp).reversed());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Rankings:\n");
        for (int i = 0; i < playerList.size(); i++) {
            UserEntity player = playerList.get(i);
            stringBuilder.append(String.format("%d. %s - Points: %d, Games Played: %d\n",
                    i + 1, player.getUsername(), player.getPp(), player.getGamePlayed()));
        }
        return stringBuilder.toString();
    }
}
