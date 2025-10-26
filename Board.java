import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final int SIZE = 15;
    private Square[][] grid;

    public Board() {
        grid = new Square[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = new Square();
    }

    // Get letter at position (0 if empty)
    public char getLetterAt(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return 0;
        if (grid[row][col].hasTile()) return grid[row][col].getTile().getLetter();
        return 0;
    }

    // Check if square currently has a tile
    public boolean squareHasTile(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;
        return grid[row][col].hasTile();
    }

    private boolean isBoardEmpty() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (grid[i][j].hasTile()) return false;
        return true;
    }

    // Check if player can place word with their rack
    public boolean canPlaceWordWithRack(String word, int row, int col, boolean horizontal, Player player) {
        List<Character> rackCopy = new ArrayList<>();
        for (Tile t : player.getRack()) rackCopy.add(t.getLetter());

        boolean connected = false;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= SIZE || c >= SIZE) return false;

            char boardChar = getLetterAt(r, c);

            if (boardChar == word.charAt(i)) {
                connected = true; // using existing tile
            } else {
                if (!rackCopy.remove((Character) word.charAt(i))) return false;
            }
        }

        // Must connect to existing tile unless board is empty
        if (!connected && !isBoardEmpty()) return false;

        return true;
    }

    // Place the word on the board
    public boolean placeWord(String word, int row, int col, boolean horizontal) {
        if (row < 0 || col < 0 || row >= SIZE || col >= SIZE) return false;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            Square sq = grid[r][c];
            if (!sq.hasTile()) {
                sq.setTile(new Tile(word.charAt(i), 1)); // simplified scoring
            }
        }
        return true;
    }

    public void display() {
        System.out.println("\n=== Board ===");

        // Column letters (A–O)
        System.out.print("   ");
        for (int j = 0; j < SIZE; j++)
            System.out.print((char) ('A' + j) + " ");
        System.out.println();

        for (int i = 0; i < SIZE; i++) {
            System.out.printf("%2d ", i + 1); // Row numbers 1–15
            for (int j = 0; j < SIZE; j++)
                System.out.print(grid[i][j].toString() + " ");
            System.out.println();
        }
    }



}
