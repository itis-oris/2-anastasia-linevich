package client;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;


public class GameUI extends JPanel {
    private int[][] maze;
    private int playerX, playerY;
    private Image[] walkingRightFrames;
    private Timer animationTimer;
    private Image[] currentFrames;
    private int currentFrame = 0;
    private final Image wallImage;
    private final Image pathImage;
    private final Image coinImage;
    private final Image trapImage;
    private final Image trophyImage;
    private String username = "Unknown";
    private int gameCoins = 0;
    private String skinName = "girl";  // или другое значение по умолчанию


    public void setUsername(String username) {
        this.username = username;
        repaint();
    }

    public void setGameCoins(int gameCoins) {
        this.gameCoins = gameCoins;
        repaint();
    }

    private Image resizeImage(Image originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        if (originalImage  == null) {
            throw new IllegalArgumentException("Image not found or failed to load");
        }
        return resizedImage;
    }

    public GameUI() {
        this.maze = null;
        this.playerX = 1;
        this.playerY = 1;
        try {
            wallImage = resizeImage(new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/image/wall.png"))).getImage(), 30, 30);
            pathImage = resizeImage(new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/image/path.png"))).getImage(), 30, 30);
            coinImage = resizeImage(new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/image/coin.png"))).getImage(), 30, 30);
            trapImage = resizeImage(new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/image/trap.png"))).getImage(), 30, 30);
            trophyImage = resizeImage(new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/image/trophy.png"))).getImage(), 30, 30);

            loadWalkingSprites();
            currentFrames = walkingRightFrames;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load images.", e);
        }

        animationTimer = new Timer(150, e -> {
            currentFrame = (currentFrame + 1) % currentFrames.length;
            repaint();
        });
        animationTimer.start();
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
        repaint();
    }

    public void updatePlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        animationTimer.restart();
        repaint();
    }
    public void clearCell(int x, int y) {
        if (maze != null && x >= 0 && y >= 0) {
            System.out.println("Clearing cell at: (" + x + ", " + y + ")");
            maze[y][x] = 0;
            repaint();
        } else {
            System.err.println("Invalid cell coordinates for clearing: (" + x + ", " + y + ")");
        }
    }
    private void loadWalkingSprites() {
        walkingRightFrames = new Image[4];
        for (int i = 0; i < walkingRightFrames.length; i++) {
            walkingRightFrames[i] = resizeImage(new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/image/" + skinName + i + ".png"))).getImage(), 30, 80);
        }
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
        loadWalkingSprites();  // Перезагрузить спрайты с новым именем скина
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (maze != null) {
            int cellSize = 30;
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    if (maze[i][j] == 1) {
                        g.drawImage(wallImage, j * cellSize, i * cellSize, cellSize, cellSize, this);
                    } else if (maze[i][j] == 0) {
                        g.drawImage(pathImage, j * cellSize, i * cellSize, cellSize, cellSize, this);
                    } else if (maze[i][j] == 2) {
                        g.drawImage(coinImage, j * cellSize, i * cellSize, cellSize, cellSize, this);
                    } else if (maze[i][j] == 3) {
                        g.drawImage(trapImage, j * cellSize, i * cellSize, cellSize, cellSize, this);
                    } else if (maze[i][j] == 4) {
                        g.drawImage(trophyImage, j * cellSize, i * cellSize, cellSize, cellSize, this);
                    }
                }
            }

            g.drawImage(walkingRightFrames[currentFrame], playerX * cellSize, playerY * cellSize, cellSize, cellSize, this);
            int panelHeight = getHeight();
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.BLACK);
            String playerText = "Player: " + username;
            String coinsText = "Coins: " + gameCoins;
            FontMetrics fm = g.getFontMetrics();
            int playerWidth = fm.stringWidth(playerText);
            int coinsWidth = fm.stringWidth(coinsText);
            int textHeight = fm.getHeight();
            int textPadding = 20;
            int x = 10;
            int yPlayer = panelHeight - textPadding - textHeight;
            int yCoins = panelHeight - textPadding;
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(x - 5, yPlayer - textHeight + 3, playerWidth + 10, textHeight);
            g.fillRect(x - 5, yCoins - textHeight + 3, coinsWidth + 10, textHeight);
            g.setColor(Color.BLACK);
            g.drawString(playerText, x, yPlayer);
            g.drawString(coinsText, x, yCoins);
        }
    }
}
