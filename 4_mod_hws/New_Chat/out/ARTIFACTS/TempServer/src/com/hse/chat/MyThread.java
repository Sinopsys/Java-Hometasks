package com.hse.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kirill on 22.04.17.
 */

class MyThread implements Runnable {
    // constants
    //
    private final static String UPDATE_USERS = "updateuserslist:";
    private final static String LOGOUT_MESSAGE = "logoutme:";

    private ArrayList<Socket> sockets;
    private ArrayList<String> users;
    private Socket s;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    private String userName;


    MyThread(Socket s, ArrayList<Socket> sockets, ArrayList<String> users) {
        this.s = s;
        this.users = users;
        this.sockets = sockets;
        try {
            DataInputStream input = new DataInputStream(s.getInputStream());
            sockets.add(s);
            userName = input.readUTF();
            users.add(userName);
            broadCast(userName + " logged in at " + DATE_FORMAT.format(new Date()) + System.lineSeparator());
            sendNewUserList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String s1;
        try {
            DataInputStream input = new DataInputStream(s.getInputStream());
            while (true) {
                s1 = input.readUTF();
                if (s1.toLowerCase().equals(LOGOUT_MESSAGE)) {
                    break;
                }
                ChatServer.getInstance().log(userName + ": " + s1);
                broadCast(DATE_FORMAT.format(new Date()) + " " + userName + ": " + s1 + System.lineSeparator());
            }

            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            ChatServer.getInstance().log("Client " + s.getRemoteSocketAddress()
                    + " " + userName + " has disconnected!");
            output.writeUTF(LOGOUT_MESSAGE);
            output.flush();

            users.remove(userName);
            broadCast(userName + "log out at " + (new Date()));
            sendNewUserList();
            sockets.remove(s);
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadCast(String str) {
        for (Socket socket : sockets) {
            try {
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(str);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendNewUserList() {
        broadCast(UPDATE_USERS + users.toString());
        ChatServer.getInstance().updateView();
    }
}


// EOF
