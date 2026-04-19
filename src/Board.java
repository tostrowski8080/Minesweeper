import java.util.*;

public class Board {
    private int rows;
    public void setRows(int row){ rows = row; }
    public int getRows(){ return rows; }

    private int cols;
    public void setCols(int col){ cols = col; }
    public int getCols(){ return cols; }

    private final Map<Point, Cell> board = new HashMap<>();

    public void setPoint(Point point, Cell cell){
        board.put(point, cell);
    }

    public Cell getCell(Point point){
        return board.get(point);
    }

    public Set<Point> getAllPoints() {
        return board.keySet();
    }

    public void drawBoard(){
        if (cols == 0 || rows == 0) return;

        System.out.print("   ");
        for (int i=1; i<=cols; i++) {
            System.out.printf("%-2d ", i);
        }
        System.out.println();

        for (int i=1; i<=rows; i++){
            System.out.printf("%-2d ", i);
            for (int j=1; j<=cols; j++){
                Cell cell = getCell(new Point(j, i));
                if (cell == null) {
                    System.out.print("   ");
                    continue;
                }

                if (cell.isRevealed()) {
                    if (!cell.isMine()) {
                        if (cell.getAdjMines() != 0)
                            System.out.printf("%-2d ", cell.getAdjMines());
                        else
                            System.out.print(".  ");
                    } else {
                        System.out.print("* ");
                    }
                } else {
                    if (cell.isFlagged()) System.out.print("F  ");
                    else System.out.print("#  ");
                }
            }
            System.out.println();
        }
    }

    public void clearBoard(){
        rows = 0;
        cols = 0;
        board.clear();
    }

    public void randomizeBombs(int bombCount, int cellCount, Point startingPoint) {
        List<Point> points = new ArrayList<>(board.keySet());
        Collections.shuffle(points);

        points.remove(startingPoint);
        points.removeAll(getPointNeighboursPoints(startingPoint));

        int actualBombCount = Math.min(bombCount, points.size());

        for (int i = 0; i < actualBombCount; i++) {
            board.get(points.get(i)).setMine(true);
        }

        for (Point point : board.keySet()) {
            Cell cell = board.get(point);
            if (cell.isMine()) continue;

            int count = 0;
            for (Cell n : getPointNeighbours(point)) {
                if (n.isMine()) count++;
            }
            cell.setAdjMines(count);
        }
    }

    public List<Cell> getPointNeighbours(Point point) {
        List<Cell> neighbours = new ArrayList<>();
        int[] deltas = {-1, 0, 1};

        for (int dx : deltas) {
            for (int dy : deltas) {
                if (dx == 0 && dy == 0) continue;
                Cell c = board.get(new Point(point.col + dx, point.row + dy));
                if (c != null) neighbours.add(c);
            }
        }
        return neighbours;
    }

    public List<Point> getPointNeighboursPoints(Point point){
        List<Point> neighbours = new ArrayList<>();
        int[] d = {-1, 0, 1};

        for (int dr : d) {
            for (int dc : d) {
                if (dr == 0 && dc == 0) continue;
                Point p = new Point(point.col + dr, point.row + dc);
                if (board.containsKey(p)) neighbours.add(p);
            }
        }
        return neighbours;
    }

    public void revealAll(){
        for (Cell c : board.values()){
            c.setRevealed(true);
        }
    }

    public boolean checkWin() {
        for (Cell c : board.values()) {
            if (c.isMine() && c.isRevealed()) return false;
            if (!c.isMine() && !c.isRevealed()) return false;
        }
        return true;
    }
}