import java.util.Objects;

public class Point {
    public final int col;
    public final int row;

    public Point(int col, int row){
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Point)) return false;
        Point other = (Point) o;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}