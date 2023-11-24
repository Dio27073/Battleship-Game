

import core.*;

public class Main {
    public static void main(String[] args) {
        // Initialize the game with two players
        Game game = new Game("Player1", "Player2");

        // Let's assume the setup phase is done and ships are placed for both players
        // This could be done manually or with an auto-setup feature
        game.getPlayer1().autoPlaceShips();
        game.getPlayer2().autoPlaceShips();

        // Start the game
        game.startGame();

        // Simulate taking turns with the players shooting at each other's board
        // We'll just randomly shoot without any strategy for demonstration purposes
        while (!game.isGameOver()) {
            Player currentPlayer = game.getCurrentPlayer();

            // Randomly select a cell to shoot at
            int row = (int) (Math.random() * 10);
            int col = (int) (Math.random() * 10);

            // Player takes a shot
            boolean hit = game.takeTurn(row, col);
            System.out.println(currentPlayer.getName() + " shoots at (" + row + "," + col + ") and it's a " + (hit ? "hit!" : "miss!"));

            // Print the current player's board
            printBoard(currentPlayer.getBoard());

            // Check for a winner
            if (game.getGameState() == GameState.GAME_OVER) {
                Player winner = game.getWinner();
                System.out.println("Game Over! The winner is " + winner.getName());
                break;
            }
        }
    }

    // Helper method to print the board state to the console
    private static void printBoard(Board board) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Cell cell = board.getCell(row, col);
                if (cell.isHit() && cell.isOccupied()) {
                    System.out.print(" X "); // Hit on a ship
                } else if (cell.isHit()) {
                    System.out.print(" - "); // Missed shot
                } else if (cell.isOccupied()) {
                    System.out.print(" S "); // Ship
                } else {
                    System.out.print(" . "); // Water
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
