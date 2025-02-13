package client;

import javax.swing.*;

public class MazeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.initialize();
        });
    }
}























