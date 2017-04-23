package com.hse.chat;

import com.hse.chat.network.NetworkServer;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private static final int WIDTH = 800;
    private static final int HEIGHT = 300;
    private static final int LIST_WIDTH = 200;
    private static final int LIST_HEIGHT = 0;
    private static final int EXIT_SUCCESS = 0;
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

        server = new NetworkServer(PORT);

        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the program?" + System.lineSeparator() +
                                "Stopping the server will forse all the clients to terminate",
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

        console = new JTextArea();
        console.setEditable(false);
        // scroll to end when new messages come
        //
        ((DefaultCaret) console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane consoleSP = new JScrollPane(console);
        consoleSP.setBorder(BorderFactory.createTitledBorder("Server log"));
        panel.add(consoleSP, BorderLayout.CENTER);

        console.setWrapStyleWord(true);
        console.setLineWrap(true);

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
        SwingUtilities.invokeLater(() -> {
            getInstance().setVisible(true);
        });
    }
}


// EOF
