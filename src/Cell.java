public class Cell {

    private boolean mine;
    public void setMine(boolean mine) { this.mine = mine; }
    public boolean isMine() { return mine; }

    private boolean revealed;
    public void setRevealed(boolean revealed){ this.revealed = revealed; }
    public boolean isRevealed(){ return revealed; }

    private boolean flagged;
    public void setFlagged(boolean flag){ flagged = flag; }
    public boolean isFlagged(){ return flagged; }

    private int adjMines;
    public void setAdjMines(int mines){ adjMines = mines; }
    public int getAdjMines(){ return adjMines; }

    public Cell(){
        mine = false;
        revealed = false;
        flagged = false;
        adjMines = 0;
    }
}