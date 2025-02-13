package client;

import entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameUI gameUI;
    private MazeClient client;
    private ShopPanel shopPanel;
    private LoginPanel loginPanel;
    private MainMenuPanel mainMenuPanel;

    public MainFrame() {
        super("Maze Game");
        initializeComponents();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gameUI = new GameUI();
        client = new MazeClient("localhost", 12345, gameUI);

        shopPanel = new ShopPanel(cardLayout, mainPanel, client, gameUI);
        client.setShopPanel(shopPanel);

        loginPanel = new LoginPanel(this::handleLogin, cardLayout, mainPanel, client);
        mainMenuPanel = new MainMenuPanel(cardLayout, mainPanel, client, gameUI);

        setupPanels();
        setupFrame();
    }

    private void handleLogin(Player player) {
        System.out.println("Player logged in: " + player.getName());
        cardLayout.show(mainPanel, "MainMenu");
        SwingUtilities.invokeLater(() -> {
            gameUI.setFocusable(true);
            gameUI.requestFocusInWindow();
        });
        setupKeyListeners();
    }

    private void setupKeyListeners() {
        gameUI.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        client.sendMoveCommand("LEFT");
                        break;
                    case KeyEvent.VK_RIGHT:
                        client.sendMoveCommand("RIGHT");
                        break;
                    case KeyEvent.VK_UP:
                        client.sendMoveCommand("UP");
                        break;
                    case KeyEvent.VK_DOWN:
                        client.sendMoveCommand("DOWN");
                        break;
                }
            }
        });
    }

    private void setupPanels() {
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(gameUI, "Game");
        mainPanel.add(shopPanel, "Shop");
    }

    private void setupFrame() {
        this.add(mainPanel);
        this.setSize(750, 778);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout.show(mainPanel, "Login");
    }

    public void initialize() {
        this.setVisible(true);
    }
}
