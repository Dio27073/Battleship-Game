package gui;

import core.Game;
import core.GameState;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;


public class GameController {
    private Game game;
    private JFrame mainFrame;
    private JPanel boardPanel;
    private JButton[][] boardButtons;
    private JLabel statusLabel;

    public GameController(){
        game = new Game("Player 1", "Player 2");
        initializeGUI();
    }

    private void initializeGUI() {
        mainFrame = new JFrame("Battleship Game");
        boardPanel = new JPanel(new GridLayout(10, 10)); // assuming a 10x10 board
        boardButtons = new JButton[10][10];

        //initialize buttons and add to the board panel
        for (int i = 0; i < boardButtons.length; i++) {
            for (int j = 0; j < boardButtons[i].length; j++) {
                JButton button = new JButton();
                int row = i;
                int col = j;
                button.addActionListener(e -> handleCellClick(row, col));
                boardPanel.add(button);
                boardButtons[i][j] = button;
            }
        }

        statusLabel = new JLabel("Setup your ships.");
        mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(statusLabel, BorderLayout.SOUTH);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

        updateGameState(); //set initial state
    }

    private void handleCellClick(int row, int col) {
        if (game.getGameState() == GameState.SETUP) {
            // Handle ship placement logic
        } else if (game.getGameState() == GameState.IN_PROGRESS) {
            boolean hit = game.takeTurn(row, col);
            updateCellButton(row, col, hit);
            checkForGameEnd();
        }
        updateGameState();
    }

    private void updateCellButton(int row, int col, boolean hit) {
        JButton button = boardButtons[row][col];
        // Update button based on hit or miss
        if (hit) {
            button.setText("Hit");
        } else {
            button.setText("Miss");
        }
        button.setEnabled(false); // Disable button after it's clicked
    }

    private void checkForGameEnd() {
        if (game.getGameState() == GameState.GAME_OVER) {
            JOptionPane.showMessageDialog(mainFrame, "Game Over! Winner: " + game.getWinner().getName());
        }
    }

    public void updateGameState() {
        GameState currentState = game.getGameState();
        switch (currentState) {
            case SETUP:
                statusLabel.setText("Setup your ships.");
                // Code to update GUI for ship placement
                break;
            case IN_PROGRESS:
                statusLabel.setText("Game in progress. " + game.getCurrentPlayer().getName() + "'s turn.");
                // Code to update GUI for in-progress game
                break;
            case GAME_OVER:
                statusLabel.setText("Game Over! Winner: " + game.getWinner().getName());
                // Code to update GUI when the game is over
                // Disable all buttons or show a dialog to restart the game
                for (JButton[] buttonRow : boardButtons) {
                    for (JButton button : buttonRow) {
                        button.setEnabled(false);
                    }
                }
                break;
        }
        // ... additional UI updates
    }

    // ... additional methods to handle user actions and update the GUI
}
