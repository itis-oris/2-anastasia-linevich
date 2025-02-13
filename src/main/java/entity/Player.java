package entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Player {
    private String name;
    private Integer id;
    private String username;
    private String password;
    public int x, y;
    private int coins;
    private String email;
    @Getter
    private List<Integer> purchasedSkins = new ArrayList<>();

    public Player(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Player(String username) {
        this.username = username;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}






