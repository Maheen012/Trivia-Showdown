package org.example.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The MainMenu class is the entry point of the Trivia Game client.
 * It provides the user interface for creating or joining a game room.
 * Players can enter their name, and depending on their choice, create a new room or join an existing one.
 */
public class MainMenu extends Application {
    private Stage primaryStage;

    /**
     * Launches the JavaFX application.
     *
     * @param primaryStage the primary window of the application
     */
    @Override
    public void start(Stage primaryStage) {
        // Prevent the application from exiting automatically when the last window is closed
        Platform.setImplicitExit(false);
        this.primaryStage = primaryStage;
        setupUI();
    }

    /**
     * Sets up the main menu UI, allowing users to create or join a game.
     */
    private void setupUI() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(Theme.getBackgroundStyle());

        Label title = new Label("TRIVIA SHOWDOWN");
        title.setStyle(Theme.getTitleStyle());

        Button createBtn = new Button("Create Game");
        createBtn.setStyle(Theme.getPrimaryButtonStyle());
        createBtn.setOnAction(e -> showNameInput(true));

        Button joinBtn = new Button("Join Game");
        joinBtn.setStyle(Theme.getSecondaryButtonStyle());
        joinBtn.setOnAction(e -> showNameInput(false));

        root.getChildren().addAll(title, createBtn, joinBtn);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trivia Game");
        primaryStage.show();
    }

    /**
     * Displays a popup to collect the player's name.
     *
     * @param isHost true if the player is creating a room, false if joining
     */
    private void showNameInput(boolean isHost) {
        Stage nameStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Button submitBtn = new Button(isHost ? "Create" : "Join");
        submitBtn.setOnAction(e -> handleNameSubmit(isHost, nameField, nameStage));

        layout.getChildren().addAll(new Label("Enter your name:"), nameField, submitBtn);
        nameStage.setScene(new Scene(layout, 300, 200));
        nameStage.show();
    }

    /**
     * Handles submission of the player's name.
     * Depending on the host flag, it either creates a room or prompts for a room code to join.
     *
     * @param isHost    true if the player wants to create a room
     * @param nameField the TextField containing the entered name
     * @param nameStage the stage to close after name is entered
     */
    private void handleNameSubmit(boolean isHost, TextField nameField, Stage nameStage) {
        try {
            GameClient client = new GameClient("localhost", 50000);
            client.setPlayerName(nameField.getText());

            if (isHost) {
                setupHostClient(client, nameStage);
            } else {
                showCodeInput(client, nameStage);
            }
        } catch (IOException ex) {
            showAlert("Connection Error", "Could not connect to server");
        }
    }

    /**
     * Sets up the client as a host. Sends the 'create' command to the server and displays the lobby on success.
     *
     * @param client    the GameClient instance
     * @param nameStage the stage to close after successful room creation
     */
    private void setupHostClient(GameClient client, Stage nameStage) {
        client.setOnRoomCreated(() -> {
            nameStage.close();
            primaryStage.hide(); // MOD: CHANGED HERE DONT CLOSE JUST HIDE
            // MOD: GAME LOBBY INSTANCE
            GameLobby lobby = new GameLobby();
            client.setGameLobby(lobby);
            lobby.show(client, true, client.roomCode);
        });
        client.sendMessage("create");
    }

    /**
     * Prompts the user to enter a room code to join an existing game.
     *
     * @param client    the GameClient instance
     * @param nameStage the name entry stage to close upon success
     */
    private void showCodeInput(GameClient client, Stage nameStage) {
        Stage codeStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField codeField = new TextField();
        codeField.setPromptText("Enter room code");

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);

        Button joinBtn = new Button("Join Room");
        joinBtn.setOnAction(e -> {
            String code = codeField.getText().trim().toUpperCase();
            if (!code.isEmpty()) {
                // Send join command in two parts as server expects
                client.sendMessage("join");
                client.sendMessage(code);

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            if (client.roomCode != null && client.roomCode.equals(code)) {
                                nameStage.close();
                                codeStage.close();
                                primaryStage.hide(); // MOD: HIDE NOT CLOSE
                                // GAME LOBBY INSTANCE
                                GameLobby lobby = new GameLobby();
                                client.setGameLobby(lobby);
                                lobby.show(client, false, client.roomCode);
                            } else {
                                statusLabel.setText("Failed to join room. Invalid code or room full.");
                            }
                        });
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } else {
                statusLabel.setText("Please enter a room code");
            }
        });

        layout.getChildren().addAll(
                new Label("Enter room code:"),
                codeField,
                joinBtn,
                statusLabel
        );
        codeStage.setScene(new Scene(layout, 300, 200));
        codeStage.show();
    }

    /**
     * Displays an error alert with the given title and message.
     *
     * @param title   the title of the alert window
     * @param message the message content of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
