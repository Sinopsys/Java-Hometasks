package com.hse.chat.network;

import com.hse.chat.network.packet.ConnectPacket;
import com.hse.chat.network.packet.Packet;
import com.hse.chat.network.packet.PacketDictionary;
import com.hse.chat.network.packet.PacketType;
import javafx.application.Application;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by kirill on 22.04.17.
 */
public class NetworkClient {
    // constants
    //
    private static final String SEPARATOR = ";";
    private static final int EXIT_SUCCESS = 0;

    // network vars
    //
    private Socket socket;
    private String ipAddress;
    private int serverPort;
    private DataInputStream inputStream;


    public NetworkClient(String ipAddress, int serverPort) {
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
    }

    public void connectToServer() {
        try {
            socket = new Socket(ipAddress, serverPort);
//            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//            output.writeUTF("Hello, server!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Server not found. " +
                    "Ask admin to start it and relaunch your chat.");
            return;
        }

        String username = JOptionPane.showInputDialog(null, "Enter your username: ",
                "Input prompt", JOptionPane.QUESTION_MESSAGE);
        sendPacket(new ConnectPacket(username));

        // client listens for server requests / responses
        new Thread(() -> {
            while (true) {
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    String rawData = inputStream.readUTF();
                    String[] data = rawData.trim().split(SEPARATOR);
                    PacketType type = PacketType.valueOf(data[0]);
                    Packet packet = PacketDictionary.translatePacketType(type, data);
                } catch (IOException e) {
                    int res = JOptionPane.showConfirmDialog
                            (null, "Server has stopped!",
                                    "Server Stopped.", JOptionPane.DEFAULT_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        try {
                            socket.close();
                            inputStream.close();
                            System.out.println("Everything has been successfully closed.");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } finally {
                    System.exit(EXIT_SUCCESS);
                }

            }
        }).start();
    }

    private void sendPacket(Packet packet) {
        try {
            // never do try-with-resourses... the mistake killed 3.5h of my life...
            // I'm so hopping-mad and frustrated!
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(packet.getOutGoingData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// EOF
