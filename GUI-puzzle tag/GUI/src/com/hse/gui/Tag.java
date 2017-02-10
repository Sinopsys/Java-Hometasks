package com.hse.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by kirill on 07.02.17.
 */
class Tag extends JButton {
    private boolean isLastButton;

    Tag() {
        super();
        initUI();
    }

    Tag(Image image) {
        super(new ImageIcon(image));
        initUI();
    }

    private void initUI() {
        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.cyan));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    void setLastButton() {
        isLastButton = true;
    }

    boolean isLastButton() {
        return isLastButton;
    }
}

