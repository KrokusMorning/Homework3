package FileCatalog.server.controller;

import FileCatalog.common.FileCatalogClient;
import FileCatalog.common.FileCatalogServer;
import FileCatalog.common.FileDTO;
import FileCatalog.server.integration.FileCatalogDAO;
import FileCatalog.server.model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Controller extends UnicastRemoteObject implements FileCatalogServer {
    private final FileCatalogDAO fileCatalogDb;
    private final ClientManager clientManager = new ClientManager();

    public Controller() throws RemoteException {
        super();
        this.fileCatalogDb = new FileCatalogDAO();
    }

    @Override
    public String createUser(String userName, String password){
            if (fileCatalogDb.findUserByName(userName, true) != null) {
                return "userTaken";
            }
            fileCatalogDb.createUser(new User(userName, password));
            return "OK";
    }

    @Override
    public String login(String userName, String password, FileCatalogClient fileCatalogClient){
        User user = fileCatalogDb.verify(userName, password);
        if(user != null){
            return clientManager.createClient(fileCatalogClient, user);
        }
        return null;
    }

    @Override
    public String logout(String clientID){
        if(clientID.equals("")){
            return null;
        }
        clientManager.removeClient(clientID);
        return "OK";
    }

    @Override
    public List<? extends FileDTO> listFiles(String clientID) throws RemoteException{
        if(clientManager.findClient(clientID) == null){
            return null;
        }
        return fileCatalogDb.findAllFiles(true);
    }

    @Override
    public String uploadFile(String fileName, int size, String permission, String clientID) throws RemoteException{
        if(clientManager.findClient(clientID) == null){
            return null;
        }
        if (fileCatalogDb.findFileByName(fileName, true) != null) {
            return null;
        }
        Client client = clientManager.findClient(clientID);
        User user = fileCatalogDb.findUserByName(client.getUsername(), true);
        fileCatalogDb.uploadFile(new File(fileName, size, permission, user));
        return "OK";
    }

    @Override
    public FileDTO downloadFile(String fileName, String clientID) {
        try{
            if(clientManager.findClient(clientID) == null){
                return null;
            }
            File fileToDownload = fileCatalogDb.findFileByName(fileName, true);
            if(fileToDownload != null){
                notifyOwner(clientID, fileToDownload, "downloaded");
            }
            return fileToDownload;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;

    }

    @Override
    public FileDTO readFile(String fileName, String clientID) throws RemoteException {
        if(clientManager.findClient(clientID) == null){
            return null;
        }
        File fileToRead = fileCatalogDb.findFileByName(fileName, true);
        if(fileToRead != null){
            notifyOwner(clientID, fileToRead, "read");
        }
        return fileToRead;
    }

    @Override
    public FileDTO updateFileName(String oldFileName, String newFileName, String clientID) throws RemoteException{
        if(clientManager.findClient(clientID) == null){
            return null;
        }
        File updatedFile = null;
        File fileToBeUpdated = fileCatalogDb.findFileByName(oldFileName, true);
        if(fileToBeUpdated.getPermission().equals("WRITE") || clientID.equals(fileToBeUpdated.getUser()))
        {
            updatedFile = fileCatalogDb.updateFileByName(oldFileName, newFileName, true);
        }
        else{
            return null;
        }
        if(updatedFile != null){
            notifyOwner(clientID, updatedFile, "updated");
        }
        return updatedFile;
    }

    @Override
    public void deleteFile(String fileName, String clientID) throws RemoteException{
        if(clientManager.findClient(clientID) == null){
            return;
        }
        File fileToDelete = fileCatalogDb.findFileByName(fileName, true);
        if(fileToDelete == null){
            return;
        }
        if(fileToDelete.getPermission().equals("WRITE") || clientID.equals(fileToDelete.getUser()))
        {
            notifyOwner(clientID, fileToDelete, "deleted");
            fileCatalogDb.deleteFileByName(fileName);
        }
    }



    private void notifyOwner(String clientID, File file, String operation){
        Client clientToNotify = clientManager.findClient(file.getUser());
        if(clientToNotify!= null){
            clientToNotify.sendMessage("Your file " + file.getName() + " has been " + operation +" by " + clientID);
        }
    }

}
