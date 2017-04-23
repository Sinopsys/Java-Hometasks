package com.hse.chat.network;

import com.hse.chat.ChatServer;
import com.hse.chat.network.packet.*;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by kirill on 22.04.17.
 */
public class NetworkServer implements PacketListener {
    // vars
    //
    private ServerSocket socket;
    private boolean running = false;
    private int port;
    private static final String SEPARATOR = ";";
    private List<PacketListener> packetListeners = new ArrayList<>();
    private Map<String, Socket> connectedClientMap = new HashMap<>();


    public NetworkServer(int port) {
        this.port = port;
        addPacketListener(this);
    }

    private void addPacketListener(PacketListener packetListener) {
        packetListeners.add(packetListener);
    }

    public void startServer() {
        try {
            socket = new ServerSocket(port);
            ChatServer.getInstance().log("Server socket initialized on port " + port);
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
                            try {
                                DataInputStream inputStream = new DataInputStream(client.getInputStream());
                                String rawData = inputStream.readUTF();
                                String[] data = rawData.trim().split(SEPARATOR);
                                PacketType type = PacketType.valueOf(data[0]);
                                Packet packet = PacketDictionary.translatePacketType(type, data);

                                broadcastPacketReseived(packet, client);
//                                ChatServer.getInstance().log("Client: " +
//                                        System.lineSeparator() + "\t" + ((ConnectPacket) packet).i_username);
//                                DataInputStream input = new DataInputStream(client.getInputStream());
//                                String request = input.readUTF();
//                                //TODO interpret request
//                                ChatServer.getInstance().log(client.getRemoteSocketAddress()
//                                        + " says:" + System.lineSeparator() + "\t" + request);
                            } catch (EOFException ex) {
                                error = true;
                                ChatServer.getInstance().log("Client " + client.getRemoteSocketAddress()
                                        + " has disconnected!");
                            } catch (IOException ex) {
                                error = true;
                                ex.printStackTrace();
                            }
                        }
                        // todo send packet instead of plain removing
                        removeClient(client);
                    }).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        running = true;
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

    public void stopServer() {
        running = false;
    }

    @Override
    public void packetSent(Packet packet, Socket client) {
        if (packet instanceof ConnectPacket) {

        }
    }

    @Override
    public void packetReseived(Packet packet, Socket client) {
        if (packet instanceof ConnectPacket) {
            connectClient((ConnectPacket) packet, client);
        } else if (packet instanceof ChatPacket) {
            ChatPacket chatPacket = (ChatPacket) packet;
            ChatServer.getInstance().log(chatPacket.i_message + " " + chatPacket.i_username);
        }
    }

    private void connectClient(ConnectPacket packet, Socket client) {
        if (connectedClientMap.get(packet.i_username) != null) {
            return;
        }
        connectedClientMap.put(packet.i_username, client);
        ChatServer.getInstance().updateView();
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
