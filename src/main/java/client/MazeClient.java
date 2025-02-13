package client;


import entity.Skin;
import lombok.Setter;
import utils.GameProtocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MazeClient {
    private final GameUI gameUI;
    @Setter
    private ShopPanel shopPanel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public boolean loggedIn = false;
    public String username;
    public Integer GameCoin;
    private String nowSkin;

    public MazeClient(String host, int port, GameUI gameUI) {
        if (gameUI == null) {
            throw new IllegalArgumentException("GameUI не может быть null");
        }
        this.gameUI = gameUI;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            closeResources();
        }
    }

    private void listenToServer() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from server: " + message);
                processServerMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error while reading from server: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void processServerMessage(String message) {
        message = message.trim();
        if (message.startsWith("LOGIN_RESPONSE:SUCCESS")) {
            loggedIn = true;
        } else if (message.startsWith(GameProtocol.PLAYER_NAME)) {
            handleUsername(message);
        } else if (message.startsWith(GameProtocol.SHOP_INFO_RESPONSE)) {
            handleShopInfoResponse(message);
        } else if (message.startsWith("MAZE_START")) {
            StringBuilder mazeData = new StringBuilder();
            try {
                while (!(message = in.readLine()).equals("MAZE_END")) {
                    mazeData.append(message.trim()).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading maze data: " + e.getMessage());
                return;
            }
            String[] lines = mazeData.toString().split("\n");
            if (lines.length == 0 || lines[0].isEmpty()) {
                System.err.println("Received empty maze data.");
                return;
            }
            int numCols = lines[0].length();
            int[][] maze = new int[lines.length][numCols];
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].length() != numCols) {
                    System.err.println("Error: Maze rows have different lengths.");
                    return;
                }
                for (int j = 0; j < lines[i].length(); j++) {
                    char cell = lines[i].charAt(j);
                    switch (cell) {
                        case '#':
                            maze[i][j] = 1;
                            break;
                        case '.':
                            maze[i][j] = 0;
                            break;
                        case 'M':
                            maze[i][j] = 2;
                            break;
                        case 'T':
                            maze[i][j] = 3;
                            break;
                        case 'C':
                            maze[i][j] = 4;
                            break;
                        default:
                            maze[i][j] = 0;
                            break;
                    }
                }
            }

            if (gameUI != null) {
                gameUI.setMaze(maze);
            } else {
                System.err.println("GameUI is null. Cannot update maze.");
            }
        }else if (message.startsWith(GameProtocol.SELECT_PLAYER_SKIN_REQUEST)) {
                handleSkinChange(message);  // обработка смены скина
        } else if (message.startsWith(GameProtocol.PLAYER_POSITION)) {
            try {
                String positionData = message.substring(GameProtocol.PLAYER_POSITION.length()).trim();
                if (positionData.isEmpty()) {
                    System.err.println("Position data is empty.");
                    return;
                }

                String[] parts = positionData.split("\\s+");
                if (parts.length != 2) {
                    System.err.println("Invalid format: Expected two values but found " + parts.length);
                    return;
                }

                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());

                gameUI.updatePlayerPosition(x, y);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format for position: " + e.getMessage());
            }
        } else if (message.startsWith("UPDATE_COINS")) {
            handleCoins(message);
        } else if (message.startsWith(GameProtocol.CLEAR_CELL)) {
            try {
                String positionData = message.substring(GameProtocol.CLEAR_CELL.length()).trim();
                if (positionData.isEmpty()) {
                    System.err.println("Position data is empty.");
                    return;
                }

                String[] parts = positionData.split("\\s+");
                if (parts.length != 2) {
                    System.err.println("Invalid format: Expected two values but found " + parts.length);
                    return;
                }
                int clearX = Integer.parseInt(parts[0].trim());
                int clearY = Integer.parseInt(parts[1].trim());
                gameUI.clearCell(clearX, clearY);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in CLEAR_CELL: " + e.getMessage());
            }
        } else {
            System.err.println("Unknown command received: " + message);
        }
    }

    public String authenticate(String username, String password) throws IOException {
        String request = GameProtocol.loginRequest(username, password);
        out.println(request);
        return in.readLine();
    }

    public String register(String username, String email, String password) throws IOException {
        String request = GameProtocol.registerRequest(username, email, password);
        out.println(request);
        return in.readLine();
    }

    public void sendMoveCommand(String direction) {
        if (out != null) {
            out.println(GameProtocol.MOVE_PREFIX + direction);
        } else {
            System.err.println("Connection to server is not established.");
        }
    }
    public void requestShopInfo() {
        try {
            out.println(GameProtocol.SHOP_INFO_REQUEST);
            out.flush();
        } catch (Exception e) {
            System.err.println("Ошибка при запросе данных магазина: " + e.getMessage());
        }
    }

    public void handleUsername(String response) {
        String[] parts = response.split(":");
        username = parts[1];
        System.out.println("Username: " + username);

        if (gameUI != null) {
            gameUI.setUsername(username);
        }
    }

    public void handleCoins(String response) {
        String[] parts = response.split(":");
        GameCoin = Integer.parseInt(parts[1]);
        if (gameUI != null) {
            gameUI.setGameCoins(GameCoin);
        }
    }

    public void handleShopInfoResponse(String response) {
        String[] parts = response.split(":");
        if (parts.length < 3) {
            System.err.println("Некорректный ответ магазина: " + response);
            return;
        }
        try {
            int coins = Integer.parseInt(parts[1]);
            List<Integer> purchasedSkins = parts[2].isEmpty()
                    ? new ArrayList<>()
                    : Arrays.stream(parts[2].split(",")).map(Integer::parseInt).toList();
            List<Skin> allSkins = parts.length > 3
                    ? Arrays.stream(parts[3].split(";"))
                    .map(s -> {
                        String[] skinParts = s.split("-");
                        return new Skin(
                                Integer.parseInt(skinParts[0]),
                                skinParts[1],
                                skinParts[2],
                                Integer.parseInt(skinParts[3])
                        );
                    })
                    .toList()
                    : new ArrayList<>();

            shopPanel.updateShopInfo(coins, purchasedSkins, allSkins);
        } catch (Exception e) {
            System.err.println("Ошибка разбора данных магазина: " + response);
        }
    }

    private void handleSkinChange(String message) {
        String playerName = message.substring(GameProtocol.SELECT_PLAYER_SKIN_REQUEST.length()).trim();
        nowSkin = playerName;
        System.out.println("Текущий скин: " + nowSkin);
        if (gameUI != null) {
            gameUI.setSkinName(nowSkin);
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error while closing resources: " + e.getMessage());
        }
    }

    public void selectSkin(int skinId) {
        out.println(GameProtocol.SELECT_SKIN_REQUEST + skinId);
        out.flush();
    }


    public void purchaseSkin(int skinId) {
        String purchaseRequest = GameProtocol.purchaseSkinRequest(skinId);
        out.println(purchaseRequest);
        out.flush();
    }
}











