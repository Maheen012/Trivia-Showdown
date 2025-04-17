package org.example.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GameClient handles the connection and communication with the trivia game server.
 * It is responsible for sending and receiving messages, managing room and player data,
 * and updating the UI based on server responses.
 *
 * This class runs a background thread to continuously listen for server messages
 * and updates the JavaFX UI accordingly using Platform.runLater().
 */
public class GameClient {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    String roomCode;
    private String playerName;
    private Runnable onRoomCreated;
    private QuestionScreen questionScreen;
    private int currentScore = 0; //Added new score var

    // Field to hold the lobby instance
    private GameLobby gameLobby;

    /**
     * Constructs a GameClient and connects to the server.
     *
     * @param host the server hostname or IP address
     * @param port the port number of the server
     * @throws IOException if the connection fails
     */
    public GameClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listenForMessages).start();
        this.questionScreen = new QuestionScreen(this);
    }

    /**
     * Sets a callback to be executed when the room is successfully created.
     *
     * @param callback the callback function to run
     */
    public void setOnRoomCreated(Runnable callback) {
        this.onRoomCreated = callback;
    }

    /**
     * Sets the GameLobby instance for this client.
     *
     * @param lobby the GameLobby instance
     */
    public void setGameLobby(GameLobby lobby) {
        this.gameLobby = lobby;
    }

    /**
     * Sends a message to the server.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        output.println(message);
    }

    /**
     * Sets the player name and notifies the server.
     *
     * @param name the name of the player
     */
    public void setPlayerName(String name) {
        this.playerName = name;
        output.println(name);
    }

    /**
     * Continuously listens for messages from the server in a background thread.
     * Parses and handles incoming messages appropriately.
     */
    private void listenForMessages() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                System.out.println("Received: " + message);
                handleServerMessage(message);
            }
        } catch (IOException e) {
            Platform.runLater(() -> showError("Disconnected from server"));
        }
    }

    /**
     * Handles different types of server messages and updates the UI accordingly.
     *
     * @param message the message received from the server
     */
    private void handleServerMessage(String message) {
        System.out.println("Received: " + message);

        try {
            if (message.startsWith("ROOM_CODE:")) {
                this.roomCode = message.substring(10);
                Platform.runLater(() -> {
                    if (onRoomCreated != null) {
                        onRoomCreated.run();
                    }
                });
            } else if (message.startsWith("JOIN_SUCCESS:")){
                this.roomCode = message.substring(13);
            } else if (message.startsWith("Joined game successfully")){
                if (message.contains(":")) {
                    this.roomCode = message.split(":")[1].trim();
                }
            }
            else if (message.startsWith("PLAYER_LIST:")) {
                String[] players = message.substring(12).split(",");
                // Use the lobby instance to update the player list
                Platform.runLater(() -> {
                    if (gameLobby != null) {
                        gameLobby.updatePlayerList(players);
                    }
                });
            }
            else if (message.startsWith("QUESTION:")) {
                // Format: "QUESTION:1/15:text|A|B|C|D|correct|time"
                String[] parts = message.split("\\|");
                if (parts.length >= 6) {
                    // Extract question number and text
                    String[] questionParts = parts[0].substring(9).split(":", 2);
                    if (questionParts.length == 2) {
                        String[] progress = questionParts[0].split("/");
                        int currentQ = Integer.parseInt(progress[0]);
                        int totalQ = Integer.parseInt(progress[1]);

                        String questionText = questionParts[1];
                        String[] options = Arrays.copyOfRange(parts, 1, 5);

                        Platform.runLater(() -> {
                            questionScreen.setTotalQuestions(totalQ);
                            questionScreen.updateQuestionNumber(currentQ);
                            questionScreen.show(questionText, options);
                        });
                    }
                }
            }
            else if (message.startsWith("ANSWER_RESULT:")) {
                // Handle both formats:
                // "ANSWER_RESULT:Correct! Your score: 1|Mitochondria"
                // "ANSWER_RESULT:Incorrect! The correct answer was: Nitrogen|Nitrogen"
                boolean isCorrect = message.contains("Correct");
                String[] parts = message.split("\\|");
                String feedback = parts[0].substring("ANSWER_RESULT:".length());

                // Extract score if available
                int score = -1;
                if (message.contains("score:")) {
                    try {
                        String scorePart = message.split("score:")[1];
                        score = Integer.parseInt(scorePart.split("[^0-9]")[0]);
                        setCurrentScore(score); //Added new score update
                    } catch (Exception e) {
                        System.err.println("Error parsing score: " + e.getMessage());
                    }
                }

                // Get correct answer (everything after last |)
                String correctAnswer = parts.length > 1 ? parts[parts.length-1] : "";

                int finalScore = score;
                Platform.runLater(() -> {
                    questionScreen.showAnswerFeedback(isCorrect, correctAnswer);
                    if (finalScore != -1) {
                        questionScreen.updateScore(finalScore);
                    }
                });
            }
            else if (message.startsWith("SCORES:")) {
                try {
                    Map<String, Integer> scores = parseScores(message.substring(7));
                    Platform.runLater(() -> {
                        if (gameLobby != null) {
                            gameLobby.updateScores(scores);
                        }
                        if (scores.containsKey(playerName)) {
                            questionScreen.updateScore(scores.get(playerName));
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error parsing scores: " + e.getMessage());
                }
            }
            else if (message.startsWith("FINAL_SCORES:")) {
                try {
                    Map<String, Integer> scores = parseScores(message.substring(13));
                    Platform.runLater(() -> {
                        questionScreen.close();
                        Scoreboard.show(scores);
                    });
                } catch (Exception e) {
                    System.err.println("Error parsing final scores: " + e.getMessage());
                }
            }
            else if (message.equals("GAME_STARTED")) {
                Platform.runLater(() -> {
                    if (gameLobby != null) {
                        gameLobby.close();
                    }
                });
            }
            else if (message.startsWith("TIMER_UPDATE:")) {
                try {
                    String[] parts = message.substring(13).split("\\|");
                    if (parts.length >= 2) {
                        double progress = Double.parseDouble(parts[0]);
                        int timeLeft = Integer.parseInt(parts[1]);
                        Platform.runLater(() -> questionScreen.updateTimer(progress, timeLeft));
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing timer update: " + e.getMessage());
                }
            }
            else if (message.equals("TIME_UP")) {
                Platform.runLater(() -> {
                    questionScreen.showTimeUpFeedback();
                    questionScreen.updateTimer(0, 0);
                });
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + message + " - " + e.getMessage());
        }
    }

    /**
     * Parses a score message into a map of player names and scores.
     *
     * @param scoresStr the raw score string from the server
     * @return a map of player names to scores
     */
    private Map<String, Integer> parseScores(String scoresStr) {
        return Arrays.stream(scoresStr.split(","))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> Integer.parseInt(arr[1])
                ));
    }

    /**
     * Displays an error alert with the given message.
     *
     * @param message the message to display
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Disconnects from the server and closes the socket.
     *
     * @throws IOException if an error occurs while closing the connection
     */
    public void disconnect() throws IOException {
        socket.close();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int score) {
        this.currentScore = score;
    }
}
