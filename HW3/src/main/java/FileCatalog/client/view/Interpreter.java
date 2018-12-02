package FileCatalog.client.view;

import FileCatalog.common.FileCatalogClient;
import FileCatalog.common.FileCatalogServer;
import FileCatalog.common.FileDTO;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class Interpreter implements Runnable{

    private static final String PROMPT = "> ";
    private final Scanner userIn = new Scanner(System.in);
    private boolean active = false;
    private final StdOut stdOut = new StdOut();
    private FileCatalogServer fileCatalogServer;
    private FileCatalogClient clientRepresentation;
    private String clientID;

    public void start(FileCatalogServer fileCatalogServer) throws RemoteException {
        this.fileCatalogServer = fileCatalogServer;
        clientRepresentation = new MessageReceiver();
        active = true;
        new Thread(this).start();

    }

    public void run() {
        FileDTO file = null;
        printHelp();
        while(active){
            stdOut.print(PROMPT);
            Input input = new Input(userIn.nextLine());
            String command = input.getCommand();
            try {
            switch (command) {
                case "CREATE" :
                    String status = fileCatalogServer.createUser(input.getParamOne(), input.getParamTwo());
                    if(status.equals("OK")){
                        stdOut.println("User created");
                    }
                    else if(status.equals("userTaken")){
                        stdOut.println("Username already exists");
                    }
                    break;
                case "LOGIN" :
                    clientID = fileCatalogServer.login(input.getParamOne(), input.getParamTwo(), clientRepresentation);
                    if(clientID != null && !clientID.equals("")){
                        stdOut.println(clientID + " successfully logged in");
                    }
                    else{
                        stdOut.println("Wrong username or password.");
                    }
                    break;
                case "LIST" :
                    List<? extends FileDTO> files = fileCatalogServer.listFiles(clientID);
                    if(files == null){
                        stdOut.println("Must be logged in to list files");
                        break;
                    }
                    if (!files.isEmpty()){
                        for (FileDTO currentFile : files) {
                            stdOut.println(
                                    "******************************\n" +
                                    "Filename:   " + currentFile.getName() + "\n" +
                                    "Size:        " + currentFile.getSize() + "\n" +
                                    "Owner:       " + currentFile.getUser() + "\n" +
                                    "Permission:  " + currentFile.getPermission() + "\n" +
                                    "******************************");
                        }
                    }
                    else{
                        stdOut.println("No files to list");
                    }
                    break;
                case "LOGOUT" :
                    String loggedOut = fileCatalogServer.logout(clientID);
                    if(loggedOut == null){
                        stdOut.println("Already logged out");
                    }
                    else{
                        stdOut.println("Successfully logged out");
                        clientID = "";
                    }
                    break;
                case "UPLOAD" :
                    if(!input.getParamTwo().equals("READ") && !input.getParamTwo().equals("WRITE")){
                        stdOut.println("Permission must be WRITE or READ");
                        break;
                    }
                    File catalogFile = new File(input.getParamOne());
                    if(catalogFile.exists()){
                        String uploadStatus = fileCatalogServer.uploadFile(catalogFile.getName(), (int)catalogFile.length(), input.getParamTwo(), clientID);
                        if(uploadStatus == null){
                            stdOut.println("Upload failed");
                        }
                        else{
                            stdOut.println("File uploaded");
                        }
                    }
                    else{
                        stdOut.println("Can not locate file");
                    }
                    break;
                case "DOWNLOAD" :
                    file = fileCatalogServer.downloadFile(input.getParamOne(), clientID);
                    if(file == null){
                        stdOut.println("No such file.");
                        break;
                    }
                    new RandomAccessFile(file.getName(), "rw").setLength(file.getSize());
                    break;
                case "READ" :
                    file = fileCatalogServer.readFile(input.getParamOne(), clientID);
                    if(file != null){
                        stdOut.println("Filename: " + file.getName() + "\n" + "Size: " + file.getSize() +
                                "\n" + "Owner: " + file.getUser() + "\n" + "Permission: " + file.getPermission());
                    }
                    else{
                        stdOut.println("Can not locate file");
                    }
                    break;
                case "UPDATE" :
                    file = fileCatalogServer.updateFileName(input.getParamOne(), input.getParamTwo(), clientID);
                    if(file != null){
                        stdOut.println("Filename: " + file.getName() + "\n" + "Size: " + file.getSize() +
                                "\n" + "Owner: " + file.getUser() + "\n" + "Permission: " + file.getPermission());
                    }
                    else{
                        stdOut.println("Can not locate file");
                    }
                    break;
                case "DELETE" :
                    fileCatalogServer.deleteFile(input.getParamOne(), clientID);
                    break;
                default :
                    stdOut.println("Unknown command");

            }
            } catch (RemoteException | FileNotFoundException e) {
                stdOut.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printHelp(){
        stdOut.println(
                "***********************************************************\n" +
                "Welcome to the File Catalog!    \n" +
                "These are the commands:            \n" +
                "Create user:             create <username> <password>       \n" +
                "log in:                  login <username> <password>     \n" +
                "log out:                 logout <username> <password>     \n" +
                "list all files:          list \n" +
                "read file:               read <filename>        \n" +
                "update filename:         update <filename> <new filename>       \n" +
                "upload file:             upload <filename> <permission>       \n" +
                "download file:           download <filename>        \n" +
                "delete file:             delete <filename>        \n" +
                "***********************************************************"
        );
    }

    private class MessageReceiver extends UnicastRemoteObject implements FileCatalogClient {

        public MessageReceiver() throws RemoteException {
        }

        @Override
        public void receive(String msg) {
            stdOut.println(msg);
        }
    }

}
