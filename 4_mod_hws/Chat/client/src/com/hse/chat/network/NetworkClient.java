package com.hse.chat.network;

import com.hse.chat.ChatClient;
import com.hse.chat.network.packet.*;
import javafx.application.Application;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by kirill on 22.04.17.
 */
public class NetworkClient implements PacketListener {
    // constants
    //
    private static final String SEPARATOR = ";";
    private static final int EXIT_SUCCESS = 0;
    private static final String LOADING_GIF_PATH = "resources//loading.gif";
    private static final int LOADING_WIDTH = 400;
    private static final int LOADING_HEIGHT = 100;

    // network vars
    //
    private Socket socket;
    private String ipAddress;
    private int serverPort;
    private DataInputStream inputStream;

    private String username;
    private Map<String, Socket> connectedClientMap = new HashMap<>();
    private java.util.List<PacketListener> packetListeners = new ArrayList<>();


    public NetworkClient(String ipAddress, int serverPort) {
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
        addPacketListener(this);
    }

    private void addPacketListener(PacketListener packetListener) {
        packetListeners.add(packetListener);
    }

    public void connectToServer() {
        try {
            socket = new Socket(ipAddress, serverPort);
//            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//            output.writeUTF("Hello, server!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Server not found. " +
                    "Ask admin to start it and restart your client.");
            try {
                closeEverything();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }

        while (username == null) {
            String[] options = {"OK"};
            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter your username: "));
            JTextField txt = new JTextField(10);
            panel.add(txt);

            int selectedOption = JOptionPane.showOptionDialog(null, panel,
                    "Login", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            txt.requestFocus();
            txt.grabFocus();
            if (selectedOption == 0)
                username = txt.getText().trim();
        }
        sendPacket(new ConnectPacket(username));

        // client listens for server requests / responses
        //
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    inputStream = new DataInputStream(socket.getInputStream());
                    String rawData = inputStream.readUTF();
                    String[] data = rawData.trim().split(SEPARATOR);
                    PacketType type = PacketType.valueOf(data[0]);
                    Packet packet = PacketDictionary.translatePacketType(type, data);

                    broadcastPacketReseived(packet, socket);
                } catch (IOException e) {
                    int res = JOptionPane.showConfirmDialog
                            (null, "Server has stopped!",
                                    "Server Stopped.", JOptionPane.DEFAULT_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        try {
                            closeEverything();
                            System.out.println("Everything has been successfully closed.");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.exit(EXIT_SUCCESS);
                }

            }
        }).start();
    }

    private void connectClient(ConnectPacket packet, Socket client) {
        if (connectedClientMap.get(packet.i_username) != null) {
            return;
        }
        connectedClientMap.put(packet.i_username, client);
        ChatClient.getInstance().updateView();
    }

    private void broadcastPacketReseived(Packet packet, Socket client) {
        for (PacketListener packetListener : packetListeners) {
            packetListener.packetReseived(packet, client);
        }
    }

    private void broadcastPacketSent(Packet packet, Socket client) {
        for (PacketListener packetListener : packetListeners) {
            packetListener.packetSent(packet, client);
        }
    }

    private void removeClient(Socket client) {
        // using removeIf to avoid ConcurrentModificationException
        //
        connectedClientMap.entrySet().removeIf(item -> item.getValue().equals(client));
        ChatClient.getInstance().updateView();
    }


    public void closeEverything() throws IOException {
        if (socket != null)
            socket.close();
        if (inputStream != null)
            inputStream.close();
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Socket> getConnectedClientMap() {
        return connectedClientMap;
    }


    public void sendPacket(Packet packet) {
        try {
            // never use try-with-resources... the mistake killed 3.5h of my life...
            // I'm so hopping-mad and frustrated!
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(packet.getOutGoingData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void packetSent(Packet packet, Socket client) {

    }

    @Override
    public void packetReseived(Packet packet, Socket client) {
        if (packet instanceof ConnectPacket) {
            connectClient((ConnectPacket) packet, socket);
        } else if (packet instanceof DisconnectPacket) {
            removeClient(socket);
        }
    }
}

// EOF
