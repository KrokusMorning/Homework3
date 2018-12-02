package FileCatalog.server.model;

import FileCatalog.common.FileCatalogClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientManager {

    private final Map<String, Client> clients = Collections.synchronizedMap(new HashMap<String, Client>());

    public String createClient(FileCatalogClient remoteNode, User user) {
        Client client = new Client(user.getUsername(),
                remoteNode, this);
        clients.put(user.getUsername(), client);
        return user.getUsername();
    }

    public Client findClient(String name) {
        return clients.get(name);
    }

    public void removeClient(String name) {
        clients.remove(name);
    }

}
