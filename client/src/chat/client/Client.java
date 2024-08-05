package chat.client;

import network.TCPConnection;
import network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = /*"192.168.0.1";*/ "127.0.0.1";
    private static final int PORT = 8080;
    private static final int WiDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { /*swing позволяет требует чтобы можно было работать только из потока EDT
        , благодаря этой строчке можно это реализовать*/
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldName = new JTextField("John_Doe");
    private final JTextField fieldMessage = new JTextField();
    //JButton button = new JButton("Click Me");
    private TCPConnection connection;


    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WiDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        fieldMessage.addActionListener(this);
        add(fieldMessage,BorderLayout.SOUTH);
        add(fieldName,BorderLayout.NORTH);
        //add(button,BorderLayout.EAST);
        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR,PORT);
        } catch (IOException e) {
            printMessage("Connection Exception: " + e);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldMessage.getText();
        if(msg.equals("")) {return;}
        fieldMessage.setText(null);
        connection.sendString(fieldName.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready:))");

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Disconnected...");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection Exception: " + e);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
