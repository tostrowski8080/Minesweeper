import java.util.*;

public class Game {
    public final Board board = new Board();

    private boolean playing;
    public boolean isPlaying(){
        return playing;
    }

    private boolean firstMove;

    private int bombCount;
    public int getBombCount(){
        return bombCount;
    }

    private int cellCount;
    public int getCellCount(){
        return cellCount; // ✅ fixed
    }

    private int flagCount;
    public int getFlagCount(){
        return flagCount; // ✅ fixed
    }

    private Main.ShapeOption boardShape;
    public void setShape(Main.ShapeOption shape){
        boardShape = shape;
    }
    public Main.ShapeOption getShape(){
        return boardShape;
    }

    private double bombRatio;
    public void setBombRatio(double Ratio){
        bombRatio = Ratio;
    }
    public double getBombRatio(){
        return bombRatio;
    }

    public void generateBoard(int cols, int rows, double ratio, Main.ShapeOption shape){
        if (ratio > 1 || ratio < 0) return;

        playing = true;
        firstMove = true;
        bombRatio = ratio;
        boardShape = shape;

        board.setRows(rows);
        board.setCols(cols);

        cellCount = 0;
        flagCount = 0;

        switch (shape){
            case RECTANGLE: generateRectangle(cols, rows); break;
            case CROSS: generateCross(cols, rows); break;
            case Z: generateZ(cols, rows); break;
            case CIRCLE: generateCircle(cols, rows); break;
            case RING: generateRing(cols, rows); break;
            default: System.out.println("Error during board generation.");
        }

        bombCount = (int)(cellCount * ratio);
    }

    public void generateRectangle(int cols, int rows){
        for (int col = 1; col <= cols; col++) {
            for (int row = 1; row <= rows; row++) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
        }
    }

    private void generateCross(int cols, int rows){
        for (int col = 1; col <= cols; col++) {
            for (int row = 1; row <= rows/4; row++) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
        }

        for (int col = cols/4 + 1; col <= cols * 3/4; col++) {
            for (int row = rows/4 + 1; row <= rows; row++) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
        }
    }

    private void generateZ(int cols, int rows){
        for (int col = 1; col <= cols; col++) {
            for (int row = 1; row <= rows/4; row++) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
        }

        int stateincr = (int)Math.round((double)cols/rows);
        int state = stateincr;

        for (int row = rows/4 + 1; row <= rows * 3/4; row++) {
            for (int col = cols - state; col >= cols * 3/4 - state; col--) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
            state += stateincr;
        }

        for (int col = 1; col <= cols; col++) {
            for (int row = rows * 3/4 + 1; row <= rows; row++) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
        }
    }

    private void generateCircle(int cols, int rows){
        int stateincr = (int)Math.round((double)cols/rows);
        int state = stateincr;

        for (int row = 1; row <= rows/2; row++) {
            for (int col = cols/2 + state; col >= cols/2 + 1 - state; col--) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
            state += stateincr;
        }

        state = stateincr;

        for (int row = rows/2 + 1; row <= rows; row++) {
            for (int col = cols + 1 - state; col >= state; col--) {
                board.setPoint(new Point(col, row), new Cell());
                cellCount++;
            }
            state += stateincr;
        }
    }

    private void generateRing(int cols, int rows) {
        int outerStateIncr = (int) Math.round((double) cols / rows);
        int halfRows = rows / 2;
        int thickness = outerStateIncr * 2;

        for (int row = 1; row <= halfRows; row++) {
            int state = row * outerStateIncr;

            int outerLeft = cols / 2 - state + 1;
            int outerRight = cols / 2 + state;

            int innerState = Math.max(state - thickness, 0);
            int innerLeft = cols / 2 - innerState + 1;
            int innerRight = cols / 2 + innerState;

            for (int col = outerLeft; col <= outerRight; col++) {
                if (col < innerLeft || col > innerRight) {
                    board.setPoint(new Point(col, row), new Cell());
                    cellCount++;
                }
            }
        }

        for (int row = halfRows + 1; row <= rows; row++) {
            int mirrorRow = rows - row + 1;
            int state = mirrorRow * outerStateIncr;

            int outerLeft = cols / 2 - state + 1;
            int outerRight = cols / 2 + state;

            int innerState = Math.max(state - thickness, 0);
            int innerLeft = cols / 2 - innerState + 1;
            int innerRight = cols / 2 + innerState;

            for (int col = outerLeft; col <= outerRight; col++) {
                if (col < innerLeft || col > innerRight) {
                    board.setPoint(new Point(col, row), new Cell());
                    cellCount++;
                }
            }
        }
    }

    public void setFlag(Point point){
        Cell targetCell = board.getCell(point);

        if (targetCell == null) {
            System.out.println("Invalid point!");
            return;
        }

        if (targetCell.isRevealed()){
            System.out.println("Cell is already revealed.");
            return;
        }

        if (!targetCell.isFlagged()) {
            targetCell.setFlagged(true);
            flagCount++;
        } else {
            targetCell.setFlagged(false);
            flagCount--;
        }

        System.out.println("Flags: " + flagCount + " / " + bombCount);
        board.drawBoard();
    }

    public void check(Point point, Boolean draw) {
        Cell targetCell = board.getCell(point);

        if (targetCell == null) {
            System.out.println("Invalid point!");
            return;
        }

        if (targetCell.isFlagged()) {
            System.out.println("Can't reveal a flagged cell. Remove flag first.");
            return;
        }

        if (firstMove){
            firstMove = false;
            board.randomizeBombs(bombCount, cellCount, point);
        }

        if (!targetCell.isRevealed()) {
            if (targetCell.isMine()) {
                System.out.println("Mine! Game over!");
                gameOver();
                return;
            }

            targetCell.setRevealed(true);

            if (targetCell.getAdjMines() == 0) {
                for (Point n : board.getPointNeighboursPoints(point)){
                    floodCheck(n);
                }
            }
        } else {
            int localFlagCount = 0;
            List<Point> neighbours = board.getPointNeighboursPoints(point);

            Iterator<Point> iterator = neighbours.iterator();
            while (iterator.hasNext()) {
                Point p = iterator.next();
                if (board.getCell(p).isFlagged()) {
                    localFlagCount++;
                    iterator.remove();
                }
            }

            if (localFlagCount == targetCell.getAdjMines()) {
                for (Point p : neighbours) {
                    if (!board.getCell(p).isRevealed()) {
                        check(p, false);
                        if (!isPlaying()) return;
                    }
                }
            } else {
                System.out.println("Point already revealed!");
            }
        }

        System.out.println("Flags: " + flagCount + " / " + bombCount);

        if (draw) board.drawBoard();

        if (board.checkWin()) {
            if (!draw) board.drawBoard();
            System.out.println("Congratulations! You won!");
            gameWon();
        }
    }

    public void floodCheck(Point point) {
        Cell cell = board.getCell(point);

        if (cell == null || cell.isRevealed() || cell.isMine() || cell.isFlagged()) return;

        cell.setRevealed(true);

        if (cell.getAdjMines() == 0) {
            for (Point n : board.getPointNeighboursPoints(point)){
                floodCheck(n);
            }
        }
    }

    public void stuck() {
        List<Point> candidates = new ArrayList<>();

        for (Point p : board.getAllPoints()) {
            Cell c = board.getCell(p);

            if (c != null && c.isRevealed()) {
                for (Point n : board.getPointNeighboursPoints(p)) {
                    Cell nc = board.getCell(n);

                    if (nc != null && nc.isMine() && !nc.isFlagged()) {
                        if (!candidates.contains(n)) {
                            candidates.add(n);
                        }
                    }
                }
            }
        }

        if (!candidates.isEmpty()) {
            Collections.shuffle(candidates);
            Point target = candidates.get(0);

            System.out.println("Flagged at " + target.col + " " + target.row);

            setFlag(target);
        } else {
            System.out.println("No bombs found");
        }
    }

    public void gameOver(){
        board.revealAll();
        board.drawBoard();
        playing = false;
    }

    public void gameWon(){
        playing = false;
    }

    public void resetGame(){
        board.clearBoard();
    }
}