package com.hse.chat;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


class ChatServer extends JFrame {
    ArrayList<Socket> sockets = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    ServerSocket ss;
    Socket s;
    private static ChatServer INSTANCE;

    //    public final static int PORT = 10000;
    public final static String UPDATE_USERS = "updateuserslist:";
    public final static String LOGOUT_MESSAGE = "logoutme:";
    // constants
    //
    public final static String TITLE = "Chat server";
    public static final int PORT = 5678;
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

    public ChatServer() {
        createView();
        new Thread(() -> {
            try {
                //ServerSocket must throw exception
                ss = new ServerSocket(PORT);
                System.out.println("Server Started " + ss);
                log("Server socket initialized on port " + PORT);
                log("Listening for clients...");
                while (true) {
                    //this method blocks until a connection is made
                    s = ss.accept();
                    log("Client has connected! " + s.getRemoteSocketAddress());
                    Runnable r = new MyThread(s, sockets, users);
                    Thread thread = new Thread(r);
                    thread.start();
                }
            } catch (BindException e) {
                JOptionPane.showMessageDialog(null, "Server already running",
                        "Too many instances", JOptionPane.ERROR_MESSAGE);
                System.exit(EXIT_SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    public static ChatServer getInstance() {
        ChatServer localInstance = INSTANCE;
        if (localInstance == null) {
            synchronized (ChatServer.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    INSTANCE = localInstance = new ChatServer();
                }
            }
        }
        return localInstance;
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
        for (String name : users) {
            model.addElement(name);
        }
        listUsers.setModel(model);
    }

    public void log(String info) {
        console.append(DATE_FORMAT.format(new Date()) + " " + info + System.lineSeparator());
    }


    public static void main(String argus[]) {
        getInstance().setVisible(true);
    }
}

// EOF
