package client;

import entity.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class LoginPanel extends JPanel {
    private final Image wallImage;

    public LoginPanel(OnLoginCallback callback, CardLayout cardLayout, JPanel mainPanel, MazeClient client) {

        Image originalWallImage = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/image/wall.png"))).getImage();
        wallImage = resizeImage(originalWallImage, 30, 30);
        setLayout(new GridBagLayout());
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(true);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        formPanel.setBackground(new Color(255, 255, 255, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton loginButton = createStyledButton("Войти");
        JButton registerButton = createStyledButton("Зарегистрироваться");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);
        gbc.gridy = 3;
        formPanel.add(registerButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(formPanel, gbc);

        loginButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (!playerName.isEmpty() && !password.isEmpty()) {
                try {
                    String response = client.authenticate(playerName, password);
                    if (client.loggedIn) {
                        callback.onLogin(new Player(playerName));
                        cardLayout.show(mainPanel, "MainMenu");
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Server error!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter username and password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                RegisterPanel registerPanel = new RegisterPanel(callback, cardLayout, mainPanel, client);
                mainPanel.add(registerPanel, "Register");
                cardLayout.show(mainPanel, "Register");
            });
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(250, 30));
        return button;
    }

    public interface OnLoginCallback {
        void onLogin(Player player);
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
}

class RegisterPanel extends JPanel {
    private final Image wallImage;

    public RegisterPanel(LoginPanel.OnLoginCallback callback, CardLayout cardLayout, JPanel mainPanel, MazeClient client) {
        Image originalWallImage = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/image/wall.png"))).getImage();
        wallImage = resizeImage(originalWallImage, 30, 30);
        setLayout(new GridBagLayout());
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(true);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        formPanel.setBackground(new Color(255, 255, 255, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel emailLabel = new JLabel("Почта:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton registerButton = createStyledButton("Зарегистрироваться");
        JButton backButton = createStyledButton("Назад");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);
        gbc.gridy = 4;
        formPanel.add(backButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(formPanel, gbc);
        registerButton.addActionListener((ActionEvent e) -> {
            String username = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                try {
                    String response = client.register(username, email, password);
                    if (client.loggedIn) {
                        JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        callback.onLogin(new Player(username));
                        cardLayout.show(mainPanel, "Login");
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(250, 30));
        return button;
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
}
