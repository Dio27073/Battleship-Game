package core;

public class Board {
    private final int size = 10; // 10x10 board (Battleship standard)
    private Cell[][] cells;      // array of cells

    public Board() {
        cells = new Cell[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = new Cell(); // initialize each cell
            }
        }
    }

    public boolean placeShip(Ship ship, int row, int column, boolean isHorizontal) {

        //input validation
        if(!isPositionValid(row, column, isHorizontal, ship.getSize())){
            return false;
        }

        //new Position object for the starting position of the ship
        Position startingPosition = new Position(row, column);

        //place the ship on the board
        for(int i = 0; i < ship.getSize(); i++){
            cells[row][column].setOccupied(true);
            cells[row][column].setShip(ship);

            if(isHorizontal){
                column++;
            }else{
                row++;
            }
        }

        //update ship position
        ship.setPosition(startingPosition, isHorizontal);

        return true;
    }

    public boolean takeShot(int row, int column) {
        //input validation
        if (row < 0 || column < 0 || row >= size || column >= size) {
            return false;   //shot is outside the board
        }

        Cell targetCell = cells[row][column];  //get cell at the given position

        if(targetCell.isHit()) {
            return false;   //cell has already been shot at
        }

        targetCell.setHit(true);  //mark cell as hit

        //check if the shot hit a ship
        if(targetCell.isOccupied()){
            Ship ship = targetCell.getShip();  //get the ship occupying the cell
            int hitSegment = calculateHitSegment(ship, row, column);

            if(hitSegment != -1){
                ship.hit(hitSegment);  //hit the ship
                return true;
            }
        }
        return false; //shot missed
    }

    private int calculateHitSegment(Ship ship, int row, int column){
        Position shipPosition = ship.getPosition(); //get the position of the ship
        boolean isHorizontal = ship.isHorizontal(); //get the orientation of the ship

        //calculate the segment of the ship that was hit
        int segment = isHorizontal ? column - shipPosition.getColumn() : row - shipPosition.getRow();

        if(segment >= 0 && segment < ship.getSize()){
            return segment;   //return the segment that was hit if valid
        }

        return -1;  //invalid hit
    }

    private boolean isPositionValid(int row, int column, boolean isHorizontal, int shipSize) {
        if(isHorizontal){
            if(column + shipSize > size)
                return false;  //ship goes out of bounds

            for(int i = 0; i < shipSize; i++){
                if(cells[row][column + i].isOccupied())
                    return false;  //cell is occupied
            }

        }else{
            if(row + shipSize > size)
                return false; //ship goes out of bounds
            for(int i = 0; i < shipSize; i++){
                if (cells[row + i][column].isOccupied())
                    return false; //cell occupied
            }
        }

        return true;  //position is valid
    }

    public boolean areAllShipsSunk(){
        for(Cell[] row : cells){
            for(Cell cell : row){
                Ship ship = cell.getShip();

                if(ship != null && !ship.isSunk()) {
                    return false; //found a ship that's not sunk
                }

            }
        }
        return true; //all ships are sunk
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(int row, int column) {
        return cells[row][column];
    }
}
