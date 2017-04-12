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
}
