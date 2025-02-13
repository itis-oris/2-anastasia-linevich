package server;

import dao.PlayerDao;
import entity.Skin;
import utils.ConnectionProvider;
import utils.DbException;
import utils.GameProtocol;
import entity.Player;
import shared.Maze;
import utils.PasswordUtils;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ConcurrentMap<String, Player> players;
    private final Maze maze;
    private PrintWriter out;
    private BufferedReader in;
    private Player player;

    public ClientHandler(Socket clientSocket, ConcurrentMap<String, Player> players, Maze maze) {
        this.clientSocket = clientSocket;
        this.players = players;
        this.maze = maze;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void sendMazeToClient() throws IOException {
            out.println("MAZE_START");
            out.println("MAZE_START");
            try {
                Thread.sleep(500);
                out.println(maze.toString());
                out.println("MAZE_END");
                out.flush();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
    }

    private void sendPlayerStartPositionToClient(Player player) throws IOException {
        if (player != null) {
            out.println(GameProtocol.playerPositionResponse(maze.getStartX(), maze.getStartY()));
            player.setX(maze.getStartX());
            player.setY(maze.getStartY());
            out.flush();
        }
    }

    private void handleLogin(String data) {
        String[] parts = data.split(",");
        if (parts.length != 2) {
            out.println(GameProtocol.loginResponse(false));
            return;
        }
        String username = parts[0].trim();
        String password = parts[1].trim();
        try {
            PlayerDao playerDao = new PlayerDao(ConnectionProvider.getInstance());
            Player player = playerDao.getPlayerByName(username);
            if (PasswordUtils.verifyPassword(password, player.getPassword())) {
                this.player = player;
                players.put(username, player);
                out.println(GameProtocol.loginResponse(true));
                out.flush();
                sendMazeToClient();
                sendPlayerStartPositionToClient(player);
                System.out.println("Игрок подключился: " + username);
                player.setName(username);
                out.println(GameProtocol.playerNameResponse(player.getName()));
                out.flush();
                out.println(GameProtocol.updateCoinsResponse(player.getCoins()));
                out.flush();
            } else {
                out.println(GameProtocol.loginResponse(false));
            }
        } catch (DbException | IOException e) {
            out.println(GameProtocol.errorResponse("Ошибка базы данных при авторизации."));
            System.err.println("Ошибка базы данных при авторизации: " + e.getMessage());
        }
    }

    private void handleRegister(String data) {
        String[] parts = data.split(",");
        if (parts.length != 3) {
            out.println(GameProtocol.registerResponse(false));
            return;
        }
        String username = parts[0].trim();
        String email = parts[1].trim();
        String password = parts[2].trim();
        try {
            String hashedPassword = PasswordUtils.hashPassword(password);
            PlayerDao playerDao = new PlayerDao(ConnectionProvider.getInstance());
            Player newPlayer = new Player(username, email, hashedPassword);
            playerDao.savePlayer(newPlayer);
            out.println(GameProtocol.registerResponse(true));
            System.out.println("Player registered: " + username);
        } catch (DbException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            out.println(GameProtocol.errorResponse("Error during registration."));
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    private void processMessage(String message) {
        if (message.startsWith(GameProtocol.LOGIN_REQUEST)) {
            handleLogin(message.substring(GameProtocol.LOGIN_REQUEST.length()));
        } else if (message.startsWith(GameProtocol.REGISTER_REQUEST)) {
            handleRegister(message.substring(GameProtocol.REGISTER_REQUEST.length()));
        } else if (message.startsWith(GameProtocol.SHOP_INFO_REQUEST)) {
                handleShopInfoRequest();
        } else if (message.startsWith(GameProtocol.PURCHASE_SKIN_REQUEST)) {
            handleBuy(message.substring(GameProtocol.PURCHASE_SKIN_REQUEST.length()));
        }if (message.startsWith(GameProtocol.SELECT_SKIN_REQUEST)) {
            System.out.println(message);
            String[] parts = message.split(":");
            int skinId = Integer.parseInt(parts[1].trim());
            System.out.println("Игрок выбрал персонажа с ID: " + skinId);
            handleSelectSkinRequest(skinId);
        } else if (message.startsWith(GameProtocol.MOVE_PREFIX)) {
            if (player != null) {
                handleMove(message.substring(GameProtocol.MOVE_PREFIX.length()));
            }
        } else if (message.startsWith(GameProtocol.PICKUP_PREFIX)) {
            if (player != null) {
                handlePickup();
            }
        }
    }

    private void handleMove(String direction) {
        System.out.println("MOVE: " + direction);
        int newX = player.getX();
        int newY = player.getY();
        switch (direction) {
            case "UP":
                newY--;
                break;
            case "DOWN":
                newY++;
                break;
            case "LEFT":
                newX--;
                break;
            case "RIGHT":
                newX++;
                break;
            default:
                out.println("Invalid direction.");
                out.flush();
                return;
        }
        if (maze.isWalkable(newX, newY)) {
            boolean not_trapped = true;
            if (activateTrap(player, newX, newY)) {
                not_trapped = false;
            } else if (collectCoin(player, newX, newY)) {

            } else if (checkTrophy(newX, newY)) {
                player.addCoins(20);
                maze.clearCell(newX, newY);
                sendUpdateToClient(newX, newY);
                try {
                    updatePlayerInDatabase(player);
                } catch (DbException e) {
                    System.err.println("Failed to update player in database: " + e.getMessage());
                }
                out.println("You found the trophy! You win!");
            }
            if (not_trapped) {
                player.setPosition(newX, newY);
                out.println(GameProtocol.playerPositionResponse(newX, newY));
                out.flush();
            }
        } else {
            out.flush();
        }
    }

    public boolean collectCoin(Player player, int x, int y) {
        if (maze.isCoin(x, y)) {
            player.addCoins(1);
            maze.clearCell(x, y);
            sendUpdateToClient(x, y);
            out.println(GameProtocol.updateCoinsResponse(player.getCoins()));
            out.flush();
            try {
                updatePlayerInDatabase(player);
            } catch (DbException e) {
                System.err.println("Failed to update player in database: " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private void updatePlayerInDatabase(Player player) throws DbException {
        if (player.getId() == null) {
            throw new DbException("Player ID is null. Cannot update player in database.");
        }
        PlayerDao playerDao = new PlayerDao(ConnectionProvider.getInstance());
        playerDao.updatePlayer(player);
    }

    public void handleBuy(String data){
        String[] parts = data.split(",");
        if (parts.length != 1) {
            System.out.println("Error: handleBuy ");
            return;
        }
        Integer id = Integer.valueOf(parts[0].trim());
        System.out.println(id);
        try {
            PlayerDao playerDao = new PlayerDao(ConnectionProvider.getInstance());
            Integer currentPlayer = player.getId();
            Integer currentCoins = player.getCoins();
            Skin currentSkin = playerDao.getSkinById(id);
            System.out.println(currentPlayer);
            System.out.println(currentCoins);
            System.out.println(currentSkin);
            if(player.getCoins() - currentSkin.getPrice() >=0) {
                player.setCoins(player.getCoins() - currentSkin.getPrice());
                playerDao.updatePlayer(player);
                System.out.println("Player buy skin");
                playerDao.addPlayerSkin(currentPlayer, currentSkin.getId());
            }else {
                System.out.println("Недостаточно средств");
            }
        } catch (DbException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    public boolean activateTrap(Player player, int x, int y) {
        if (maze.isTrap(x, y)) {
            player.setPosition(maze.getStartX(), maze.getStartY());
            out.println(GameProtocol.playerPositionResponse(maze.getStartX(), maze.getStartY()));
            maze.clearCell(x, y);
            sendUpdateToClient(x, y);
            out.flush();
            return true;
        }
        return false;
    }

    private void sendUpdateToClient(int x, int y) {
        out.println(GameProtocol.clearCellResponse(x, y));
    }

    public boolean checkTrophy(int x, int y) {
        return maze.isTrophy(x, y);
    }

    private void handlePickup() {
        out.println("You picked up an item!");
        out.flush();
    }

    private void handleSelectSkinRequest(Integer skinId) {
        try {
            PlayerDao playerDao = new PlayerDao(ConnectionProvider.getInstance());
            playerDao.updatePlayerSkin(player.getId(), skinId);
            Skin selectedSkin = playerDao.getSkinById(skinId);
            String playerName = selectedSkin != null ? selectedSkin.getName() : "boy";
            out.println(GameProtocol.SELECT_PLAYER_SKIN_REQUEST + playerName);
        } catch (DbException e) {
            out.println(GameProtocol.ERROR_RESPONSE + "Error updating skin");
        }
    }

    private void handleShopInfoRequest() {
        if (player == null) {
            out.println(GameProtocol.errorResponse("Необходима авторизация."));
            return;
        }
        try {
            PlayerDao playerDao = new PlayerDao(ConnectionProvider.getInstance());
            Player currentPlayer = playerDao.getPlayerById(player.getId());

            if (currentPlayer != null) {
                List<Integer> purchasedSkins = playerDao.getPlayerSkins(currentPlayer.getId());
                List<Skin> allSkins = playerDao.getSkins();
                currentPlayer.setPurchasedSkins(purchasedSkins);
                int coins = currentPlayer.getCoins();
                out.println(GameProtocol.shopInfoResponse(coins, purchasedSkins, allSkins));
            } else {
                out.println(GameProtocol.errorResponse("Игрок не найден."));
            }
        } catch (Exception e) {
            out.println(GameProtocol.errorResponse("Ошибка получения данных магазина."));
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
