package de.socketfiles.client;

import java.io.IOException;
import java.util.ArrayList;

public class ClientLogic {

    private static Client client;

    public static boolean tryLogin(String address, int port, String username) throws IOException, ClassNotFoundException {
        client = new Client(address, port, username);
        client.connect();
        return client.isOnline();
    }

    public static void close() {
        if(client != null) {
            client.close();
        }
    }

}
