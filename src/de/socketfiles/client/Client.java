package de.socketfiles.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Client {

    private Socket socket;
    private Thread inputThread;

    private String username;
    private InetSocketAddress address;

    private boolean online;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Client c = new Client("localhost", 1337, "Stefan");
        c.connect();
        c.sendFile(new File("tes.txt"));
        c.close();
        System.out.println("Connection closed.");
    }

    public Client(String address, int port, String username) {
        socket = new Socket();
        inputThread = new Thread(() -> {
            while (online) {
                try {
                    handleInput(in.readObject());
                } catch (IOException|ClassNotFoundException e) {
                    online = false;
                }
            }
            try {
                socket.close();
            } catch (IOException e) {}
        });
        this.username = username;
        this.address = new InetSocketAddress(address, port);
        online = false;
    }

    public void connect() throws IOException, ClassNotFoundException {
        socket.connect(address);
        if(!socket.isConnected()) {
            return;
        }

        online = true;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connection attempt to /" + address.getHostName());

        out.writeObject(new String[] { "sfp", "login", username});
        out.flush();
        socket.setSoTimeout(1000);
        Object o = in.readObject();
        socket.setSoTimeout(0);
        if(!(o instanceof String[])) {
            online = false;
            socket.close();
            return;
        }
        String[] msg = (String[]) o;
        if(msg.length != 2 || !msg[0].equals("sfp") || !msg[1].equals("success")) {
            online = false;
        }
        System.out.println("Successfully connected via SFP to /" + address.getHostName() + " as " + username);
        inputThread.start();
    }

    public void close() {
        try {
            ArrayList<Object> transfer = new ArrayList<>();
            transfer.add("sfp");
            transfer.add("exit");
            out.writeObject(transfer);
            out.flush();
            socket.close();
        } catch (IOException e) {}
    }

    private void handleInput(Object o) {
        System.out.println("Message from server: " + o);
    }

    private void sendFile(File f) throws IOException {
        if(f.exists() && f.isFile()) {
            String fileName = f.getName();
            byte[] fileContent = Files.readAllBytes(Paths.get(f.getPath()));

            ArrayList<Object> transferList = new ArrayList<>();
            transferList.add("sfp");
            transferList.add("send");
            transferList.add(fileName);
            transferList.add(fileContent);
            out.writeObject(transferList);
            out.flush();
            System.out.println("Successfully sent file " + fileName + " to server.");
        }
    }

    public boolean isOnline() {
        return online;
    }
}
