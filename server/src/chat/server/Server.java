package chat.server;

import network.TCPConnection;
import network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server  implements TCPConnectionListener {

    public static void main(String[] args) {
        new Server();
    }

    private  final ArrayList<TCPConnection> connections = new ArrayList<>();

    private Server() {
        System.out.println("Server started :))");
        try(ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        try {
            connections.add(tcpConnection);
            if (connections.size() > 2) {
                throw new Exception();
            }
            else {
                sendToAllConnections("Client connected: " + tcpConnection);
            }
        } catch (Exception e) {
            sendToAllConnections("Client has tried to connect but session is full");
            tcpConnection.disconnect();
        }
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        final int sze = connections.size();
        for (TCPConnection connection : connections) {
            connection.sendString(value);
        }
    }
}
