package FileCatalog.client.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import FileCatalog.client.view.Interpreter;
import FileCatalog.common.FileCatalogServer;

public class Main {
    public static void main(String[] args) throws RemoteException {
        FileCatalogServer fileCatalogServer = null;
        try {
            fileCatalogServer = (FileCatalogServer) Naming.lookup(FileCatalogServer.FILE_CATALOG_NAME_IN_REGISTRY);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        new Interpreter().start(fileCatalogServer);
    }
}
