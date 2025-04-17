package org.example.server;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
/**
 * The GameRoom class represents a single game room in the trivia game.
 * It manages players, scores, game state, question flow, and bot participation.
 */
public class GameRoom {
    private final String code;
    private final Map<String, ClientHandler> players = new LinkedHashMap<>();
    private final Map<String, Integer> scores = new HashMap<>();
    private final GameLogic gameLogic;
    private boolean gameStarted = false;
    private ScheduledExecutorService questionTimer;
    private int questionTimeLimit = 10;
    private boolean computerBotEnabled = true;
    private String computerBotName = "Computer";
    /**
     * Constructs a GameRoom with a unique room code.
     *
     * @param code the room code assigned to this game
     */

    public GameRoom(String code) {
        this.code = code;
        this.gameLogic = new GameLogic();
        this.questionTimer = Executors.newSingleThreadScheduledExecutor();
    }
    /**
     * Adds a player to the room and sends the initial success message.
     * Also triggers bot addition if only one player is present.
     *
     * @param name    the player's name
     * @param handler the ClientHandler associated with the player
     */
    public void addPlayer(String name, ClientHandler handler) {
        players.put(name, handler);
        scores.put(name, 0);

        // Send success message and room code to host
        handler.sendMessage("JOIN_SUCCESS:" + code);
        if (players.size() == 1) {
            handler.sendMessage("ROOM_CODE:" + code);
        }

        announcePlayerList();
        announce(name + " has joined the room.");

        if (computerBotEnabled && players.size() == 1) {
            addComputerBot();
        }
    }

    /**
     * Adds a simulated computer bot that answers questions randomly or correctly.
     */
    private void addComputerBot() {
        players.put(computerBotName, new ClientHandler(null, null) {
            @Override
            public void sendMessage(String message) {
                if (message.startsWith("QUESTION:")) {
                    String[] parts = message.split("\\|");
                    String correctAnswer = parts[5];
                    Random rand = new Random();
                    int delay = 3 + rand.nextInt(6);

                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    executor.schedule(() -> {
                        // Bot has 80% chance to answer correctly
                        if (rand.nextDouble() < 0.8) {
                            processAnswer(computerBotName, correctAnswer);
                        } else {
                            char randomAnswer = (char) ('A' + rand.nextInt(4));
                            processAnswer(computerBotName, String.valueOf(randomAnswer));
                        }
                    }, delay, TimeUnit.SECONDS);
                }
            }
        });
        announce(computerBotName + " has joined the room.");
        announcePlayerList();
    }

    /**
     * Starts the game by resetting the logic and sending the first question.
     */
    public void startGame() {
        if (players.size() < 1) {
            announce("ERROR:Need at least 1 player to start the game");
            return;
        }

        gameLogic.startGame();
        gameStarted = true;
        announce("GAME_STARTED");
        sendNextQuestion();
    }

    /**
     * Sends the next question to all players, starts the countdown timer,
     * and handles timeout scenarios.
     */

    public void sendNextQuestion() {
        try {
            if (questionTimer != null) {
                questionTimer.shutdownNow();
                if (!questionTimer.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.err.println("Question timer didn't terminate properly");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        questionTimer = Executors.newSingleThreadScheduledExecutor();
        Question question = gameLogic.getNextQuestion();

        if (question == null) {
            endGame();
            return;
        }

        // New format: "QUESTION:currentQ/totalQ:text|A|B|C|D|correct|time"
        String questionMessage = String.format("QUESTION:%d/%d:%s|%s|%s|%s|%s|%s|%d",
                gameLogic.getCurrentQuestionIndex() + 1,  // Current question (1-based)
                gameLogic.getTotalQuestions(),           // Total questions
                question.getText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectAnswer(),
                questionTimeLimit);

        announce(questionMessage);

        // Timer updates every second
        final int[] timeRemaining = {questionTimeLimit};
        // In the sendNextQuestion method where you schedule timer updates:
        ScheduledFuture<?> timerTask = questionTimer.scheduleAtFixedRate(() -> {
            timeRemaining[0]--;
            double progress = (double) timeRemaining[0] / questionTimeLimit;
            announce("TIMER_UPDATE:" + progress + "|" + timeRemaining[0]);
            if (timeRemaining[0] <= 0) {
                announce("TIME_UP");
                announce("Time's up! Correct answer was: " + question.getCorrectAnswer());
                sendScoresUpdate();
                sendNextQuestion();
            }
        }, 1, 1, TimeUnit.SECONDS);

        // Full question timeout
        questionTimer.schedule(() -> {
            timerTask.cancel(true);
        }, questionTimeLimit, TimeUnit.SECONDS);
    }

    /**
     * Processes a submitted answer from a player and updates scores accordingly.
     *
     * @param playerName the name of the player submitting the answer
     * @param answer     the answer submitted (A/B/C/D)
     */
    public void processAnswer(String playerName, String answer) {
        if (!gameStarted) return;

        Question currentQuestion = gameLogic.getCurrentQuestion();
        if (currentQuestion == null) return;

        String normalizedInput = answer.trim().toUpperCase();
        boolean isCorrect = currentQuestion.isCorrectAnswer(normalizedInput);

        synchronized(scores) {
            if (isCorrect) {
                int newScore = scores.getOrDefault(playerName, 0) + 1;
                scores.put(playerName, newScore);
                players.get(playerName).sendMessage(
                        "ANSWER_RESULT:Correct! Your score: " + newScore +
                                "|" + currentQuestion.getCorrectAnswer());
            } else {
                players.get(playerName).sendMessage(
                        "ANSWER_RESULT:Incorrect! The correct answer was: " +
                                currentQuestion.getCorrectAnswer() +
                                "|" + currentQuestion.getCorrectAnswer());
            }
            sendScoresUpdate();
        }
    }

    /**
     * Sends the updated scores to all players.
     */
    private void sendScoresUpdate() {
        String scoresStr = scores.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));
        announce("SCORES:" + scoresStr);
    }

    /**
     * Ends the game and sends the final scores to all players.
     */
    public void endGame() {
        gameStarted = false;
        if (questionTimer != null) {
            questionTimer.shutdownNow();
        }

        String finalScores = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));

        announce("FINAL_SCORES:" + finalScores);
    }

    /**
     * Sends an updated player list to all connected clients.
     */
    private void announcePlayerList() {
        String playerList = String.join(",", players.keySet());
        announce("PLAYER_LIST:" + playerList);
    }

    /**
     * Sends a message to all players in the room.
     *
     * @param message the message to broadcast
     */
    public void announce(String message) {
        System.out.println("Broadcasting: " + message); // Debug log
        for (ClientHandler handler : players.values()) {
            if (handler != null) {
                try {
                    handler.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("Error sending to player: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Returns the room code.
     *
     * @return the room's code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns a set of all player names in the room.
     *
     * @return a Set of player names
     */
    public Set<String> getPlayerNames() {
        return players.keySet();
    }

    /**
     * Indicates whether the game has started.
     *
     * @return true if the game is running, false otherwise
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Removes a player from the room and ends the game if no players remain.
     *
     * @param name the name of the player to remove
     */
    public void removePlayer(String name) {
        players.remove(name);
        announce(name + " has left the room.");
        announcePlayerList();

        if (gameStarted && players.isEmpty()) {
            endGame();
        }
    }
}