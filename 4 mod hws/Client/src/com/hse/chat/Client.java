package com.hse.chat;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by kirill on 21.04.17.
 */
public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String message = "";
    private String serverIP;
    private Socket connection;

    // ctor

    public Client(String host) {
        super("Client 1");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener((event) ->
        {
            sendMessage(event.getActionCommand());
            userText.setText("");
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        chatWindow.setEditable(false);
        setSize(300, 150);
        setVisible(true);
    }

    // connect to server

    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException eofe) {
            showMessage("\n Client terminated connection!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeCrap();
        }
    }

    public void connectToServer() throws IOException {
        showMessage("Attempting connection...\n");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to " + connection.getInetAddress().getHostName());
    }

    // setup streams

    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\nStreams are good to gow.\n");
    }

    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException cnfe) {
                showMessage("Unknown object type");
            }
        } while (!message.equals("SERVER - END"));
    }

    //close the streams and sockets

    private void closeCrap() {
        showMessage("\nClosing crap down");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            connection.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // send messages to server

    private void sendMessage(String message) {
        try {
            outputStream.writeObject("CLIENT - " + message);
            outputStream.flush();
            showMessage("\nCLIENT - " + message);
        } catch (IOException ioe) {
            chatWindow.append("\n something went wrong");
        }
    }

    private void showMessage(final String message) {
        SwingUtilities.invokeLater(() -> {
            chatWindow.append(message);
        });
    }

    // gives user a permission to type crap in text field

    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(() -> {
            userText.setEditable(tof);
        });
    }
}


//EOF
