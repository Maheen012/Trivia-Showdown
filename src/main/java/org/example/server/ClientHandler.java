package org.example.server;

import java.io.*;
import java.net.Socket;
/**
 * The ClientHandler class handles communication with a single client
 * connected to the game server. It processes incoming commands and messages,
 * manages room creation and joining, and sends responses back to the client.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameServer server;
    private BufferedReader input;
    private PrintWriter output;
    private String name;
    private GameRoom currentRoom;

    // Constructor to initialize client handler with socket and server reference
    /**
     * Constructs a ClientHandler with a given socket and reference to the GameServer.
     *
     * @param socket the client's socket connection
     * @param server the main GameServer instance managing rooms and clients
     */
    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    // Main client handling thread that processes commands and messages
    /**
     * The main execution method for the client thread.
     * Handles input from the client and processes commands such as:
     * - create
     * - join
     * - start
     * - next
     * - A/B/C/D (answers)
     */
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Get player name
            output.println("Enter your name:");
            name = input.readLine();
            output.println("Hello " + name + "! Type 'create' to make a game or 'join' to join one.");

            // Main command loop
            while (true) {
                String command = input.readLine();
                if (command == null) break;

                if ("create".equalsIgnoreCase(command)) {
                    handleCreateCommand();
                } else if ("join".equalsIgnoreCase(command)) {
                    handleJoinCommand();
                } else if ("start".equalsIgnoreCase(command) && currentRoom != null) {
                    if (currentRoom.getPlayerNames().iterator().next().equals(name)) {
                        currentRoom.startGame();
                    } else {
                        output.println("Only the host can start the game!");
                    }
                } else if (command.length() == 1 && "ABCD".contains(command.toUpperCase())) {
                    // Handle answer submission
                    if (currentRoom != null && currentRoom.isGameStarted()) {
                        currentRoom.processAnswer(name, command.toUpperCase());
                    } else {
                        output.println("Game not started yet!");
                    }
                } else if ("next".equalsIgnoreCase(command) && currentRoom != null) {
                    // Host can force next question
                    if (currentRoom.getPlayerNames().iterator().next().equals(name)) {
                        currentRoom.sendNextQuestion();
                    }
                } else {
                    output.println("Unknown command. Available commands:");
                    output.println("- create: Create new game");
                    output.println("- join: Join existing game");
                    output.println("- start: Start game (host only)");
                    output.println("- [A/B/C/D]: Answer current question (just the letter)");
                    if (currentRoom != null && currentRoom.getPlayerNames().iterator().next().equals(name)) {
                        output.println("- next: Move to next question (host only)");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(name + " disconnected.");
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
        }
    }

    // Handles game creation command from client
    /**
     * Handles the 'create' command sent by the client.
     * Creates a new game room and assigns the client as the host.
     *
     * @throws IOException if an error occurs during room creation
     */
    private void handleCreateCommand() throws IOException {
        String code = server.createRoom(name, this);
        currentRoom = server.getRoom(code);
        output.println("Game created! Your code is: " + code);
        output.println("Type 'start' to begin when players have joined.");
    }

    // Handles game joining command from client
    /**
     * Handles the 'join' command sent by the client.
     * Prompts for a room code and attempts to join the specified room.
     *
     * @throws IOException if an error occurs during joining
     */
    private void handleJoinCommand() throws IOException {
        output.println("Enter game code:");
        String code = input.readLine().toUpperCase();
        boolean joined = server.joinRoom(code, name, this);
        if (joined) {
            currentRoom = server.getRoom(code);
            output.println("JOIN_SUCCESS:" + code);
        } else {
            output.println("JOIN_ERROR:Game not found or already started");
        }
    }

    // Sends message to this specific client
    /**
     * Sends a message to this client through the output stream.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        output.println(message);
    }

    // Returns the name of this client
    /**
     * Returns the name of the player associated with this client.
     *
     * @return the client's name
     */
    public String getName() {
        return name;
    }
}