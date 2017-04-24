package com.hse.chat;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by kirill on 22.04.17.
 */

class ClientThread implements Runnable {

    private final static String UPDATE_USERS = "updateuserslist:";
    private final static String LOGOUT_MESSAGE = "logoutme:";
    private static final int EXIT_SUCCESS = 0;
    private DataInputStream input;
    private ChatClient client;


    ClientThread(DataInputStream input, ChatClient client) {
        this.input = input;
        this.client = client;
    }

    public void run() {
        String s2 = "";
        try {
            while (true) {
                s2 = input.readUTF();
//                System.out.println(s2);
                if (s2.startsWith(UPDATE_USERS)) {
                    updateUsersList(s2);
                } else if (s2.startsWith(LOGOUT_MESSAGE)) {
                    client.usersList.setListData(new Vector<>());
                    break;
                } else
                    client.txtBroadcast.append(s2);
                int lineOffset = client.txtBroadcast.getLineStartOffset(client.txtBroadcast.getLineCount() - 1);
                client.txtBroadcast.setCaretPosition(lineOffset);
            }
        } catch (EOFException e) {
            int res = JOptionPane.showConfirmDialog
                    (null, "Server has stopped!",
                            "Server Stopped.", JOptionPane.DEFAULT_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    closeEverything();
                    System.out.println("Everything has been successfully closed.");
                    System.exit(EXIT_SUCCESS);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void closeEverything() throws IOException {
        if (input != null)
            input.close();
    }

    private void updateUsersList(String ul) {
        Vector<String> ulist = new Vector<>();
        ul = ul.replace("[", "").
                replace("]", "").
                replace(UPDATE_USERS, "");
        StringTokenizer st = new StringTokenizer(ul, ",");
        while (st.hasMoreTokens()) {
            String temp = st.nextToken();
            ulist.add(temp);
        }
        client.usersList.setListData(ulist);
    }
}


// EOF
