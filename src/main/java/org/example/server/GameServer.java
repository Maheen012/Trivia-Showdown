package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
/**
 * The GameServer class starts a server on a fixed port and handles
 * incoming client connections. It manages game rooms by creating,
 * storing, and retrieving them based on unique room codes.
 */
public class GameServer {
    //port to start server
    /**
     * Port number the server listens on.
     */
    public static final int port = 50000;
    //manage game rooms
    /**
     * A map of active game rooms indexed by their unique room code.
     */
    private final Map<String, GameRoom> gameRooms = new HashMap<>();

    /**
     * Main method to launch the game server.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new GameServer().start();
    }

    // Start the game server
    /**
     * Starts the game server and begins accepting client connections.
     * Each client is assigned a new thread via ClientHandler.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server began running on " + port + "...");

            while (true) {
                // Accept a socket connection from a new client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());

                // Create a ClientHandler for the new client
                ClientHandler handler = new ClientHandler(clientSocket, this);

                // Start the client handler in a new thread
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* After getting the host name, the function generates a random code to start a new game,
    generates a new game room,
    Adds player to that room,
    stores the room in game rooms map,
    and returns the code. */

    public synchronized String createRoom(String hostName, ClientHandler hostHandler) {
        String code = generateGameCode();
        GameRoom room = new GameRoom(code);
        room.addPlayer(hostName, hostHandler);
        gameRooms.put(code, room);
        return code;
    }

    /* to join a room, user inputs the code along with their name,
    function retrieves the requested game from game room,
    if the room exists (code is valid), then player gets added to the room
     */
    public synchronized boolean joinRoom(String code, String playerName, ClientHandler handler) {
        GameRoom room = gameRooms.get(code);
        if (room != null) {
            room.addPlayer(playerName, handler);
            return true;
        }
        return false;
    }

    /* function generates a random & unique code for new games */
    private String generateGameCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(chars.charAt(rand.nextInt(chars.length())));
            }
            code = sb.toString();
        } while (gameRooms.containsKey(code));
        return code;
    }

    /* Getter function to access a room using a code */
    public GameRoom getRoom(String code) {
        return gameRooms.get(code);
    }
}
