package com.hse.chat;

import com.hse.chat.network.NetworkClient;
import com.hse.chat.network.packet.ChatPacket;
import com.hse.chat.network.packet.DisconnectPacket;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

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
    private static final int WIDTH = 450;
    private static final int HEIGHT = 550;
    private static final int LIST_WIDTH = 110;
    private static final int LIST_HEIGHT = 0;
    private static final String SEND_BTN_TEXT = "Send";
    private static final int EXIT_SUCCESS = 0;

    // GUI vars
    //
    private JList<String> listUsers;
    private JTextArea textChat;
    private JTextArea fieldInput;
    private JButton buttonSend;

    public static ChatClient getInstance() {
        return instance;
    }

    public ChatClient() {
        createView();

        client = new NetworkClient(IP, PORT);
        client.connectToServer();

        setTitle(TITLE + ". Logged in as " + client.getUsername());
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
                    try {
                        client.sendPacket(new DisconnectPacket(client.getUsername()));
                        client.closeEverything();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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

        listUsers = new JList<>();
        JScrollPane listUsersSP = new JScrollPane(listUsers);
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Users"));

        listUsersSP.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
        panel.add(listUsersSP, BorderLayout.EAST);

        JPanel panelChat = new JPanel(new BorderLayout());
        panel.add(panelChat, BorderLayout.CENTER);

        textChat = new JTextArea();
        textChat.setEditable(false);
        // scroll to end when new messages come
        //
        ((DefaultCaret) textChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane textChatSP = new JScrollPane(textChat);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat history"));
        panelChat.add(textChatSP, BorderLayout.CENTER);

        JPanel panelInput = new JPanel(new BorderLayout());
        panel.add(panelInput, BorderLayout.SOUTH);

        fieldInput = new JTextArea();
        ((DefaultCaret) fieldInput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane fieldInputSP = new JScrollPane(fieldInput);
        fieldInputSP.setPreferredSize(new Dimension(0, 60));
        panelInput.add(fieldInputSP, BorderLayout.CENTER);

        textChat.setWrapStyleWord(true);
        textChat.setWrapStyleWord(true);
        fieldInput.setLineWrap(true);
        fieldInput.setLineWrap(true);

        fieldInput.addKeyListener(new KeyAdapter() {
                                      @Override
                                      public void keyReleased(KeyEvent e) {
                                          if (e.getKeyCode() == KeyEvent.VK_ENTER &&
                                                  (e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
                                              sendMessage();
                                          }
                                      }
                                  }
        );

        buttonSend = new JButton(SEND_BTN_TEXT);
        buttonSend.addActionListener(e -> {
            sendMessage();
        });
        panelInput.add(buttonSend, BorderLayout.EAST);
    }

    private void sendMessage() {
        String message = fieldInput.getText().trim();
        if (message.length() == 0) {
            return;
        }

        showMessage(client.getUsername() + ": " + message + System.lineSeparator());
        fieldInput.setText("");
        // FIXME send chat message to server
        client.sendPacket(new ChatPacket(client.getUsername(), message));
    }

    public void showMessage(String text) {
        textChat.append(text);
    }

    public void updateView() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String name : client.getUserNames()) {
            model.addElement(name);
        }
        listUsers.setModel(model);
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
