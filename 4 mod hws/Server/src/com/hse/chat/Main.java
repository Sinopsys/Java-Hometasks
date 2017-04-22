package com.hse.chat;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Server sally = new Server();
        sally.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        sally.startRunning();
    }
}
