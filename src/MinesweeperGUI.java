import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MinesweeperGUI {
    private final Game game = new Game();
    private final JFrame frame;
    private JPanel boardPanel;
    private Map<Point, JButton> buttonMap;
    private JPanel controlPanel;
    private JComboBox diffBox;
    private JComboBox shapeBox;
    private JButton btnNewGame;
    private JButton btnStuck;
    private JPanel mainPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinesweeperGUI::new);
    }

    public MinesweeperGUI() {
        frame = new JFrame("Java Swing Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);

        btnNewGame.addActionListener(e -> {
            String diff = (String) diffBox.getSelectedItem();
            Game.ShapeOption selectedShape;
            switch ((String)shapeBox.getSelectedItem()){
                case "Cross": selectedShape = Game.ShapeOption.CROSS; break;
                case "Z": selectedShape = Game.ShapeOption.Z; break;
                case "Circle": selectedShape = Game.ShapeOption.CIRCLE; break;
                case "Ring": selectedShape = Game.ShapeOption.RING; break;
                default: selectedShape = Game.ShapeOption.RECTANGLE; break;
            }
            if ("Custom".equals(diff)) {
                handleCustomGame(selectedShape);
            } else startNewGame(diff, selectedShape);
        });

        btnStuck.addActionListener(e -> {
            if (game.isPlaying()) {
                game.stuck();
                updateGUIBoard();
            }
        });

        startNewGame("Beginner", Game.ShapeOption.RECTANGLE);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleCustomGame(Game.ShapeOption shape) {
        JTextField colsField = new JTextField("20");
        JTextField rowsField = new JTextField("20");
        JTextField ratioField = new JTextField("0.15");

        Object[] message = {
                "Columns (Width):", colsField,
                "Rows (Height):", rowsField,
                "Bomb Ratio (e.g. 0.15 for 15% of cells):", ratioField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Custom Game Settings", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int c = Integer.parseInt(colsField.getText().trim());
                int r = Integer.parseInt(rowsField.getText().trim());
                double ratio = Double.parseDouble(ratioField.getText().trim());

                if (c < 1 || r < 1 || ratio <= 0 || ratio >= 1) {
                    JOptionPane.showMessageDialog(frame, "Invalid limits. Rows/Cols must be > 0. Ratio must be between 0 and 1.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                game.resetGame();
                game.generateBoard(c, r, ratio, shape);
                buildGUIBoard(c, r);
                updateStatus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startNewGame(String difficulty, Game.ShapeOption shape) {
        game.resetGame();

        int cols = 9, rows = 9;
        double ratio = 0.1;

        switch (difficulty) {
            case "Beginner":
                cols = 9; rows = 9; ratio = 0.1; break;
            case "Intermediate":
                cols = 16; rows = 16; ratio = 0.15; break;
            case "Expert":
                cols = 30; rows = 16; ratio = 0.2; break;
        }

        game.generateBoard(cols, rows, ratio, shape);
        buildGUIBoard(cols, rows);
    }

    private void buildGUIBoard(int cols, int rows) {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols, 1, 1));
        buttonMap = new HashMap<>();

        Dimension cellSize = new Dimension(40, 40);

        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                Point p = new Point(col, row);
                Cell cell = game.board.getCell(p);

                if (cell == null) {
                    JPanel emptySpace = new JPanel();
                    emptySpace.setBackground(Color.DARK_GRAY);
                    emptySpace.setPreferredSize(cellSize);
                    boardPanel.add(emptySpace);
                } else {
                    JButton btn = new JButton();
                    btn.setPreferredSize(cellSize);

                    btn.setFocusPainted(false);
                    btn.setFont(new Font("Arial", Font.BOLD, 20));
                    btn.setMargin(new Insets(0, 0, 0, 0));
                    btn.setBackground(Color.LIGHT_GRAY);

                    btn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if (!game.isPlaying()) return;

                            if (SwingUtilities.isRightMouseButton(e)) {
                                game.setFlag(p);
                            } else if (SwingUtilities.isLeftMouseButton(e)) {
                                game.check(p);
                            }

                            updateGUIBoard();
                            checkGameState();
                        }
                    });

                    buttonMap.put(p, btn);
                    boardPanel.add(btn);
                }
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void updateGUIBoard() {
        for (Map.Entry<Point, JButton> entry : buttonMap.entrySet()) {
            Point p = entry.getKey();
            JButton btn = entry.getValue();
            Cell cell = game.board.getCell(p);

            btn.setOpaque(true);
            btn.setBorderPainted(!cell.isRevealed());

            if (cell.isRevealed()) {
                btn.setBackground(Color.WHITE);

                if (cell.isMine()) {
                    btn.setText("●");
                    btn.setForeground(Color.BLACK);
                    btn.setBackground(Color.RED);
                } else if (cell.getAdjMines() > 0) {
                    btn.setText(String.valueOf(cell.getAdjMines()));
                    setColorForNumber(btn, cell.getAdjMines());
                } else {
                    btn.setText("");
                }
            } else {
                btn.setBackground(Color.LIGHT_GRAY);

                if (cell.isFlagged()) {
                    btn.setText("■");
                    btn.setForeground(Color.BLUE);
                } else {
                    btn.setText("");
                }
            }
        }
        updateStatus();
    }

    private void updateStatus() {
        if (statusLabel != null) {
            statusLabel.setText("Flags Placed: " + game.getFlagCount() + " / " + game.getBombCount());
        }
    }

    private void checkGameState() {
        if (!game.isPlaying()) {
            if (game.board.checkWin()) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won!", "Victory", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Mine! Game over!", "Game Over", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setColorForNumber(JButton btn, int num) {
        switch (num) {
            case 1: btn.setForeground(Color.BLUE); break;
            case 2: btn.setForeground(new Color(0, 153, 0)); break; // Dark Green
            case 3: btn.setForeground(Color.RED); break;
            case 4: btn.setForeground(new Color(0, 0, 153)); break; // Dark Blue
            case 5: btn.setForeground(new Color(153, 0, 0)); break; // Dark Red
            case 6: btn.setForeground(new Color(0, 153, 153)); break; // Cyan
            case 7: btn.setForeground(Color.BLACK); break;
            case 8: btn.setForeground(Color.DARK_GRAY); break;
            default: btn.setForeground(Color.BLACK); break;
        }
    }
}