package de.socketfiles.client;

import java.io.File;
import java.io.IOException;

/**
 * Contains the client instance and methods to interact with it
 */
public class ClientLogic {

    /**
     * Client instance
     */
    private static Client client;

    /**
     * Tries to login to specified server
     * @param address server address
     * @param port server port
     * @param username client username
     * @return true, if successfully connected
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static boolean tryLogin(String address, int port, String username) throws IOException, ClassNotFoundException {
        client = new Client(address, port, username);
        client.connect();
        return client.isOnline();
    }

    /**
     * Closes the client connection if client is connected
     * @return
     */
    public static boolean disconnect() {
        if(client != null && !client.isOnline()) return false;
        client.close();
        return true;
    }

    /**
     * Tries to upload a file to the server
     * @param f file
     * @return true if upload was successful
     */
    public static boolean uploadFile(File f) {
        try {
            return client.sendFile(f);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Request a file from the server
     * @param f file information
     * @return true, if request got through
     */
    public static boolean requestDownload(FileMeta f) {
        try {
            client.requestDownload(f);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * closes the client socket
     */
    public static void close() {
        if(client != null) {
            client.close();
        }
    }

}
