package gui;

import javax.swing.*;
import core.Cell;
// other imports as necessary

public class CellButton extends JButton {
    private final Cell cell;

    // Icons for different cell states
    private static final Icon hitShipIcon = new ImageIcon("path/to/hitShipIcon.png");
    private static final Icon missIcon = new ImageIcon("path/to/missIcon.png");
    private static final Icon shipIcon = new ImageIcon("path/to/shipIcon.png");
    private static final Icon waterIcon = new ImageIcon("path/to/waterIcon.png");

    public CellButton(Cell cell) {
        this.cell = cell;
        // Set the default icon or visual state
        this.setIcon(waterIcon);
    }

    public void updateVisual() {
        if (cell.isHit()) {
            if (cell.isOccupied()) {
                // Change the button to show a hit ship part
                this.setIcon(hitShipIcon);
            } else {
                // Change the button to show a miss
                this.setIcon(missIcon);
            }
        } else {
            // No shot has been taken here yet
            if (cell.isOccupied()) {
                // Optional: If you want to show where the ships are (for debugging or if it's the player's own board)
                this.setIcon(shipIcon);
            } else {
                // Change the button to show water/empty cell
                this.setIcon(waterIcon);
            }
        }
    }
    // Other methods as necessary...
}
