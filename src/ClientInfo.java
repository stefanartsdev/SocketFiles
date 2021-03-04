import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientInfo {

    private Socket socket;

    private String username;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread thread;

    @SuppressWarnings("unchecked")
    public ClientInfo(Socket socket, Server server) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        thread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    Object input = read();
                    if(validateInput(input)) {
                        server.handleInput(this, (ArrayList<Object>) input);
                    } else {
                        disconnect(server);
                    }
                } catch (IOException|ClassNotFoundException e) {
                    disconnect(server);
                    System.out.println("Error: " + e);
                }
            }
        });
    }

    private void disconnect(Server server) {
        System.out.println("Illegal operation by client /" + socket.getInetAddress().getHostAddress()
                + ". Client was disconnected.");
        server.getClients().remove(this);
        try {
            socket.close();
        } catch (IOException e) {}
    }

    @SuppressWarnings("unchecked")
    private boolean validateInput(Object input) {
        if(input instanceof ArrayList) {
            ArrayList<Object> transfer = (ArrayList<Object>) input;
            if(transfer.size() > 1) {
                if(transfer.get(0) instanceof String && transfer.get(0).equals("sfp")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void activate() {
        assert username != null;
        thread.setName("sfp/u/" + username);
        thread.start();
    }

    public String getUsername() {
        return username;
    }

    public synchronized Object read() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public synchronized void send(Object o) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
