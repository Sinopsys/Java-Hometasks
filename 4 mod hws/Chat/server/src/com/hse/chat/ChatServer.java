package com.hse.chat;

import com.hse.chat.network.NetworkServer;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kirill on 22.04.17.
 */
public class ChatServer extends JFrame {

    // singleton instance
    //
    private static ChatServer INSTANSE;

    // constants
    //
    public final static String TITLE = "Chat server";
    private static final int PORT = 5678;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int LIST_WIDTH = 200;
    private static final int LIST_HEIGHT = 0;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

    // GUI vars
    //
    private JTextArea console;
    private JList<String> listUsers;

    // other
    //
    private NetworkServer server;


    public static ChatServer getInstance() {
        ChatServer localInstance = INSTANSE;
        if (localInstance == null) {
            synchronized (ChatServer.class) {
                localInstance = INSTANSE;
                if (localInstance == null) {
                    INSTANSE = localInstance = new ChatServer();
                    INSTANSE.server.startServer();
                }
            }
        }
        return localInstance;
    }

    public ChatServer() {
        createView();

        server = new NetworkServer(this, PORT);

        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        // fixme replace EXIT_ON_CLOSE with confirmation
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createView() {
        JPanel panel = new JPanel();
        add(panel);

        panel.setLayout(new BorderLayout());

        console = new JTextArea();
        console.setEditable(false);
        // scroll to end when new messages come
        ((DefaultCaret) console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane consoleSP = new JScrollPane(console);
        consoleSP.setBorder(BorderFactory.createTitledBorder("Console Output"));
        panel.add(consoleSP, BorderLayout.CENTER);

        listUsers = new JList<>();
        JScrollPane listUsersSP = new JScrollPane(listUsers);
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Connected Users:"));
        listUsersSP.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
        panel.add(listUsersSP, BorderLayout.EAST);
    }

    public void updateView() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String name : server.getConnectedClientMap().keySet()) {
            model.addElement(name);
        }
        listUsers.setModel(model);
    }

    public void log(String info) {
        console.append(DATE_FORMAT.format(new Date()) + " " + info + System.lineSeparator());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            getInstance().setVisible(true);
        });
    }
}


// EOF
