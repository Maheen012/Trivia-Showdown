package org.example.client;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Manages the question/answer interface with stable window positioning and state.
 * Handles multiple concurrent question screens without interference.
 */
public class QuestionScreen {
    private Stage stage;
    private final GameClient client;
    private ProgressBar timerBar;
    private Label scoreLabel;
    private Label feedbackLabel;
    private VBox feedbackBox;
    private Label questionProgressLabel;
    private Label timerLabel;
    private Label playerNameLabel;
    private int totalQuestions = 15;
    private int timeLimit = 10;
    private static int windowOffset = 0;

    /**
     * Constructs a QuestionScreen associated with the given GameClient
     * @param client The GameClient instance managing the connection
     */
    public QuestionScreen(GameClient client) {
        this.client = client;
    }

    /**
     * Displays the question screen with stable window positioning
     * @param questionText The trivia question to display
     * @param options Array of 4 answer options (A-D)
     */
    public void show(String questionText, String[] options) {
        Platform.runLater(() -> {
            if (stage == null) {
                stage = new Stage();
                // Position window with offset for multiple instances
                stage.setX(400 + (windowOffset % 5) * 30);
                stage.setY(150 + (windowOffset % 5) * 30);
                windowOffset++;

                // Lock window position after showing
                stage.setOnShown(e -> {
                    stage.setX(stage.getX());
                    stage.setY(stage.getY());
                });
            }

            VBox root = new VBox(15);
            root.setPadding(new Insets(25));
            root.setStyle("-fx-background-color: #f8f9fa;");

            /* Header Section */
            HBox headerBox = new HBox(20);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.setStyle("-fx-background-color: #6a11cb; -fx-padding: 10; -fx-background-radius: 5 5 0 0;");

            playerNameLabel = new Label("Player: " + client.getPlayerName());
            playerNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            scoreLabel = new Label("Score: Loading...");
            scoreLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            Region leftSpacer = new Region();
            HBox.setHgrow(leftSpacer, Priority.ALWAYS);

            timerLabel = new Label("Time: " + timeLimit + "s");
            timerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            Region rightSpacer = new Region();
            HBox.setHgrow(rightSpacer, Priority.ALWAYS);

            questionProgressLabel = new Label();
            questionProgressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(
                    playerNameLabel, scoreLabel, leftSpacer,
                    timerLabel, rightSpacer, questionProgressLabel
            );

            /* Question Display */
            Label questionLabel = new Label(questionText);
            questionLabel.setWrapText(true);
            questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            questionLabel.setPadding(new Insets(15, 0, 15, 0));

            /* Answer Options */
            ToggleGroup optionsGroup = new ToggleGroup();
            VBox optionsBox = new VBox(10);
            optionsBox.setPadding(new Insets(0, 0, 15, 0));

            for (int i = 0; i < options.length; i++) {
                RadioButton option = new RadioButton((char)(65 + i) + ". " + options[i]);
                option.setToggleGroup(optionsGroup);
                option.setUserData((char)('A' + i));
                option.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                option.setPadding(new Insets(8, 5, 8, 5));
                optionsBox.getChildren().add(option);
            }

            /* Submit Button */
            Button submitBtn = new Button("Submit Answer");
            submitBtn.setStyle(Theme.getPrimaryButtonStyle());
            submitBtn.setOnAction(e -> handleAnswerSubmission(optionsGroup, submitBtn));

            /* Timer Progress Bar */
            timerBar = new ProgressBar(1.0);
            timerBar.setPrefWidth(Double.MAX_VALUE);
            timerBar.setStyle("-fx-accent: #2ecc71; -fx-pref-height: 10px;");

            /* Feedback Area */
            feedbackLabel = new Label();
            feedbackLabel.setStyle("-fx-font-size: 14px;");
            feedbackBox = new VBox(5, new Separator(), feedbackLabel);
            feedbackBox.setVisible(false);
            feedbackBox.setPadding(new Insets(10, 0, 0, 0));

            root.getChildren().addAll(
                    headerBox, questionLabel, optionsBox,
                    submitBtn, feedbackBox, timerBar
            );

            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.setTitle("Question - " + client.getPlayerName());
            stage.setAlwaysOnTop(true);
            stage.show();
        });
    }

    /**
     * Handles answer submission while maintaining window state
     * @param optionsGroup The toggle group containing answer options
     * @param submitBtn The submit button that triggered the action
     */
    private void handleAnswerSubmission(ToggleGroup optionsGroup, Button submitBtn) {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected != null) {
            char answer = (char) selected.getUserData();
            client.sendMessage(String.valueOf(answer));

            submitBtn.setDisable(true);
            submitBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

            feedbackLabel.setText("✓ Answer submitted! Waiting for results...");
            feedbackLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");
            feedbackBox.setVisible(true);
        }
    }

    /**
     * Displays feedback about the player's answer.
     *
     * @param isCorrect     True if the answer was correct
     * @param correctAnswer The text of the correct answer
     */
    public void showAnswerFeedback(boolean isCorrect, String correctAnswer) {
        Platform.runLater(() -> {
            if (isCorrect) {
                // Green text for correct answer
                feedbackLabel.setText("✓ Correct! The answer was: " + correctAnswer);
                feedbackLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14px;");
            } else {
                // Red text for incorrect answer
                feedbackLabel.setText("✗ Incorrect! The correct answer was: " + correctAnswer);
                feedbackLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
            }
            feedbackBox.setVisible(true);
        });
    }

    /**
     * Displays time expiration feedback.
     */
    public void showTimeUpFeedback() {
        Platform.runLater(() -> {
            // Orange text for time expiration
            feedbackLabel.setText("⏰ Time's up! Please wait for next question...");
            feedbackLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px;");
            feedbackBox.setVisible(true);
        });
    }

    /**
     * Updates the displayed score.
     *
     * @param score The player's new score
     */
    public void updateScore(int score) {
        Platform.runLater(() -> scoreLabel.setText("Score: " + score));
    }

    /**
     * Updates the timer display with remaining time.
     *
     * @param progress         The progress percentage (1.0 to 0.0)
     * @param secondsRemaining The number of seconds remaining
     */
    public void updateTimer(double progress, int secondsRemaining) {
        Platform.runLater(() -> {
            // Update progress bar and text
            timerBar.setProgress(progress);
            timerLabel.setText("Time: " + secondsRemaining + "s");

            // Change colors based on time remaining
            if (progress < 0.3) {
                // Red when time is almost up
                timerBar.setStyle("-fx-accent: #e74c3c; -fx-pref-height: 10px;");
                timerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else if (progress < 0.6) {
                // Yellow when time is medium
                timerBar.setStyle("-fx-accent: #f39c12; -fx-pref-height: 10px;");
                timerLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            } else {
                // Green when plenty of time remains
                timerBar.setStyle("-fx-accent: #2ecc71; -fx-pref-height: 10px;");
                timerLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            }
        });
    }

    /**
     * Updates the question progress indicator.
     *
     * @param currentQuestion The current question number (1-based)
     */
    public void updateQuestionProgress(int currentQuestion) {
        Platform.runLater(() -> {
            questionProgressLabel.setText("Q" + currentQuestion + "/" + totalQuestions);
            questionProgressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        });
    }

    /**
     * Sets the total number of questions in the game.
     *
     * @param total The total number of questions
     */
    public void setTotalQuestions(int total) {
        totalQuestions = total;
    }

    /**
     * Sets the time limit per question.
     *
     * @param seconds The time limit in seconds
     */
    public void setTimeLimit(int seconds) {
        timeLimit = seconds;
    }

    /**
     * Closes the question screen.
     */
    public void close() {
        if (stage != null) {
            Platform.runLater(() -> {
                stage.close();
                stage = null;
            });
        }
    }

    /**
     * Updates the question number display.
     *
     * @param current The current question number
     */
    public void updateQuestionNumber(int current) {
        Platform.runLater(() -> {
            questionProgressLabel.setText(current + "/" + totalQuestions);
        });
    }
}
