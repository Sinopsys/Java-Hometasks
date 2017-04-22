package com.hse.chat;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kirill on 22.04.17.
 */

class Server extends JFrame {

    private JTextArea userMessageArea;
    private JTextArea charArea;
    private JTextField portField;
    private JButton startButton;
    private JButton stopButton;
    private JPanel mainPanel;
    private JPanel upperPanel;
    private JPanel lowerPanel;
    private JLabel portLabel;

    public Server() {
        super("Chat Server");
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(500, 100, 300, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(2, 2));

        upperPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        portLabel = new JLabel("Port");
        upperPanel.add(portLabel);

        portField = new JTextField(9);
        upperPanel.add(portField);

        startButton = new JButton("Start");
        upperPanel.add(startButton);

        stopButton = new JButton("Stop");
        upperPanel.add(stopButton);

        mainPanel.add(upperPanel, BorderLayout.PAGE_START);

        charArea = new JTextArea();
        mainPanel.add(charArea, BorderLayout.CENTER);

        lowerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userMessageArea = new JTextArea("your message here...", 14, 10);


        lowerPanel.add(userMessageArea);
        mainPanel.add(lowerPanel, BorderLayout.PAGE_END);
        add(mainPanel);
    }
}
