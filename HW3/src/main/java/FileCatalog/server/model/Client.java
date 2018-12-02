package FileCatalog.server.model;

import FileCatalog.common.FileCatalogClient;

import java.rmi.RemoteException;

public class Client {
    private final FileCatalogClient client;
    private final ClientManager clientManager;
    private String username;

    public Client(String username, FileCatalogClient client, ClientManager clientManager) {
        this.username = username;
        this.client = client;
        this.clientManager = clientManager;
    }

    public void sendMessage(String msg) {
        try {
            client.receive(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }
}
