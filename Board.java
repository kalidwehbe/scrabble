import java.util.*;

public class Board {
    private static final int SIZE = 15;
    private final Square[][] grid;

    public Board() {
        grid = new Square[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = new Square();

        setupPremiumSquares();
    }

    private void setupPremiumSquares() {
        // Triple Word (TW) positions
        int[] tw = {0, 14};
        for (int r : tw)
            for (int c : tw)
                grid[r][c].setBonus(Square.Bonus.TW);
        grid[7][0].setBonus(Square.Bonus.TW);
        grid[0][7].setBonus(Square.Bonus.TW);
        grid[7][14].setBonus(Square.Bonus.TW);
        grid[14][7].setBonus(Square.Bonus.TW);


        // Double Word (DW) positions (excluding center and TW)
        int[][] dw = {{1,1},{2,2},{3,3},{4,4},{13,1},{12,2},{11,3},{10,4},
                {1,13},{2,12},{3,11},{4,10},{13,13},{12,12},{11,11},{10,10}};
        for (int[] pos : dw) grid[pos[0]][pos[1]].setBonus(Square.Bonus.DW);

        // Double Letter (DL) positions
        int[][] dl = {{0,3},{0,11},{2,6},{2,8},{3,0},{3,7},{3,14},{6,2},{6,6},{6,8},{6,12},
                {7,3},{7,11},{8,2},{8,6},{8,8},{8,12},{11,0},{11,7},{11,14},{12,6},{12,8},
                {14,3},{14,11}};
        for (int[] pos : dl) grid[pos[0]][pos[1]].setBonus(Square.Bonus.DL);

        // Triple Letter (TL) positions
        int[][] tl = {{1,5},{1,9},{5,1},{5,5},{5,9},{5,13},{9,1},{9,5},{9,9},{9,13},{13,5},{13,9}};
        for (int[] pos : tl) grid[pos[0]][pos[1]].setBonus(Square.Bonus.TL);
    }

    public boolean squareHasTile(int row, int col) {
        return inBounds(row, col) && grid[row][col].hasTile();
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean canPlaceWordWithRack(String word, int row, int col, boolean horizontal, Player p) {
        List<Character> rackCopy = new ArrayList<>();
        for (Tile t : p.getRack()) rackCopy.add(t.isBlank() ? '*' : t.getLetter());

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!inBounds(r, c)) return false;

            char letter = Character.toUpperCase(word.charAt(i));
            if (!grid[r][c].hasTile()) {
                if (!rackCopy.remove((Character) letter) && !rackCopy.remove((Character) '*'))
                    return false;
            } else if (grid[r][c].getTile().getLetter() != letter) return false;
        }
        return true;
    }

    public boolean placeWord(String word, int row, int col, boolean horizontal, Player p) {
        if (!canPlaceWordWithRack(word, row, col, horizontal, p)) return false;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            char letter = Character.toUpperCase(word.charAt(i));

            if (!grid[r][c].hasTile()) {
                Tile removed = p.takeTileForLetter(letter);
                if (removed == null) return false;
                if (removed.isBlank()) grid[r][c].setTile(Tile.placedBlank(letter));
                else grid[r][c].setTile(removed);
            }
        }
        return true;
    }

    public Square getSquare(int row, int col) {
        if (!inBounds(row, col)) throw new IndexOutOfBoundsException();
        return grid[row][col];
    }

    public char getLetterAt(int row, int col) {
        return inBounds(row, col) && grid[row][col].hasTile()
                ? grid[row][col].getTile().getLetter() : ' ';
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (char c = 'A'; c <= 'O'; c++) sb.append(c).append(" ");
        sb.append("\n");

        for (int i = 0; i < SIZE; i++) {
            if (i + 1 < 10) sb.append(" ");
            sb.append(i + 1).append(" ");
            for (int j = 0; j < SIZE; j++)
                sb.append(grid[i][j].toString()).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

    public char getTileLetter(int row, int col) {
        if (grid[row][col] != null) {
            return grid[row][col].getLetter();
        }
        return ' '; // or any placeholder for empty
    }
}
