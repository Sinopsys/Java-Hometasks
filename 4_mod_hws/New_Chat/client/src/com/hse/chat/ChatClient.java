package com.hse.chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.NumberFormatter;

/**
 * Created by kirill on 22.04.17.
 */

public class ChatClient extends JFrame implements ActionListener {
    // constants
    //
    private static final String TITLE = "Chat client";
    private static final int WIDTH = 560;
    private static final int HEIGHT = 300;
    private static final int LIST_WIDTH = 110;
    private static final int LIST_HEIGHT = 0;
    private static final String SEND_BTN_TEXT = "Send";
    private static final int FIELD_WIDTH = 100;
    private static final int FIELD_HEIGHT = 25;
    private final static String LOGOUT_MESSAGE = "logoutme:";
    private static final int EXIT_SUCCESS = 0;
    private static final int FIELD_INPUT_WIDTH = 400;
    private static final int FIELD_INPUT_HEIGHT = 60;

    private static int PORT = 5678;
    private static String IP = "127.0.0.1";

    private String IPADDRESS_PATTERN =
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                    "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    private JButton ipButton;
    private Socket s;
    private DataOutputStream output;
    private JButton sendButton, logoutButton, loginButton, exitButton;
    JTextArea txtBroadcast;
    private JTextArea txtMessage;
    JList<String> usersList;


    private ChatClient() {
        createView();
    }

    public boolean setIP(String ip) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.find()) {
            IP = ip;
            return true;
        } else {
            return false;
        }
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
        askToChangePortAndIP();
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
        fieldInputSP.setPreferredSize(new Dimension(FIELD_INPUT_WIDTH, FIELD_INPUT_HEIGHT));
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
                                              sendButton.doClick();
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
//        txtMessage.addFocusListener(new FocusAdapter() {
//            public void focusGained(FocusEvent fe) {
//                txtMessage.selectAll();
//            }
//        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (s != null) {
                    int confirmed = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to exit the chat?",
                            "Exit Program",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmed == JOptionPane.YES_OPTION) {
                        logoutSession();
                        dispose();
                    }
//                    JOptionPane.showMessageDialog(null,
//                            "You are logged out now. ", "Exit",
//                            JOptionPane.INFORMATION_MESSAGE);
//                    logoutSession();
                }
                System.exit(EXIT_SUCCESS);
            }
        });
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(sendButton);
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(loginButton);
        buttonsPanel.add(exitButton);
        panelInput.add(buttonsPanel, BorderLayout.EAST);

        setTitle(TITLE + " " + IP + ":" + PORT);
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void askToChangePortAndIP() {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.equals(""))
                    return null;
                return super.stringToValue(text);
            }
        };
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setText(Integer.toString(PORT));
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));

        JTextField field2 = new JTextField();
        field2.setText(IP);
        field2.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));

        final JComponent[] inputs = new JComponent[]{
                new JLabel("Port"),
                field,
                new JLabel("IP"),
                field2
        };
        do {
            int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Port and IP", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                setPORT(Integer.valueOf(field.getText()));
            }
        } while (!setIP(field2.getText()));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tmp == loginButton) {
            String userName = JOptionPane.showInputDialog(null, "Please enter your name: ");
            if (userName != null)
                try {
                    txtBroadcast.setText("");
                    clientChat(userName);
                } catch (IOException e) {
                    closeEverything();
                    e.printStackTrace();
                }
        } else if (tmp == logoutButton) {
            if (s != null) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to logout of the chat?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    txtBroadcast.append("---LOGGING OUT---" + System.lineSeparator());
                    logoutSession();
                    closeEverything();
                }
            }
        } else if (tmp == exitButton) {
            if (s != null) {
//                JOptionPane.showMessageDialog(null,
//                        "You are logged out now. ", "Exit", JOptionPane.INFORMATION_MESSAGE);
                logoutSession();
                closeEverything();
            }
            System.exit(EXIT_SUCCESS);
        }
    }

    private void logoutSession() {
        if (s == null) return;
        try {
            output.writeUTF(LOGOUT_MESSAGE);
            TimeUnit.MILLISECONDS.sleep(500);
            s = null;
        } catch (InterruptedException | IOException e) {
            closeEverything();
            e.printStackTrace();
        }

        logoutButton.setEnabled(false);
        loginButton.setEnabled(true);
        setTitle("Login for Chat " + IP + ":" + PORT);
    }

    private void clientChat(String userName) throws IOException {
        try {
            s = new Socket(IP, PORT);
            DataInputStream input = new DataInputStream(s.getInputStream());
            output = new DataOutputStream(s.getOutputStream());
            ClientThread ct = new ClientThread(input, this);
            Thread t1 = new Thread(ct);
            t1.start();
            output.writeUTF(userName);
            setTitle(userName + "'s chat window " + IP + ":" + PORT);
            logoutButton.setEnabled(true);
            loginButton.setEnabled(false);
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(null, "Server not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            closeEverything();
        } catch (IOException e) {
            closeEverything();
            e.printStackTrace();
        }
    }

    public static void setPORT(int PORT) {
        ChatClient.PORT = PORT;
    }

    private void closeEverything() {
        try {
            if (s != null)
                s.close();
            if (output != null)
                output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argus[]) {
        new ChatClient();
    }
}

// EOF
