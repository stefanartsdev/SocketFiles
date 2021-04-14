package de.socketfiles;

import de.socketfiles.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * ClientInfo class
 * Wrapper for a socket that is used by the {@link de.socketfiles.Server} and the {@link de.socketfiles.client.Client}
 */
public class ClientInfo {

    /**
     * The actual socket
     */
    private Socket socket;
    /**
     * Specified username
     */
    private String username;
    /**
     * Input stream of socket
     */
    private ObjectInputStream in;
    /**
     * Output stream of socket
     */
    private ObjectOutputStream out;
    /**
     * Thread to handle incoming data
     */
    private Thread thread;

    /**
     *  ClientInfo constructor
     * @param socket client socket
     * @param server server it is connected to
     * @throws IOException
     */
    public ClientInfo(Socket socket, Server server) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        thread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    Object input = read();
                    if (validateInput(input)) {
                        server.handleInput(this, (ArrayList<Object>) input);
                    } else {
                        disconnect(server);
                    }
                } catch (SocketException e) {
                    System.out.println("Client disconnected");
                    disconnect(server);
                } catch (IOException | ClassNotFoundException e) {
                    disconnect(server);
                    System.out.println("Error: ");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Disconnects the client
     * @param server
     */
    private void disconnect(Server server) {
        System.out.println("Illegal operation by client /" + socket.getInetAddress().getHostAddress()
                + ". Client was disconnected.");
        server.getClients().remove(this);
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Checks if a transferred object is valid
     * @param input transferred data
     * @return true, if valid, false, if not
     */
    private boolean validateInput(Object input) {
        if (input instanceof ArrayList) {
            ArrayList<Object> transfer = (ArrayList<Object>) input;
            if (transfer.size() > 1) {
                if (transfer.get(0) instanceof String && transfer.get(0).equals("sfp")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Starts the thread to receive data after successfully connecting to the server
     */
    public void activate() {
        assert username != null;
        thread.setName("sfp/u/" + username);
        thread.start();
    }

    /**
     * Getter for the username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Reads data from the inputstream
     * @return data of any type
     * @throws IOException from readObject()
     * @throws ClassNotFoundException from readObject()
     */
    public synchronized Object read() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    /**
     * Sends an object via the output stream
     * @param o data of any type
     * @throws IOException from writeObject() and flush()
     */
    public void send(Object o) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    /**
     * Getter for the socket
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Setter for the username
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
