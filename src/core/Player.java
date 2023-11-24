package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private final String name;
    private final Board board;
    private final List<Ship> ships;

    //keeping track of the shots for potential use in our GUI
    private final List<Position> shotsTaken;

    public Player(String name){
        this.name = name;
        this.board = new Board();
        this.ships = new ArrayList<>();
        this.shotsTaken = new ArrayList<>();
        initializeShips();
    }

    private void initializeShips() {
        for(ShipType type : ShipType.values()){
            ships.add(new Ship(type));  //create a new ship of the given type
        }
    }

    //attempt to place a ship at a specified location
    public boolean placeShip(Ship ship, int row, int col, boolean isHorizontal) {
        boolean success = board.placeShip(ship, row, col, isHorizontal);
        if(success) {
            //TODO: check if the ship can be placed at the given location
            // if the ship was successfully placed, save its position (this is a simplification)
            ship.setPosition(new Position(row, col), isHorizontal); // Set the position of the ship
        }
        return success;
    }

    //record a shot made by this player
    public boolean takeShot(int row, int col){
        boolean hit = board.takeShot(row, col);  //record if the shot hit a ship

        shotsTaken.add(new Position(row, col));  //record the shot
        return hit;
    }

    //check if this player has lost the game
    public boolean hasLost() {
        return board.areAllShipsSunk();
    }

    //randomly place all the ships on the board for this player
    public void autoPlaceShips(){
        Random random = new Random(); //random number generator

        for(Ship ship : ships){
            boolean placed = false;
            while(!placed){
                int row = random.nextInt(board.getSize()); //random row
                int col = random.nextInt(board.getSize()); //random column

                //TODO: decide if the ship should be placed horizontally or vertically
                boolean isHorizontal = random.nextBoolean();

                placed = placeShip(ship, row, col, isHorizontal); //try to place the ship
            }
        }
    }

    //getters
    public String getName() {
        return name;
    }

    public Board getBoard() {
        return board;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public List<Position> getShotsTaken() {
        return shotsTaken;
    }

    //TODO: add other methods if necessary

}
