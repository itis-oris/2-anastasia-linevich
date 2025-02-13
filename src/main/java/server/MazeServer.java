package server;

import entity.Player;
import shared.Maze;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MazeServer {
    public static void main(String[] args) {
        int port = 12345;
        Maze maze = new Maze(30, 48);
        System.out.println(maze);
        ConcurrentMap<String, Player> players = new ConcurrentHashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, players, maze);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}






