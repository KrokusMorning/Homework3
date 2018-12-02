package FileCatalog.server.startup;

import FileCatalog.server.controller.Controller;
import FileCatalog.common.FileCatalogServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {

        Server server = new Server();
        try {
            server.setupRMI();
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void setupRMI() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller controller = new Controller();
        String fileCatalogName = FileCatalogServer.FILE_CATALOG_NAME_IN_REGISTRY;
        Naming.rebind(fileCatalogName, controller);
    }
}
