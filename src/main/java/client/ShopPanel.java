package client;

import entity.Skin;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import  java.awt.image.BufferedImage;
import java.util.Objects;


public class ShopPanel extends JPanel {
    private CardLayout cardLayout;
    private final int[] prices = {100, 100, 100};
    private final boolean[] purchased = {false, false, false};
    private int coins;
    private JLabel coinsLabel;
    private final MazeClient client;
    private List<Skin> allSkins;

    public ShopPanel(CardLayout cardLayout, JPanel mainPanel, MazeClient client, GameUI gameUI) {
        this.cardLayout = cardLayout;
        this.client = client;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        coinsLabel = new JLabel("Ваши монеты: " + coins);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(coinsLabel, gbc);
        gbc.gridwidth = 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image background = new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/wall.png"))).getImage();
        Image resizedBackground = resizeImage(background, 30, 30);
        int tileSize = 30;
        for (int x = 0; x < getWidth(); x += tileSize) {
            for (int y = 0; y < getHeight(); y += tileSize) {
                g.drawImage(resizedBackground, x, y, tileSize, tileSize, this);
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

    private void handleButtonClick(JButton button, int index) {
        int skinId = index + 1;
        System.out.println("Какой скин:" + purchased[skinId - 1] + skinId);
        if (purchased[skinId - 1]) {
            JOptionPane.showMessageDialog(this, "Персонаж " + skinId + " выбран!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            client.selectSkin(skinId);
            button.setText("Выбран");
        } else if (coins >= prices[index]) {
            coins -= prices[index];
            purchased[skinId - 1] = true;
            button.setText("Выбрать");
            coinsLabel.setText("Ваши монеты: " + coins);
            JOptionPane.showMessageDialog(this, "Персонаж " + skinId + " куплен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            client.purchaseSkin(skinId);
            client.selectSkin(skinId);
        } else {
            JOptionPane.showMessageDialog(this, "Недостаточно монет!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void updateShopInfo(int coins, List<Integer> purchasedSkins, List<Skin> allSkins) {
        this.coins = coins;
        this.allSkins = allSkins;

        Arrays.fill(purchased, false);
        for (int skinId : purchasedSkins) {
            if (skinId > 0 && skinId <= purchased.length) {
                purchased[skinId - 1] = true;
            }
        }

        coinsLabel.setText("Ваши монеты: " + coins);
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        coinsLabel = new JLabel("Ваши монеты: " + coins);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(coinsLabel, gbc);
        gbc.gridwidth = 1;
        for (int i = 0; i < allSkins.size(); i++) {
            Skin skin = allSkins.get(i);
            JPanel characterPanel = new JPanel();
            characterPanel.setLayout(new BoxLayout(characterPanel, BoxLayout.Y_AXIS));
            characterPanel.setOpaque(true);
            characterPanel.setBackground(Color.LIGHT_GRAY);
            characterPanel.setPreferredSize(new Dimension(300, 350));
            characterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            String modifiedUrl = "/image/" + skin.getUrl() + "0.png";
            URL resourceUrl = getClass().getResource(modifiedUrl);
            if (resourceUrl != null) {
                ImageIcon characterImage = new ImageIcon(resourceUrl);
                Image resizedImage = resizeImage(characterImage.getImage(), 200, 200);
                JLabel characterImageLabel = new JLabel(new ImageIcon(resizedImage));
                characterImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                characterPanel.add(characterImageLabel);
            }
            JLabel characterLabel = new JLabel(skin.getName());
            characterLabel.setFont(new Font("Arial", Font.BOLD, 18));
            characterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            characterPanel.add(characterLabel);
            JButton actionButton;
            if (purchasedSkins.contains(skin.getId())) {
                actionButton = createStyledButton("Выбрать");
            } else {
                actionButton = createStyledButton("Купить за " + skin.getPrice() + " монет");
            }
            actionButton.setPreferredSize(new Dimension(220, 60));
            final int index = i;
            actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionButton.addActionListener(e -> handleButtonClick(actionButton, index));
            characterPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            characterPanel.add(actionButton);

            gbc.gridx = i % 3;
            gbc.gridy = 1 + i / 3;
            add(characterPanel, gbc);
        }

        JButton backButton = createStyledButton("Назад");
        backButton.setPreferredSize(new Dimension(250, 60));
        backButton.addActionListener(e -> cardLayout.show(this.getParent(), "MainMenu"));
        gbc.gridx = 0;
        gbc.gridy += 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 40, 0, 0);
        add(backButton, gbc);

        revalidate();
        repaint();
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
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }
}








