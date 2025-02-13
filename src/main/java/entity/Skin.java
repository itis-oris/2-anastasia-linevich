package entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Skin {
    private String name;
    private Integer id;
    private String url;
    private Integer price;

    public Skin(Integer id, String name, String url, Integer price) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.price = price;
    }

    @Override
    public String toString() {
        return id + "-" + name + "-" + url + "-" + price;
    }
}
