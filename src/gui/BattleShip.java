package gui;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;

public class BattleShip extends JFrame {
    private Game game;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JPanel playerBoardPanel;
    private JPanel guessBoardPanel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final Color EMPTY_TILE_COLOR = Color.LIGHT_GRAY;
    private static final int BOARD_GAP_WIDTH = 5; // Adjust this value to your preference for gap width

    private JPanel createRulesPanel() {
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BorderLayout()); // You can choose a different layout as needed

        // Set the preferred width (and height) for the rules panel
        rulesPanel.setPreferredSize(new Dimension(200, this.getHeight())); // Adjust width as needed

        JTextArea rulesText = new JTextArea("Rules of the Game:\n\n -Grey Tiles are your ships\n\n -Blue Tiles are the sea" +
                "\n\n -If you hit an enemy ship or yours is hit, it will appear as Red\n\n -If you miss your shot, the Tile will" +
                "become white");
        rulesText.setEditable(false); // Make the text area non-editable
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);

        // Add a scroll pane in case the text is longer than the panel
        JScrollPane scrollPane = new JScrollPane(rulesText);
        rulesPanel.add(scrollPane, BorderLayout.CENTER);

        // Add a clickable link for more rules
        JLabel linkLabel = new JLabel("<html><a href=''>More Rules</a></html>");
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.ultraboardgames.com/battleship/game-rules.php")); // Put the URL of your rules page here
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        rulesPanel.add(linkLabel, BorderLayout.SOUTH);

        return rulesPanel;
    }
    /*
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345); // Server IP and port
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a new thread to listen for server messages
            new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        // Process and update game state based on server's response
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendToServer(String data) {
        out.println(data);
    }
    */

    public BattleShip() {
        // Initialize the game
        game = new Game("Player1", "Player2");
        game.getPlayer1().autoPlaceShips();
        game.getPlayer2().autoPlaceShips();

        // Set up the game window
        setTitle("Battleship Game");
        setSize(800, 1000); // Adjust the window size as needed to accommodate the guess board
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the game board panel
        boardPanel = new JPanel();
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS)); // Use BoxLayout to stack boards vertically
        initializeBoardPanel(game.getCurrentPlayer());
        add(boardPanel, BorderLayout.CENTER);

        JPanel rulesPanel = createRulesPanel();
        add(rulesPanel, BorderLayout.EAST);

        // Set up the status label at the bottom
        statusLabel = new JLabel("Player1's turn");
        add(statusLabel, BorderLayout.SOUTH);

        // Start the game
        game.startGame();
        //connectToServer();
    }

    private void initializeBoard(Board board, JPanel boardPanel, Player currentPlayer, boolean isPlayerBoard) {
        Color theSea = new Color(173, 216, 230);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                JButton button = new JButton();
                Cell cell = board.getCell(row, col);

                // Updated logic to display ships on player's board
                if (isPlayerBoard) {
                    if (cell.isOccupied()) {
                        button.setBackground(Color.GRAY); // Gray for ship location
                    } else {
                        button.setBackground(theSea);
                    }
                    button.setEnabled(false); // Disable buttons on player's board
                } else {
                    // Logic for guess board
                    if (cell.isHit()) {
                        if (cell.isOccupied()) {
                            button.setBackground(Color.RED); // Red for hit ship
                        } else {
                            button.setBackground(Color.WHITE); // White for miss
                        }
                    } else {
                        button.setBackground(theSea);
                    }
                    button.setEnabled(!cell.isHit()); // Enable only unhit cells on guess board
                }

                int finalRow = row;
                int finalCol = col;
                button.addActionListener(e -> {
                    if (isPlayerBoard) {
                        // No action if the player's own board is clicked
                    } else {
                        handleCellClick(finalRow, finalCol, button); // Handle clicks on the guess board
                    }
                });
                boardPanel.add(button);
            }
        }
    }
    private void initializeBoardPanel(Player currentPlayer) {
        playerBoardPanel = new JPanel(new GridLayout(10, 10));
        guessBoardPanel = new JPanel(new GridLayout(10, 10));

        Board playerBoard = currentPlayer.getBoard();
        Board opponentBoard = game.getOpponentBoard(currentPlayer);

        initializeBoard(playerBoard, playerBoardPanel, currentPlayer, true);
        initializeBoard(opponentBoard, guessBoardPanel, currentPlayer, false);

        // Create rigid areas for the gap and labels
        Component gap = Box.createRigidArea(new Dimension(0, BOARD_GAP_WIDTH)); // Adjust the height of the gap
        JLabel yourBoardLabel = new JLabel("Your Board");
        JLabel opponentBoardLabel = new JLabel("Opponent's Board");

        // Add labels and the guess board above the player's board
        boardPanel.removeAll();
        boardPanel.add(opponentBoardLabel);
        boardPanel.add(guessBoardPanel);
        boardPanel.add(gap);
        boardPanel.add(yourBoardLabel);
        boardPanel.add(playerBoardPanel);

        // Disable buttons on the player's board
        disableButtons(playerBoardPanel);

        // Enable buttons on the guess board
        enableButtons(guessBoardPanel);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    // Helper method to disable buttons on a panel
    private void disableButtons(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setEnabled(false);
            }
        }
    }

    // Helper method to enable buttons on a panel
    private void enableButtons(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setEnabled(true);
            }
        }
    }

    /*
    private void initializeGuessBoard(JPanel guessBoardPanel) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                JButton button = new JButton();
                button.setBackground(EMPTY_TILE_COLOR); // Set empty tiles to a specific color

                int finalRow = row;
                int finalCol = col;
                button.addActionListener(e -> handleGuessClick(finalRow, finalCol, button));
                guessBoardPanel.add(button);
            }
        }
    }*/

    private void handleCellClick(int row, int col, JButton button) {
        Player currentPlayer = game.getCurrentPlayer();
        Board opponentBoard = game.getOpponentBoard(currentPlayer);

        // Check if the cell has already been guessed
        Cell cell = opponentBoard.getCell(row, col);
        if (cell.isHit()) {
            // Cell has already been guessed, do nothing
            return;
        }

        // Player takes a shot
        boolean hit = opponentBoard.shootAt(row, col);
        updateButtonState(button, hit);

        String hitOrMiss = hit ? "HIT" : "MISS";
        statusLabel.setText(currentPlayer.getName() + " shoots at (" + (row + 1) + "," + (col + 1) + ") and it is a " + hitOrMiss);


        // Switch to the next player's turn
        game.endTurn();
        currentPlayer = game.getCurrentPlayer();
        initializeBoardPanel(currentPlayer);
        statusLabel.setText(currentPlayer.getName() + "'s turn");

        // Check for a winner
        if (game.isGameOver()) {
            Player winner = game.getWinner();
            JOptionPane.showMessageDialog(this, "Game Over! The winner is " + winner.getName());
        } else {
            // Switch to the next player's board and update the status label
            currentPlayer = game.getCurrentPlayer();
            initializeBoardPanel(currentPlayer);
            statusLabel.setText(currentPlayer.getName() + "'s turn");
        }
    }
    private void updateButtonState(JButton button, boolean hit) {
        if (hit) {
            button.setBackground(Color.RED); // Red for hit ship
        } else {
            button.setBackground(Color.WHITE); // Blue for miss
        }
        button.setEnabled(false);
    }

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Battleship Game");

        // Try setting the Nimbus Look and Feel for cross-platform UI consistency
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the cross-platform Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Disable hardware acceleration if needed (optional)
         System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BattleShip().setVisible(true);
            }
        });
    }
}