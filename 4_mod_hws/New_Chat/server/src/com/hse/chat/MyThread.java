package com.hse.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by kirill on 24.04.17.
 */
class MyThread implements Runnable {
    ArrayList<Socket> sockets;
    ArrayList<String> users;
    Socket s;
    String userName;

    public MyThread(Socket s, ArrayList sockets, ArrayList users) {
        this.s = s;
        this.users = users;
        this.sockets = sockets;
        try {
            DataInputStream input = new DataInputStream(s.getInputStream());
            sockets.add(s);
            userName = input.readUTF();
            users.add(userName);
            broadCast(userName + " Logged in at " + (new Date()));
            sendNewUserList();
        } catch (Exception e) {
            System.err.println("MyThread constructor " + e);
        }
    }

    public void run() {
        String s1;
        try {
            DataInputStream input = new DataInputStream(s.getInputStream());
            while (true) {
                s1 = input.readUTF();
                if (s1.toLowerCase().equals(ChatServer.LOGOUT_MESSAGE)) {
                    break;
                }
                broadCast(userName + " said: " + s1);
            }

            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            output.writeUTF(ChatServer.LOGOUT_MESSAGE);
            output.flush();

            users.remove(userName);
            broadCast(userName + "log out at " + (new Date()));
            sendNewUserList();
            sockets.remove(s);
            s.close();

        } catch (Exception e) {
            System.err.println("MyThread run " + e);
        }
    }

    public void broadCast(String str) {
        Iterator iter = sockets.iterator();
        while (iter.hasNext()) {
            try {
                Socket broadSoc = (Socket) iter.next();
                DataOutputStream output = new DataOutputStream(broadSoc.getOutputStream());
                output.writeUTF(str);
                //This forces any buffered output bytes to be written out to the stream.
                output.flush();
            } catch (Exception e) {
                System.err.println("MyThread broadCast " + e);
            }
        }

    }

    //a little bug here that not update on his own user list
    public void sendNewUserList() {
        broadCast(ChatServer.UPDATE_USERS + users.toString());
        ChatServer.getInstance().updateView();
    }
}
