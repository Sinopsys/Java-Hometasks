package com.hse.chat;

import com.hse.chat.network.NetworkClient;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kirill on 22.04.17.
 */
public class ChatClient extends JFrame {

    public final static String TITLE = "Chat client";
    public static ChatClient instance;

    //List of users connected
    private JList listUsers;

    //Chat text
    private JTextArea textChat;

    //User input
    private JTextField fieldInput;
    private JButton buttonSend;

    private NetworkClient client;

    public static ChatClient getInstance() {
        return instance;
    }

    public ChatClient() {
        createView();

        client = new NetworkClient("localhost", 5678);
        client.connectToServer();

        setTitle(TITLE);
        setSize(600, 700);
        setResizable(true);
        // fixme replace EXIT_ON_CLOSE with confirmation
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createView() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(new BorderLayout());

        listUsers = new JList();
        JScrollPane listUsersSP = new JScrollPane(listUsers);
        listUsersSP.setPreferredSize(new Dimension(200, 0));
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
        buttonSend = new JButton("Send");
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO send chat message to server
            }
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
