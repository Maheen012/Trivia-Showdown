package org.example.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;

/**
 * Manages the game lobby screen where players wait before the game starts.
 * Maintains proper window positioning and state across interactions.
 */
public class GameLobby {
    private Stage stage;
    private GameClient client;
    private boolean isHost;
    private final ObservableList<String> players = FXCollections.observableArrayList();
    private final Label scoreLabel = new Label();
    private static int windowOffset = 0; // Tracks window positions for multiple instances

    /**
     * Shows the lobby window with proper positioning
     */
    public void show(GameClient gameClient, boolean host, String code) {
        this.client = gameClient;
        this.isHost = host;

        stage = new Stage();
        // Position window with offset for multiple instances
        stage.setX(300 + (windowOffset % 5) * 50);
        stage.setY(100 + (windowOffset % 5) * 50);
        windowOffset++;

        VBox root = new VBox(20);
        root.setStyle(Theme.getBackgroundStyle());

        Label title = new Label("Game Lobby - Room: " + code);
        title.setStyle(Theme.getTitleStyle());

        ListView<String> playerList = new ListView<>(players);
        playerList.setPrefHeight(200);
        playerList.setStyle(Theme.getLightBodyStyle());

        scoreLabel.setStyle(Theme.getLightBodyStyle() + " -fx-font-weight: bold;");
        VBox scoreBox = new VBox(10, new Label("Current Scores:"), scoreLabel);
        scoreBox.setStyle(Theme.getLightBodyStyle());

        Button startBtn = new Button("Start Game");
        startBtn.setStyle(Theme.getPrimaryButtonStyle());
        startBtn.setDisable(!isHost);
        startBtn.setOnAction(e -> {
            client.sendMessage("start");
            startBtn.setDisable(true);
        });

        root.getChildren().addAll(title, playerList, scoreBox, startBtn);

        Scene scene = new Scene(root, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Lobby - " + code);

        // Prevent window from moving when gaining focus
        stage.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                stage.toFront();
            }
        });

        stage.show();
    }

    /**
     * Updates the list of players displayed in the lobby.
     *
     * @param playerNames an array of player names
     */
    public void updatePlayerList(String[] playerNames) {
        Platform.runLater(() -> players.setAll(playerNames));
    }

    /**
     * Updates the scores displayed in the lobby.
     *
     * @param scores a map of player names to their current scores
     */
    public void updateScores(Map<String, Integer> scores) {
        Platform.runLater(() -> {
            StringBuilder scoresText = new StringBuilder();
            scores.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(e -> scoresText.append(e.getKey())
                            .append(": ")
                            .append(e.getValue())
                            .append("\n"));
            scoreLabel.setText(scoresText.toString());
        });
    }

    /**
     * Closes the lobby window if it's open.
     */
    public void close() {
        if (stage != null) {
            Platform.runLater(() -> stage.close());
        }
    }
}
