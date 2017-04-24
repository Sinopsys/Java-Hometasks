package com.hse.chat;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by kirill on 22.04.17.
 */

class ChatServer extends JFrame {

    // constants
    //
    private final static String TITLE = "Chat server";
    private static final int WIDTH = 670;
    private static final int HEIGHT = 300;
    private static final int LIST_WIDTH = 200;
    private static final int LIST_HEIGHT = 0;
    private static final int EXIT_SUCCESS = 0;
    private static final int FIELD_WIDTH = 100;
    private static final int FIELD_HEIGHT = 25;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    private static ChatServer INSTANCE;

    private int PORT = 5678;


    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();
    private ServerSocket ss;
    private Socket s;
    private JButton portBtn;
    private JTextArea console;
    private JList<String> listUsers;

    private ChatServer() {
        createView();
        new Thread(() -> {
            try {
                ss = new ServerSocket(PORT);
                log("Server socket initialized on port " + PORT);
                log("Listening for clients...");
                while (true) {
                    s = ss.accept();
                    log("Client has connected! " + s.getRemoteSocketAddress());
                    Runnable r = new MyThread(s, sockets, users);
                    Thread thread = new Thread(r);
                    thread.start();
                }
            } catch (BindException e) {
                JOptionPane.showMessageDialog(null, "Server already running",
                        "Too many instances", JOptionPane.ERROR_MESSAGE);
                closeEverything();
                System.exit(EXIT_SUCCESS);
            } catch (IOException e) {
                closeEverything();
                System.exit(EXIT_SUCCESS);
            }
        }).start();
    }

    static ChatServer getInstance() {
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

        JPanel highPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        askToChangePort();

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
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Users online:"));
        listUsersSP.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
        panel.add(listUsersSP, BorderLayout.EAST);
        setTitle(TITLE);
        try {
            InetAddress ia = InetAddress.getLocalHost();
            setTitle(TITLE + " " + ia.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
                    closeEverything();
                    System.exit(EXIT_SUCCESS);
                }
            }
        });

        setLocationRelativeTo(null);
    }

    private void askToChangePort() {
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
        final JComponent[] inputs = new JComponent[]{
                new JLabel("Port"),
                field
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Port", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            setPORT(Integer.valueOf(field.getText()));
        }
    }

    void updateView() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String name : users) {
            model.addElement(name);
        }
        listUsers.setModel(model);
    }

    private void closeEverything() {
        try {
            if (ss != null)
                ss.close();
            if (s != null)
                s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void log(String info) {
        console.append(DATE_FORMAT.format(new Date()) + " " + info + System.lineSeparator());
    }

    public void setPORT(int port) {
        this.PORT = port;
    }

    public static void main(String argus[]) {
        getInstance().setVisible(true);
    }
}


// EOF
