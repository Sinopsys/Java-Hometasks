package com.hse.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
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
    ArrayList<Integer> highScores = new ArrayList<>();
    public static final int
            ROWS = 4,
            COLS = 4,
            DIM = ROWS * COLS;
    private BufferedImage resized;
    public String path = "";
    boolean pathChanged = false;
    private int numberOfTurns = 0;

    private TagGame(String path) throws URISyntaxException, IOException {
        this.path = path;
        initUI();
    }

    private void initUI() throws URISyntaxException, IOException {
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLS; ++j) {
                solution.add(new Point(i, j));
            }
        }
        setBounds(500, 100, 600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        readHighScores();


        tags = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(ROWS, COLS, 0, 0));

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        for (String fileItem : new String[]{"Open", "Shuffle", "Show source img",
                "Show highscore table", "Clear highscore table", "Exit"}) {
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
                                    "Image inappropriate! Choose another one.",
                                    "Exception occured",
                                    JOptionPane.ERROR_MESSAGE);
                            System.out.println("Image inappropriate! Choose another one.");
                        } catch (URISyntaxException | IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case "Exit":
                        System.exit(0);
                        break;
                    case "Shuffle":
                        if (sourceImage != null) {
                            TagGame tg;
                            try {
                                tg = new TagGame(this.path);
                                tg.setVisible(true);
                                this.dispose();
                                numberOfTurns = 0;
                            } catch (URISyntaxException | IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Nothing to shuffle",
                                    "Load an image first.",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case "Show source img":
                        showSourceImage();
                        break;
                    case "Show highscore table":
                        showTableofHighscores(false);
                        break;
                    case "Clear highscore table":
                        try {
                            ClearHighscoreTable();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
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

//
//            Collections.shuffle(tags);
//
//            boolean solvable = false;
//
//            while (!solvable) {
//                System.out.println("generating solvable puzzle");
//                ArrayList<Point> current = new ArrayList<>();
//                for (Tag btn : tags) {
//                    current.add((Point) btn.getClientProperty("position"));
//                }
//
//                current.add((Point) lastButton.getClientProperty("position"));
//
//                int[] currentPositions = new int[DIM];
//                for (int i = 0; i < DIM; ++i) {
//                    currentPositions[i] = current.get(i).x * ROWS + current.get(i).y;
//                }
//                Collections.shuffle(tags);
//
//                solvable = isSolvable(currentPositions);
//                System.out.println("solvable: " + solvable);
//            }

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

    private boolean isSolvable(int[] puzzle) {
        int parity = 0;
        int gridWidth = (int) Math.sqrt(puzzle.length);
        int row = 0; // the current row we are on
        int blankRow = 0; // the row with the blank tile

        for (int i = 0; i < puzzle.length; i++) {
            if (i % gridWidth == 0) { // advance to next row
                row++;
            }
            if (puzzle[i] == 0) { // the blank tile
                blankRow = row; // save the row on which encountered
                continue;
            }
            for (int j = i + 1; j < puzzle.length; j++) {
                if (puzzle[i] > puzzle[j] && puzzle[j] != 0) {
                    parity++;
                }
            }
        }

        if (gridWidth % 2 == 0) { // even grid
            if (blankRow % 2 == 0) { // blank on odd row; counting from bottom
                return parity % 2 == 0;
            } else { // blank on even row; counting from bottom
                return parity % 2 != 0;
            }
        } else { // odd grid
            return parity % 2 == 0;
        }
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
            lastButton.setEnabled(false);
            for (Tag tag : tags) {
                tag.setEnabled(false);
            }

            try {
                writeHighScore();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int minimum = Integer.MAX_VALUE;
            try {
                minimum = Collections.min(highScores);
                highScores = removeDuplicates(highScores);
            } catch (NoSuchElementException ignored) {
            }
            if (numberOfTurns < minimum) {
                int res = JOptionPane.showOptionDialog(null,
                        "YOU WON! NEW HIGHSCORE: " + numberOfTurns + "! Show table of winners?", "CONGRATULATIONS!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, null, null);
                switch (res) {
                    case JOptionPane.YES_OPTION:
                        showTableofHighscores(true);
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                }
                System.err.println("The game has ended. You won.");
            } else {
                int res = JOptionPane.showOptionDialog(null, "YOU WON! " +
                                "Show table of winners?", "CONGRATULATIONS!!!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, null, null);
                switch (res) {
                    case JOptionPane.YES_OPTION:
                        showTableofHighscores(true);
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                }
            }
            highScores.add(numberOfTurns);
        }
    }

    private void ClearHighscoreTable() throws IOException {
        int res = JOptionPane.showOptionDialog(null, "Are you sure?", "Clear history of matches",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, null, null);
        switch (res) {
            case JOptionPane.YES_OPTION:
                highScores.clear();
                Files.deleteIfExists(Paths.get("highScores.txt"));
                break;
            case JOptionPane.NO_OPTION:
                break;
        }
    }

    private static boolean compareList(ArrayList<Point> ls1, ArrayList<Point> ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    private void showTableofHighscores(boolean you) {
        highScores = removeDuplicates(highScores);
        ArrayList<Integer> cpy = new ArrayList<>();
        cpy.addAll(highScores);
        if (numberOfTurns != 0) {
            cpy.add(numberOfTurns);
        }
        cpy = removeDuplicates(cpy);
        Collections.sort(cpy);
        int indexYour = cpy.indexOf(numberOfTurns);
        StringBuilder msg = new StringBuilder("place  score \n");
        for (int i = 0; i < cpy.size(); ++i) {
            if (you && i == indexYour) {
                msg.append("    ").append(i + 1).append("          ").append(cpy.get(i)).append("<-- YOU").append("\n");
                continue;
            }
            msg.append("    ").append(i + 1).append("          ").append(cpy.get(i)).append("\n");
        }
        if (highScores.size() == 0 && numberOfTurns == 0)
            msg = new StringBuilder("No games has been played yet.");
        JOptionPane.showMessageDialog(null, msg,
                "Table of scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void readHighScores() throws IOException {
        if (!Files.exists(Paths.get("highScores.txt"))) {
            Files.createFile(Paths.get("highScores.txt"));
        }
        Scanner scanner = new Scanner(new File("highScores.txt"));
        while (scanner.hasNextInt()) {
            highScores.add(scanner.nextInt());
        }
    }

    private void writeHighScore() throws IOException {
        if (!Files.exists(Paths.get("highScores.txt"))) {
            Files.createFile(Paths.get("highScores.txt"));
        }
        FileWriter fileWriter = new FileWriter("highScores.txt", true);
        fileWriter.write("\n" + numberOfTurns);
        fileWriter.close();
    }

    private ArrayList<Integer> removeDuplicates(ArrayList<Integer> inp) {
        Set<Integer> hs = new HashSet<>();
        hs.addAll(inp);
        inp.clear();
        inp.addAll(hs);

        return inp;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TagGame game = null;
            try {
                game = new TagGame("");
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
            if (game != null) {
                game.setVisible(true);
            }
        });
    }
}

// EOF
