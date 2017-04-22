package com.hse.chat;

import sun.nio.ch.IOStatus;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Created by kirill on 12.04.17.
 */
public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket server;
    private Socket connection;


    public Server() {
        super("Instant messenger");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(e -> {
            sendMessage(e.getActionCommand());
            userText.setText("");
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        chatWindow.setEditable(false);
        setSize(300, 150);
        setVisible(true);
    }

    // set up and run server

    public void startRunning() {
        try {
            server = new ServerSocket(6789, 100);
            while (true) {
                try {
                    // connect and do chatting
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                } catch (EOFException e) {
                    showMessage("\n Server ended the connection!\n");
                } finally {
                    closeCrap();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // wait for connection then display connection information
    private void waitForConnection() throws IOException {
        showMessage("Waiting for someone to connect..\n");
        connection = server.accept();
        showMessage("Now connected to " + connection.getInetAddress().getHostName());
    }

    // get stream to send and receive data
    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\nThe streams are now set up.\n");
    }

    // during the chat conversation
    private void whileChatting() throws IOException {
        String message = "You are now connected";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("idk wtf was sent\n");
            }
        }
        while (!message.equals("CLIENT - END"));
    }

    // close streams and sockets after chatting
    private void closeCrap() {
        showMessage("\nClosig connections..");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // send message to the client
    private void sendMessage(String message) {
        try {
            outputStream.writeObject("SERVER - " + message);
            outputStream.flush();
            showMessage("\nSERVER - " + message);
        } catch (IOException e) {
            chatWindow.append("\nEOOR!!! CAN'T SEND MESSAGE!");
        }
    }

    // updates chat window
    private void showMessage(final String text) {
        SwingUtilities.invokeLater(() -> {
            chatWindow.append(text);
        });
    }

    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(() -> {
            userText.setEditable(tof);
        });
    }
}


// EOF
