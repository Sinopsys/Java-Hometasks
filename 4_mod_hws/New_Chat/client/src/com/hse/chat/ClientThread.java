package com.hse.chat;


import java.io.DataInputStream;
import java.util.StringTokenizer;
import java.util.Vector;

class ClientThread implements Runnable {
    DataInputStream input;
    ChatClient client;

    public ClientThread(DataInputStream input, ChatClient client) {
        this.input = input;
        this.client = client;
    }

    public void run() {
        String s2 = "";
        try {
            while (true) {
                s2 = input.readUTF();
                System.out.println(s2);
                if (s2.startsWith(ChatServer.UPDATE_USERS)) {
                    updateUsersList(s2);
                } else if (s2.startsWith(ChatServer.LOGOUT_MESSAGE)) {
                    //if user logout, clear it's user list
                    client.usersList.setListData(new Vector());
                    break;
                } else
                    client.txtBroadcast.append("\n" + s2);
                //
                int lineOffset = client.txtBroadcast.getLineStartOffset(client.txtBroadcast.getLineCount() - 1);
                client.txtBroadcast.setCaretPosition(lineOffset);
            }
        } catch (Exception e) {
            System.err.println("ClientThread Run " + e);
        }
    }

    public void updateUsersList(String ul) {
        Vector ulist = new Vector();

        ul = ul.replace("[", "");
        ul = ul.replace("]", "");
        ul = ul.replace(ChatServer.UPDATE_USERS, "");
        StringTokenizer st = new StringTokenizer(ul, ",");

        while (st.hasMoreTokens()) {
            String temp = st.nextToken();
            ulist.add(temp);
        }
        client.usersList.setListData(ulist);
    }
}
