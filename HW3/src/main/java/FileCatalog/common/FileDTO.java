package FileCatalog.common;

import java.rmi.RemoteException;

public interface FileDTO {

    public String getName() throws RemoteException;
    public int getSize() throws RemoteException;
    public String getUser() throws RemoteException;
    public String getPermission() throws RemoteException;
}
