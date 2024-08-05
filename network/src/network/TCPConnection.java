package network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread; /* receive Thread - поток, слушающий входящие изменения*/
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException{
        this(eventListener, new Socket(ipAddr, port));

    }

    public TCPConnection(TCPConnectionListener eventListener ,Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this,msg);
                    }
                } catch (IOException e){
                    eventListener.onException(TCPConnection.this,e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }
//    synchronized необходим для того, чтобы метод можно было вызвать из каждого потока
    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
