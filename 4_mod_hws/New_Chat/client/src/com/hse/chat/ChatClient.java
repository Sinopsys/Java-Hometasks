package com.hse.chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class ChatClient extends JFrame implements ActionListener {
    private static ChatClient instance;
    Socket s;
    DataInputStream input;
    DataOutputStream output;

    JButton sendButton, logoutButton, loginButton, exitButton;
    JTextArea txtBroadcast;
    JTextArea txtMessage;
    JList usersList;
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


    public ChatClient() {
        createView();
    }

    private void createView() {
        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BorderLayout());

        usersList = new JList<>();
        JScrollPane listUsersSP = new JScrollPane(usersList);
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Users"));

        listUsersSP.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
        panel.add(listUsersSP, BorderLayout.EAST);

        JPanel panelChat = new JPanel(new BorderLayout());
        panel.add(panelChat, BorderLayout.CENTER);

        txtBroadcast = new JTextArea();
        txtBroadcast.setEditable(false);
        // scroll to end when new messages come
        //
        ((DefaultCaret) txtBroadcast.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane textChatSP = new JScrollPane(txtBroadcast);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat history"));
        panelChat.add(textChatSP, BorderLayout.CENTER);

        JPanel panelInput = new JPanel(new BorderLayout());
        panel.add(panelInput, BorderLayout.SOUTH);

        txtMessage = new JTextArea();
        ((DefaultCaret) txtMessage.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane fieldInputSP = new JScrollPane(txtMessage);
        fieldInputSP.setPreferredSize(new Dimension(0, 60));
        panelInput.add(fieldInputSP, BorderLayout.CENTER);

        txtBroadcast.setWrapStyleWord(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setLineWrap(true);
        txtBroadcast.setLineWrap(true);

        txtMessage.addKeyListener(new KeyAdapter() {
                                      @Override
                                      public void keyReleased(KeyEvent e) {
                                          if (e.getKeyCode() == KeyEvent.VK_ENTER &&
                                                  (e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
//               todo                                 sendMessage();
                                          }
                                      }
                                  }
        );
        logoutButton = new JButton("Log out");
        loginButton = new JButton("Log in");
        exitButton = new JButton("Exit");
        sendButton = new JButton(SEND_BTN_TEXT);
        sendButton.addActionListener(this);
        logoutButton.addActionListener(this);
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        //default enable login and disable logout
        logoutButton.setEnabled(false);
        loginButton.setEnabled(true);
        //An abstract adapter class for receiving keyboard focus events.
        txtMessage.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent fe) {
                txtMessage.selectAll();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                if (s != null) {
                    JOptionPane.showMessageDialog(null, "You are logged out right now. ", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    logoutSession();
                }
                System.exit(0);
            }
        });
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(sendButton);
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(loginButton);
        buttonsPanel.add(exitButton);
        panelInput.add(buttonsPanel, BorderLayout.EAST);

        // todo login as NAME
        setTitle(TITLE + ". Logged in as ");
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // todo confirm exit
        setLocationRelativeTo(null);
        setVisible(true);

    }


    public void actionPerformed(ActionEvent ae) {
        JButton tmp = (JButton) ae.getSource();
        if (tmp == sendButton) {
            if (s == null) {
                JOptionPane.showMessageDialog(null, "You are not logged in. Please login");
                return;
            }
            try {
                //send the text the user wrote to server and then clear the text
                output.writeUTF(txtMessage.getText());
                txtMessage.setText("");
            } catch (Exception excp) {
                txtBroadcast.append("send button click :" + excp);
            }
        } else if (tmp == loginButton) {
            String userName = JOptionPane.showInputDialog(null, "Please enter your name: ");
            if (userName != null)
                clientChat(userName);
        } else if (tmp == logoutButton) {
            if (s != null) {
                logoutSession();
            }
        } else if (tmp == exitButton) {
            if (s != null) {
                JOptionPane.showMessageDialog(null, "You are logged out right now. ", "Exit", JOptionPane.INFORMATION_MESSAGE);
                logoutSession();
            }
            System.exit(0);
        }
    }

    public void logoutSession() {
        if (s == null) return;
        try {
            output.writeUTF(ChatServer.LOGOUT_MESSAGE);
            Thread.sleep(500);
            s = null;
        } catch (Exception e) {
            txtBroadcast.append("\n inside logoutSession Method" + e);
        }

        logoutButton.setEnabled(false);
        loginButton.setEnabled(true);
        setTitle("Login for Chat");
    }

    public void clientChat(String userName) {
        try {
            //server's IP Address
            s = new Socket(InetAddress.getLocalHost(), ChatServer.PORT);
            input = new DataInputStream(s.getInputStream());
            output = new DataOutputStream(s.getOutputStream());
            ClientThread ct = new ClientThread(input, this);
            Thread t1 = new Thread(ct);
            t1.start();
            output.writeUTF(userName);
            setTitle(userName + "'s chat window");
            logoutButton.setEnabled(true);
            loginButton.setEnabled(false);
        } catch (Exception e) {
            txtBroadcast.append("\nClient Constructor " + e);
        }

    }

    public static void main(String argus[]) {
        new ChatClient();
    }
}

// EOF
