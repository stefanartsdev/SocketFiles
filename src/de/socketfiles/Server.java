package de.socketfiles;

import de.socketfiles.client.ClientInfo;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Server extends ServerSocket {

    private ArrayList<ClientInfo> clients;

    public static void main(String[] args) {
        try {
            new Server(1337);
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
                    ci.send("[SFP] Welcome, " + ci.getUsername() + "!");
                } else ci.getSocket().close();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection attempt from /" + s.getInetAddress().getHostAddress() + " failed: " + e);
                s.close();
            }
        }
    }

    public void handleInput(ClientInfo ci, ArrayList<Object> transfer) {
        if (transfer.size() == 4 &&
                transfer.get(1) instanceof String &&
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
            } catch (IOException e) {
                System.out.println("Server error: Could not create received file.");
            }
        } else if (transfer.size() == 2 &&
                transfer.get(1) instanceof String &&
                transfer.get(1).equals("exit")) {
            try {
                getClients().remove(ci);
                ci.getSocket().close();
                System.out.println("Client /" + ci.getSocket().getInetAddress().getHostAddress()
                        + " u: " + ci.getUsername() + " disconnected");
            } catch (IOException e) {}
        } else {
            System.out.println("Input from /" + ci.getSocket().getInetAddress().getHostAddress() + ": "
                    + transfer);
        }

    }

    private boolean validateRequest(Object o, ClientInfo ci) {

        if (o instanceof String[]) {
            String[] msg = (String[]) o;
            if (msg.length != 3 || !msg[0].equals("sfp") || !msg[1].equals("login")) {
                return false;
            }
            ci.setUsername(msg[2]);
            return true;

        } else return false;

    }

    public synchronized ArrayList<ClientInfo> getClients() {
        return clients;
    }
}
