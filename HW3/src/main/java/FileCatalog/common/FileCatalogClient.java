package FileCatalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileCatalogClient extends Remote {

    void receive(String msg) throws RemoteException;
}
