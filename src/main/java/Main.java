import client.MazeGame;
import server.MazeServer;

public class Main {
    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> {
            try {
                System.out.println("Starting the Maze Server...");
                MazeServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Starting the Maze Game client...");
        MazeGame.main(new String[]{});
    }
}


