package org.example.client;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Handles the display of winner announcement and final scores screens.
 * Manages the transition between winner screen and detailed results screen.
 */
public class Scoreboard {
    private static Stage stage;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int WINNER_DISPLAY_DURATION_SECONDS = 5;
    private static final int WINNER_SCENE_WIDTH = 500;
    private static final int WINNER_SCENE_HEIGHT = 350;
    private static final int RESULTS_SCENE_WIDTH = 400;
    private static final int RESULTS_SCENE_HEIGHT = 500;
    private static int windowOffset = 0;

    /**
     * Displays the winner screen followed by full results
     * @param scores Map of player names to their scores
     */
    public static void show(Map<String, Integer> scores) {
        Platform.runLater(() -> showWinnerScreen(scores));
    }

    /**
     * Creates and shows the winner announcement screen
     * @param scores Map containing player scores
     */
    private static void showWinnerScreen(Map<String, Integer> scores) {
        stage = new Stage();
        VBox root = createWinnerScreenRoot(scores);
        Scene scene = new Scene(root, WINNER_SCENE_WIDTH, WINNER_SCENE_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Winner!");
        stage.show();

        scheduleResultsTransition(scores);
    }

    /**
     * Creates the root layout for winner screen
     * @param scores Map containing player scores
     * @return Configured VBox root element
     */
    private static VBox createWinnerScreenRoot(Map<String, Integer> scores) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(Theme.getBackgroundStyle());

        String winner = getWinnerName(scores);

        Label winnerLabel = new Label("🏆 " + winner + " Wins! 🏆");
        winnerLabel.setStyle(Theme.getTitleStyle() + " -fx-font-size: 32px;");

        Label subtitle = new Label("Showing final results in " +
                WINNER_DISPLAY_DURATION_SECONDS + " seconds...");
        subtitle.setStyle(Theme.getLightBodyStyle());

        root.getChildren().addAll(winnerLabel, subtitle);
        return root;
    }

    /**
     * Extracts the winner's name from scores
     * @param scores Map of player scores
     * @return Name of the winning player
     */
    private static String getWinnerName(Map<String, Integer> scores) {
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No winner");
    }

    /**
     * Schedules the transition to results screen
     * @param scores Map containing player scores
     */
    private static void scheduleResultsTransition(Map<String, Integer> scores) {
        scheduler.schedule(() ->
                        Platform.runLater(() -> {
                            stage.close();
                            showFullResults(scores);
                        }),
                WINNER_DISPLAY_DURATION_SECONDS,
                TimeUnit.SECONDS
        );
    }

    /**
     * Displays the full results screen with all player scores
     * @param scores Map containing player scores
     */
    private static void showFullResults(Map<String, Integer> scores) {
        VBox root = createResultsScreenRoot(scores);
        Scene scene = new Scene(root, RESULTS_SCENE_WIDTH, RESULTS_SCENE_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Game Results");
        stage.show();
    }

    /**
     * Creates the root layout for results screen
     * @param scores Map containing player scores
     * @return Configured VBox root element
     */
    private static VBox createResultsScreenRoot(Map<String, Integer> scores) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle(Theme.getBackgroundStyle());

        Label title = new Label("Final Scores");
        title.setStyle(Theme.getTitleStyle());

        ListView<String> scoreList = createStyledScoreList(scores);
        Button exitBtn = createExitButton();

        root.getChildren().addAll(title, scoreList, exitBtn);
        return root;
    }

    /**
     * Creates a styled ListView of player scores
     * @param scores Map containing player scores
     * @return Configured ListView element
     */
    private static ListView<String> createStyledScoreList(Map<String, Integer> scores) {
        ListView<String> scoreList = new ListView<>();
        scoreList.setStyle("-fx-control-inner-background: #6a11cb; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold;");
        scoreList.setPrefHeight(300);

        scoreList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    if (item.startsWith(getWinnerName(scores))) {
                        setStyle("-fx-text-fill: " + Theme.WARNING_COLOR +
                                "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        scores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(e -> scoreList.getItems().add(e.getKey() + ": " + e.getValue()));

        return scoreList;
    }

    /**
     * Creates the exit button for results screen
     * @return Configured Button element
     */
   // MOD: ADDED System.exit(0) to close terminal operation when exit is pressed
    private static Button createExitButton() {
        Button exitBtn = new Button("Exit");
        exitBtn.setStyle(Theme.getAccentButtonStyle());
        exitBtn.setOnAction(e -> {
            stage.close();
            Platform.exit();
            System.exit(0);
        });
        return exitBtn;
    }

    /**
     * Closes the scoreboard and cleans up resources
     */


    public static void close() {
        if (stage != null) {
            Platform.runLater(stage::close);
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
