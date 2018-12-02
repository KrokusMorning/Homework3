package FileCatalog.server.model;

import FileCatalog.common.FileDTO;

import javax.persistence.*;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "findFileByName",
                query = "SELECT file FROM File file WHERE file.name LIKE :fileName"
        ),
        @NamedQuery(
                name = "findAllFiles",
                query = "SELECT file FROM File file"
        ),
        @NamedQuery(
                name = "updateFileByName",
                query = "UPDATE File file SET file.name = :newFileName WHERE file.name LIKE :oldFileName"
        ),
        @NamedQuery(
                name = "deleteFileByName",
                query = "DELETE FROM File file WHERE file.name LIKE :fileName"
        ),
})

@Entity(name = "File")
public class File implements Serializable, FileDTO {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private int size;

    @ManyToOne()
    @JoinColumn(name = "owner", nullable = false)
    private User user;

    @Id
    @Column(name = "permissions", nullable = false)
    private String permission;

    public File() {
        this.name = null;
        this.user = null;
        this.permission = null;
        this.size = 0;
    }

    public File(String name, int size, String permission, User user) {
        this.name = name;
        this.size = size;
        this.user = user;
        this.permission = permission;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getUser() {
        return user.getUsername();
    }

    @Override
    public String getPermission() {
        return permission;
    }


}

