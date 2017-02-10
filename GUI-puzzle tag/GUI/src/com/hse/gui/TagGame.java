package com.hse.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TagGame extends JFrame {
    private JPanel panel;
    private BufferedImage sourceImage;
    private ArrayList<Tag> tags;
    private ArrayList<Point> solution = new ArrayList<>();
    private Image image;
    private Tag lastButton;
    private int width, height;
    private static final int DESIRED_WIDTH = 600;
    public static final int
            ROWS = 4,
            COLS = 4,
            DIM = ROWS * COLS;
    private BufferedImage resized;
    public String path = "";
    boolean pathChanged = false;
    private int numberOfTurns = 0;

    private TagGame(String path) throws URISyntaxException {
        this.path = path;
        initUI();
    }

    private void initUI() throws URISyntaxException {
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLS; ++j) {
                solution.add(new Point(i, j));
            }
        }
        setBounds(500, 100, 600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        tags = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(ROWS, COLS, 0, 0));

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        for (String fileItem : new String[]{"Open", "Shuffle", "Show source img", "Exit"}) {
            JMenuItem item = new JMenuItem(fileItem);
            item.setActionCommand(fileItem);
            item.addActionListener(e -> {
                String command = e.getActionCommand();
                switch (command) {
                    case "Open":
                        try {
                            openImage();
                            if (pathChanged) {
                                TagGame tg = new TagGame(this.path);
                                tg.setVisible(true);
                                this.dispose();
                            }
                        } catch (RasterFormatException e1) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Image too small! Choose another one.",
                                    "Exception occured",
                                    JOptionPane.ERROR_MESSAGE);
                            System.out.println("Image too small! Choose another one.");
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case "Exit":
                        System.exit(0);
                        break;
                    case "Shuffle":
                        TagGame tg = null;
                        try {
                            tg = new TagGame(this.path);
                            tg.setVisible(true);
                            this.dispose();

                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case "Show source img":
                        showSourceImage();
                        break;
                }
            });
            fileMenu.add(item);
        }

        menu.add(fileMenu);
        setJMenuBar(menu);


        if (!path.equals("")) {
            try {
                sourceImage = loadImage();
                int h = getNewHeight(sourceImage.getWidth(), sourceImage.getHeight());
                int h_;
                if (h > DESIRED_WIDTH) {
                    h_ = DESIRED_WIDTH;
                } else {
                    h_ = h;
                }
                resized = resizeImage(sourceImage, DESIRED_WIDTH, h_,
                        BufferedImage.TYPE_INT_ARGB);
                sourceImage = resizeImage(sourceImage, DESIRED_WIDTH, h,
                        BufferedImage.TYPE_INT_ARGB);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            width = resized.getWidth(this);
            height = resized.getHeight(this);

            add(panel, BorderLayout.CENTER);

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    image = createImage(new FilteredImageSource(resized.getSource(),
                            new CropImageFilter(j * width / COLS, i * height / ROWS,
                                    (width / ROWS), height / COLS)));
                    Tag button = new Tag(image);
                    button.putClientProperty("position", new Point(i, j));

                    if (i == ROWS - 1 && j == COLS - 1) {
                        lastButton = new Tag();
                        lastButton.setBorderPainted(false);
                        lastButton.setContentAreaFilled(false);
                        lastButton.setLastButton();
                        lastButton.putClientProperty("position", new Point(i, j));
                    } else {
                        tags.add(button);
                    }
                }
            }

            Collections.shuffle(tags);
            tags.add(lastButton);

            for (int i = 0; i < DIM; i++) {
                panel.add(tags.get(i));
                tags.get(i).setBorder(BorderFactory.createLineBorder(Color.gray));
                tags.get(i).addActionListener(new ClickAction());
            }
            pack();
        }
        setTitle("Tag game. By Sinopsys.");
        setLocationRelativeTo(null);
        setResizable(false);
    }


    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);
        if (fileChooser.showDialog(null, "open file") == JFileChooser.APPROVE_OPTION) {
            pathChanged = !fileChooser.getSelectedFile().getAbsolutePath().equals(path);
            path = fileChooser.getSelectedFile().getAbsolutePath();
        }

    }

    private void showSourceImage() {
        if (sourceImage == null)
            JOptionPane.showMessageDialog(this,
                    "No image was uploaded yet");
        else {
            ImageIcon imageIcon = new ImageIcon(sourceImage);
            JOptionPane.showMessageDialog(
                    null,
                    "",
                    "SOURCE IMAGE", JOptionPane.INFORMATION_MESSAGE,
                    imageIcon);
        }
    }

    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        return (int) (h * ratio);
    }

    private BufferedImage loadImage() throws IOException, URISyntaxException {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type)
            throws IOException {

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private class ClickAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            numberOfTurns++;
            checkWin();
        }

        private void checkButton(ActionEvent e) {
            int lastIndex = 0;
            for (int i = 0; i < DIM; ++i) {
                if (tags.get(i).isLastButton()) {
                    lastIndex = i;
                }
            }
            JButton button = (JButton) e.getSource();
            int buttonIndex = tags.indexOf(button);

            if ((buttonIndex - 1 == lastIndex) || (buttonIndex + 1 == lastIndex)
                    || (buttonIndex - ROWS == lastIndex) || (buttonIndex + COLS == lastIndex)) {
                Collections.swap(tags, buttonIndex, lastIndex);
                updateButtons();
            }
        }

        private void updateButtons() {
            panel.removeAll();
            for (Tag btn : tags) {
                panel.add(btn);
            }
//            panel.updateUI();
            panel.revalidate();
            panel.repaint();
        }
    }

    private void checkWin() {
        ArrayList<Point> current = new ArrayList<>();
        for (Tag btn : tags) {
            current.add((Point) btn.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            lastButton.setIcon(null);
            lastButton.setText("ХОДОВ:" + numberOfTurns);
            JOptionPane.showMessageDialog(null, "YOU WON!",
                    "CONGRATULATIONS!!!", JOptionPane.INFORMATION_MESSAGE);
            System.err.println("The game has ended. You won.");
        }
    }

    private static boolean compareList(ArrayList<Point> ls1, ArrayList<Point> ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TagGame game = null;
            try {
                game = new TagGame("");
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
            if (game != null) {
                game.setVisible(true);
            }
        });
    }
}

// EOF
