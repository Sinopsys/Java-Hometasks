package com.hse.chat;

import com.hse.chat.network.NetworkClient;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by kirill on 22.04.17.
 */
public class ChatClient extends JFrame {
    private static ChatClient instance;
    private NetworkClient client;

    // constants
    //
    private static final String TITLE = "Chat client";
    private static final int PORT = 5678;
    private static final String IP = "127.0.0.1";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 650;
    private static final int LIST_WIDTH = 200;
    private static final int LIST_HEIGHT = 0;
    private static final String SEND_BTN_TEXT = "Send";
    private static final int EXIT_SUCCESS = 0;

    // GUI vars
    //
    private JList listUsers;
    private JTextArea textChat;
    private JTextField fieldInput;
    private JButton buttonSend;

    public static ChatClient getInstance() {
        return instance;
    }

    public ChatClient() {
        createView();

        client = new NetworkClient(IP, PORT);
        client.connectToServer();

        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the chat?",
                        "Exit Program",
                        JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(EXIT_SUCCESS);
                }
            }
        });
        setLocationRelativeTo(null);
    }

    private void createView() {
        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BorderLayout());

        listUsers = new JList();
        JScrollPane listUsersSP = new JScrollPane(listUsers);
        listUsersSP.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
        panel.add(listUsersSP, BorderLayout.EAST);

        JPanel panelChat = new JPanel(new BorderLayout());
        panel.add(panelChat, BorderLayout.CENTER);

        textChat = new JTextArea();
        textChat.setEditable(false);
        // scroll to end when new messages come
        ((DefaultCaret) textChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane textChatSP = new JScrollPane(textChat);
        panelChat.add(textChatSP, BorderLayout.CENTER);

        JPanel panelInput = new JPanel(new BorderLayout());
        panel.add(panelInput, BorderLayout.SOUTH);
        fieldInput = new JTextField();
        panelInput.add(fieldInput, BorderLayout.CENTER);
        buttonSend = new JButton(SEND_BTN_TEXT);
        buttonSend.addActionListener(e -> {
            //TODO send chat message to server
        });
        panelInput.add(buttonSend, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            instance = new ChatClient();
            instance.setVisible(true);
        });
    }
}


// EOF
