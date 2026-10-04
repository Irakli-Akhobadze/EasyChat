package Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class SocketWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;

    private final transient Socket socket;

    private final transient ObjectOutputStream outputStream;

    public SocketWrapper(String n, Socket s) throws IOException {

        this.name = n;

        this.socket = s;

        this.outputStream =
                new ObjectOutputStream(
                        s.getOutputStream()
                );

        this.outputStream.flush();
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String toString() {
        return name;
    }
}