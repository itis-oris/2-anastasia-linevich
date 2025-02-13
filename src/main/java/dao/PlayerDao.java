package dao;


import entity.Player;
import entity.Skin;
import utils.ConnectionProvider;
import utils.DbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDao {
    private final ConnectionProvider connectionProvider;

    public PlayerDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Player getPlayerByName(String name) throws DbException {
        String sql = "SELECT * FROM Player WHERE Name = ?";
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error while fetching user by username", e);
        }
        return null;
    }

    public Player getPlayerByEmail(String email) throws DbException {
        String sql = "SELECT * FROM Player WHERE Email = ?";
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error while fetching user by email", e);
        }
        return null;
    }

    public void savePlayer(Player player) throws DbException {
        String sql = "INSERT INTO Player (Name, Email, Password) VALUES (?, ?, ?)";
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, player.getUsername());
            preparedStatement.setString(2, player.getEmail());
            preparedStatement.setString(3, player.getPassword());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Error saving user", e);
        }
    }

    private Player mapResultSetToUser(ResultSet resultSet) throws SQLException {
        Player player = new Player();
        player.setId(resultSet.getInt("Id"));
        player.setUsername(resultSet.getString("Name"));
        player.setPassword(resultSet.getString("Password"));
        player.setEmail(resultSet.getString("Email"));
        player.setCoins(Integer.parseInt(resultSet.getString("Coins")));
        return player;
    }

    public Player getPlayerById(int id) throws DbException {
        String sql = "SELECT * FROM Player WHERE Id = ?";
        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        connection.commit();
                        return mapResultSetToUser(resultSet);
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new DbException("Error fetching user by ID", e);
            }
        } catch (SQLException e) {
            throw new DbException("Error while managing connection", e);
        }
        return null;
    }


    public void updatePlayer(Player player) throws DbException {
        String sql = "UPDATE Player SET Name = ?,  Email = ?, Coins = ? WHERE Id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, player.getUsername());
            preparedStatement.setString(2, player.getEmail());
            preparedStatement.setString(3, String.valueOf(player.getCoins()));
            preparedStatement.setInt(4, player.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Ошибка обновления пользователя", e);
        }
    }

    public List<Integer> getPlayerSkins(int playerId) throws DbException {
        String sql = "SELECT SkinId FROM Player_Skin WHERE PlayerId = ?";
        List<Integer> skinIds = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, playerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    skinIds.add(resultSet.getInt("SkinId"));
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error fetching player's skins", e);
        }

        return skinIds;
    }

    public void addPlayerSkin(int playerId, int skinId) throws DbException {
        String sql = "INSERT INTO Player_Skin (PlayerId, SkinId) VALUES (?, ?)";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, skinId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Error adding skin for player", e);
        }
    }

    public List<Skin> getSkins() throws DbException {
        String sql = "SELECT * FROM Skin";
        List<Skin> skins = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    skins.add(mapResultSetToSkin(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error fetching player's skins", e);
        }
        return skins;
    }



    public Skin getSkinById(int id) throws DbException {
        String sql = "SELECT * FROM Skin WHERE Id = ?";
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToSkin(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error while fetching user by username", e);
        }
        return null;
    }

    public void updatePlayerSkin(int playerId, int skinId) throws DbException {
        String sql = "UPDATE Player SET SkinId = ? WHERE Id = ?";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, skinId);
            preparedStatement.setInt(2, playerId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Error updating player skin", e);
        }
    }

    private Skin mapResultSetToSkin(ResultSet resultSet) throws SQLException {
        Skin skin = new Skin();
        skin.setId(resultSet.getInt("Id"));
        skin.setName(resultSet.getString("Name"));
        skin.setUrl(resultSet.getString("ImageUrl"));
        skin.setPrice(resultSet.getInt("Price"));
        return skin;
    }

}
