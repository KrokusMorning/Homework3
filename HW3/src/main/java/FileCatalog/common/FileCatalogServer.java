package FileCatalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileCatalogServer extends Remote {

    public static final String FILE_CATALOG_NAME_IN_REGISTRY = "fileCatalog";

    public String createUser(String userName, String password) throws RemoteException;

    public String login(String userName, String password, FileCatalogClient fileCatalogClient) throws RemoteException;

    public String logout(String clientID) throws RemoteException;

    public List<? extends FileDTO> listFiles(String clientID) throws RemoteException;

    public String uploadFile(String fileName, int size, String permission, String clientID) throws RemoteException;

    public FileDTO downloadFile(String paramOne, String clientID) throws RemoteException;

    public FileDTO readFile(String fileName, String clientID) throws RemoteException;

    public FileDTO updateFileName(String oldFileName, String newFileName, String clientID) throws RemoteException;

    public void deleteFile(String fileName, String clientID) throws RemoteException;



}
