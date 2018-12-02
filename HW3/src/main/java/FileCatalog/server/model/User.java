package FileCatalog.server.model;

import FileCatalog.common.UserDTO;

import java.io.Serializable;
import javax.persistence.*;

@NamedQueries({
        @NamedQuery(
                name = "verifyUser",
                query = "SELECT user FROM User user WHERE user.name LIKE :userName AND user.password LIKE :password"
        )
        ,
        @NamedQuery(
                name = "findUserByName",
                query = "SELECT user FROM User user WHERE user.name LIKE :userName"
        )
})

@Entity(name = "User")
public class User implements Serializable, UserDTO {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    public User() {
        this.name = null;
        this.password = null;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return name;
    }
}
