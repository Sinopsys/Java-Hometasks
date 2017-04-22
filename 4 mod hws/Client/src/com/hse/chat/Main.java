package com.hse.chat;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Client charlie = new Client("127.0.0.1");
        charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        charlie.startRunning();
    }
}
