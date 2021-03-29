package de.socketfiles;

import de.socketfiles.client.ClientInfo;
import de.socketfiles.client.FileMeta;
import de.socketfiles.client.SocketFile;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server extends ServerSocket {

    private ArrayList<ClientInfo> clients;

    public static void main(String[] args) {
        try {
            new Server(1805);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(int port) throws IOException {
        super(port);
        clients = new ArrayList<>();
        System.out.println("Server is running on " + port);
        while (!isClosed()) {
            Socket s;
            try {
                s = accept();
                System.out.println("Client attempting to connect at /" + s.getInetAddress().getHostAddress());
                if (!s.getInetAddress().getHostAddress().equals("localhost") &&
                        !s.getInetAddress().getHostAddress().equals("127.0.0.1")) {
                    System.out.println("Client violates terms and is being closed. /" + s.getInetAddress().getHostAddress());
                    s.close();
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            try {
                ClientInfo ci = new ClientInfo(s, this);
                s.setSoTimeout(1000);
                Object o = ci.read();
                s.setSoTimeout(0);
                if (validateRequest(o, ci)) {
                    getClients().add(ci);
                    ci.send(new String[]{"sfp", "success"});
                    System.out.println("Client connected /" + ci.getSocket().getInetAddress().getHostAddress()
                            + " u: " + ci.getUsername());
                    ci.activate();
                    updateUsersForAllClients();
                    updateFilesForClient(ci);
                } else {
                    ci.getSocket().close();
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection attempt from /" + s.getInetAddress().getHostAddress() + " failed: " + e);
                s.close();
            }
        }
    }

    public void handleInput(ClientInfo ci, ArrayList<Object> transfer) {
        if(!transfer.get(0).equals("sfp")) return;
        if (transfer.size() == 4 &&
                transfer.get(1).equals("send") &&
                transfer.get(2) instanceof String &&
                transfer.get(3) instanceof byte[]) {
            File dir = new File("files" + File.separator + ci.getUsername());
            dir.mkdirs();
            String fileName = (String) transfer.get(2);
            byte[] fileContent = (byte[]) transfer.get(3);
            File newFile = new File(dir.getPath() + File.separator + fileName);
            try {
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                Files.write(Paths.get(newFile.getPath()), fileContent);
                System.out.println("Successfully received file " + fileName + " by " + ci.getUsername());
                updateFilesForAllClients();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Server error: Could not create received file.");
            }
        } else if (transfer.size() == 3 &&
                transfer.get(1).equals("get") &&
                transfer.get(2) instanceof FileMeta) {
            FileMeta fm = (FileMeta) transfer.get(2);
            try {
                SocketFile file = getFileByMeta(fm);
                assert file != null;
                sendFileToClient(ci, file);
            } catch (IOException e) {
                System.out.println("Error while sending file to client: File is not existant");

                e.printStackTrace();
            }

        } else if (transfer.size() == 2 &&
                transfer.get(1).equals("exit")) {
            try {
                getClients().remove(ci);
                ci.getSocket().close();
                System.out.println("Client /" + ci.getSocket().getInetAddress().getHostAddress()
                        + " u: " + ci.getUsername() + " disconnected");
                updateUsersForAllClients();
            } catch (IOException e) {}
        } else {
            System.out.println("Input from /" + ci.getSocket().getInetAddress().getHostAddress() + ": "
                    + transfer);
        }

    }

    private void sendFileToClient(ClientInfo ci, SocketFile f) throws IOException {
        ArrayList<Object> transferList = new ArrayList<>();
        transferList.add("sfp");
        transferList.add("send");
        transferList.add(f.getMeta().getName());
        transferList.add(f.getData());
        ci.send(transferList);
    }

    private ArrayList<FileMeta> getCurrentMeta() throws IOException {
        ArrayList<FileMeta> files = new ArrayList<>();
        File root = new File("files");
        for(File dir : root.listFiles()) {
            if(dir.isDirectory()) {
                for(File userfile : dir.listFiles()) {
                    files.add(new FileMeta(userfile.getName(),
                            dir.getName(), Files.size(Paths.get(userfile.getPath()))));
                }
            }
        }
        return files;
    }

    private SocketFile getFileByMeta(FileMeta fm) throws IOException {
        File file = new File("files" + File.separator + fm.getAuthor() + File.separator + fm.getName());
        if(file.exists()) {
            return getFileByMeta(fm.getName(), fm.getAuthor());
        }
        return null;
    }

    private SocketFile getFileByMeta(String name, String author) throws IOException {
        return new SocketFile(name,
                author,
                Files.readAllBytes(Paths.get("files"+File.separator+author+File.separator+name))
        );
    }

    private boolean validateRequest(Object o, ClientInfo ci) {

        if (o instanceof String[]) {
            String[] msg = (String[]) o;
            if (msg.length != 3 || !msg[0].equals("sfp") || !msg[1].equals("login")) {
                return false;
            }
            if(msg[2].isEmpty() || !msg[2].matches("^[a-zA-Z0-9_]*$")) {
                return false;
            }
            String[] users = getUsernameList();
            for (String user : users) {
                if (user.equals(msg[2])) return false;
            }
            ci.setUsername(msg[2]);
            return true;

        } else return false;

    }

    public synchronized ArrayList<ClientInfo> getClients() {
        return clients;
    }

    public String[] getUsernameList() {
        String[] clients = new String[getClients().size()];
        for(int i = 0; i < clients.length; i++) {
            clients[i] = this.clients.get(i).getUsername();
        }
        return clients;
    }

    public void updateFilesForClient(ClientInfo ci) throws IOException {
        ArrayList<Object> data = new ArrayList<>();
        data.add("sfp");
        data.add("files");
        data.add(getCurrentMeta());
        ci.send(data);
    }

    public void updateFilesForAllClients() throws IOException {
        for(ClientInfo ci : clients) {
            updateFilesForClient(ci);
        }
    }

    public void updateUsersForClient(ClientInfo ci) throws IOException {
        List<Object> transfer = new ArrayList<>();
        transfer.add("sfp");
        transfer.add("users");
        transfer.add(getUsernameList());
        ci.send(transfer);
    }

    public void updateUsersForAllClients() throws IOException {
        for(ClientInfo ci : clients) {
            updateUsersForClient(ci);
        }
    }

}
