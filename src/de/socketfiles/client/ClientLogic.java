package de.socketfiles.client;

import java.io.File;
import java.io.IOException;

public class ClientLogic {

    private static Client client;

    public static boolean tryLogin(String address, int port, String username) throws IOException, ClassNotFoundException {
        client = new Client(address, port, username);
        client.connect();
        return client.isOnline();
    }

    public static boolean disconnect() {
        if(client != null && !client.isOnline()) return false;
        client.close();
        return true;
    }

    public static boolean uploadFile(File f) {
        try {
            return client.sendFile(f);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean requestDownload(FileMeta f) {
        try {
            client.requestDownload(f);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void close() {
        if(client != null) {
            client.close();
        }
    }

}
