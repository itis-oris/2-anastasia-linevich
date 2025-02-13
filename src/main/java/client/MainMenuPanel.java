package client;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class MainMenuPanel extends JPanel {
    private final Image wallImage;

    public MainMenuPanel(CardLayout cardLayout, JPanel mainPanel, MazeClient client, GameUI gameUI) {
        Image originalWallImage = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/image/wall.png"))).getImage();
        wallImage = resizeImage(originalWallImage, 30, 30);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JButton playButton = createStyledButton("Играть");
        JButton shopButton = createStyledButton("Магазин");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(playButton, gbc);
        gbc.gridy = 1;
        add(shopButton, gbc);
        playButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Game");
            SwingUtilities.invokeLater(() -> {
                gameUI.setFocusable(true);
                gameUI.requestFocusInWindow();
            });
        });
        shopButton.addActionListener(e -> {
            client.requestShopInfo();
            cardLayout.show(mainPanel, "Shop");
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tileSize = 30;
        for (int x = 0; x < getWidth(); x += tileSize) {
            for (int y = 0; y < getHeight(); y += tileSize) {
                g.drawImage(wallImage, x, y, tileSize, tileSize, this);
            }
        }
    }

    private Image resizeImage(Image originalImage, int width, int height) {
        if (originalImage == null) {
            throw new IllegalArgumentException("Image not found or failed to load");
        }
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }
}


