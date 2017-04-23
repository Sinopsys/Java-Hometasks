package com.hse.chat.network;

import com.hse.chat.ChatServer;
import com.hse.chat.network.packet.*;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by kirill on 22.04.17.
 */
public class NetworkServer {
    // vars
    //
    private ServerSocket socket;
    private boolean running = false;
    private int port;
    private static final String SEPARATOR = ";";
    private static final int EXIT_SUCCESS = 0;
    private Map<String, Socket> connectedClientMap = new HashMap<>();
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public NetworkServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            socket = new ServerSocket(port);
            ChatServer.getInstance().log("Server socket initialized on port " + port);
        } catch (BindException e) {
            JOptionPane.showMessageDialog(null, "Server already running",
                    "Too many instances", JOptionPane.ERROR_MESSAGE);
            System.exit(EXIT_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        new Thread(() -> {
            ChatServer.getInstance().log("Listening for clients...");
            while (running) {
                try {
                    final Socket client = socket.accept();
                    ChatServer.getInstance().log("Client has connected! " + client.getRemoteSocketAddress());
                    //Read individual client data
                    //
                    new Thread(() -> {
                        boolean error = false;
                        while (!error && client.isConnected()) {
                            Packet packet = null;
                            try {
                                inputStream = new DataInputStream(client.getInputStream());
                                String rawData = inputStream.readUTF();
                                String[] data = rawData.trim().split(SEPARATOR);
                                PacketType type = PacketType.valueOf(data[0]);
                                packet = PacketDictionary.translatePacketType(type, data);

                                readPacket(packet, client);
//                                ChatServer.getInstance().log("Client: " +
//                                        System.lineSeparator() + "\t" + ((ConnectPacket) packet).i_username);
//                                DataInputStream input = new DataInputStream(client.getInputStream());
//                                String request = input.readUTF();
//                                //TODO interpret request
//                                ChatServer.getInstance().log(client.getRemoteSocketAddress()
//                                        + " says:" + System.lineSeparator() + "\t" + request);
                            } catch (EOFException ex) {
                                error = true;
//                                ChatServer.getInstance().log("Client " + client.getRemoteSocketAddress()
//                                        + " has disconnected!");
                            } catch (IOException ex) {
                                error = true;
                                ex.printStackTrace();
                            }
                        }
                        // todo send packet instead of plain removing --- done!
//                        removeClient(client);
                    }).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        running = true;
    }

    private void readPacket(Packet packet, Socket client) throws IOException {
        if (packet instanceof ConnectPacket) {
            ConnectPacket connectPacket = new ConnectPacket(((ConnectPacket) packet).i_username);
            connectPacket.i_username = ((ConnectPacket) packet).i_username;
            connectClient(connectPacket, client);
        } else if (packet instanceof DisconnectPacket) {
            DisconnectPacket disconnectPacket = (DisconnectPacket) packet;
            ChatServer.getInstance().log("Client " + disconnectPacket.i_username
                    + client.getRemoteSocketAddress() + " has disconnected!");
            sendToAll(disconnectPacket);
            removeClient(client);
        } else if (packet instanceof ChatPacket) {
            ChatPacket chatPacket = (ChatPacket) packet;
            ChatServer.getInstance().log(chatPacket.i_username + ": " + chatPacket.i_message);
            sendToAll(chatPacket);
        } else if (packet instanceof PacketUpdate) {
            PacketUpdate packetUpdate = (PacketUpdate) packet;
            if (packetUpdate.i_content.equals("request;")) {
                Socket s = connectedClientMap.get(packetUpdate.i_username);
                outputStream = new DataOutputStream(s.getOutputStream());
                String users = String.join(SEPARATOR, connectedClientMap.keySet()) + SEPARATOR;
                PacketUpdate pu = new PacketUpdate(packetUpdate.i_username, users);
                outputStream.writeUTF(pu.getOutGoingData());
            }
        }
    }

    private void sendToAll(Packet packet) {
        for (Socket s : connectedClientMap.values()) {
            try {
                DataOutputStream outputStream = new DataOutputStream(s.getOutputStream());
                outputStream.writeUTF(packet.getOutGoingData());
//                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        running = false;
    }

    private void connectClient(ConnectPacket packet, Socket client) {
        if (connectedClientMap.get(packet.i_username) != null) {
            return;
        }
        connectedClientMap.put(packet.i_username, client);
        ChatServer.getInstance().updateView();
        sendToAll(packet);
    }

    private void removeClient(Socket client) {
        // using removeIf to avoid ConcurrentModificationException
        //
        connectedClientMap.entrySet().removeIf(item -> item.getValue().equals(client));
        ChatServer.getInstance().updateView();
    }

    public Map<String, Socket> getConnectedClientMap() {
        return connectedClientMap;
    }
}

// EOF
