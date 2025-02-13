package utils;

import entity.Skin;

import java.util.List;
import java.util.stream.Collectors;

public class GameProtocol {
    public static final String MOVE_PREFIX = "MOVE:";
    public static final String PICKUP_PREFIX = "PICKUP:";
    public static final String PLAYER_POSITION = "PLAYER_POSITION:";
    public static final String PLACE_TRAP = "PLACE_TRAP";
    public static final String CLEAR_CELL = "CLEAR_CELL:"; // Новая команда
    public static final String LOGIN_REQUEST = "LOGIN_REQUEST:";
    public static final String REGISTER_REQUEST = "REGISTER_REQUEST:";
    public static final String LOGIN_RESPONSE = "LOGIN_RESPONSE:";
    public static final String REGISTER_RESPONSE = "REGISTER_RESPONSE:";
    public static final String ERROR_RESPONSE = "ERROR:";
    public static final String SHOP_INFO_REQUEST = "SHOP_INFO_REQUEST";
    public static final String SHOP_INFO_RESPONSE = "SHOP_INFO_RESPONSE";
    public static final String PURCHASE_SKIN_REQUEST = "PURCHASE_SKIN_REQUEST:";
    public static final String PLAYER_NAME = "PLAYER_NAME:";
    public static final String UPDATE_COINS = "UPDATE_COINS:";
    public static final String SELECT_SKIN_REQUEST = "SELECT_SKIN_REQUEST:";
    public static final String SELECT_PLAYER_SKIN_REQUEST = "SELECT_SKIN_REQUEST:";

    public static String shopInfoResponse(int coins, List<Integer> purchasedSkins, List<Skin> allSkins) {
        String purchasedSkinsStr = String.join(",", purchasedSkins.stream().map(String::valueOf).toList());
        String allSkinsStr = allSkins.stream()
                .map(Skin::toString)
                .collect(Collectors.joining(";"));
        return SHOP_INFO_RESPONSE + ":" + coins + ":" + purchasedSkinsStr + ":" + allSkinsStr;
    }

    public static String selectSkinRequest(int skinId) {
        return SELECT_SKIN_REQUEST + skinId;
    }

    public static String playerPositionResponse(int x, int y) {
        return PLAYER_POSITION + " " + x + " " + y;
    }

    public static String moveResponse(int x, int y) {
        return MOVE_PREFIX + " " + x + "," + y;
    }

    public static String clearCellResponse(int x, int y) {
        return CLEAR_CELL + " " + x + " " + y;
    }
    public static String loginRequest(String username, String password) {
        return LOGIN_REQUEST + username + "," + password;
    }

    public static String registerRequest(String username, String email, String password) {
        return REGISTER_REQUEST + username + "," + email + "," + password;
    }

    public static String loginResponse(boolean success) {
        return LOGIN_RESPONSE + (success ? "SUCCESS" : "FAIL");
    }

    public static String registerResponse(boolean success) {
        return REGISTER_RESPONSE + (success ? "SUCCESS" : "FAIL");
    }

    public static String playerNameResponse(String playerName) {
        return PLAYER_NAME + playerName;
    }

    public static String updateCoinsResponse(int coins) {
        return UPDATE_COINS + coins;
    }

    public static String errorResponse(String message) {
        return ERROR_RESPONSE + message;
    }

    public static String purchaseSkinRequest(int skinId) {
        return PURCHASE_SKIN_REQUEST + skinId;
    }
}
















