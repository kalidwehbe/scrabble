import java.util.*;

/**
 * Represents a 15x15 Scrabble board consisting of individual squares.
 * Handles placement and validation of words using player racks.
 */
public class Board {
    private static final int SIZE = 15;
    private final Square[][] grid;

    /** Initializes the board with empty squares. */
    public Board() {
        grid = new Square[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Square();
            }
        }
    }

    /** Returns true if the given position already has a tile. */
    public boolean squareHasTile(int row, int col) {
        return inBounds(row, col) && grid[row][col].hasTile();
    }

    /** Checks if the coordinates are within the 15x15 board limits. */
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Verifies if a word can be placed starting at (row, col),
     * horizontally or vertically, using tiles from the player’s rack.
     */
    public boolean canPlaceWordWithRack(String word, int row, int col, boolean horizontal, Player p) {
        List<Character> rackCopy = new ArrayList<>();
        for (Tile t : p.getRack()) rackCopy.add(t.getLetter());

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);

            if (!inBounds(r, c)) return false; // Word goes off the board
            char letter = word.charAt(i);

            if (!grid[r][c].hasTile()) {
                // Letter must be available in player's rack
                if (!rackCopy.remove((Character) letter)) return false;
            } else if (grid[r][c].getTile().getLetter() != letter) {
                // Conflicts with an existing letter
                return false;
            }
        }
        return true;
    }

    /**
     * Places a valid word on the board.
     * Returns false if the placement is invalid or out of bounds.
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal, Player p) {
        if (!canPlaceWordWithRack(word, row, col, horizontal, p)) return false;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!grid[r][c].hasTile()) {
                grid[r][c].setTile(new Tile(word.charAt(i), 1));
            }
        }
        return true;
    }

    /** Returns the letter at the given board position, or a space if empty. */
    public char getLetterAt(int row, int col) {
        return inBounds(row, col) && grid[row][col].hasTile()
                ? grid[row][col].getTile().getLetter()
                : ' ';
    }

    /** Renders the board as a human-readable 15x15 grid with coordinates. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Column headers (A–O)
        sb.append("   ");
        for (char c = 'A'; c <= 'O'; c++) sb.append(c).append(" ");
        sb.append("\n");

        // Board rows with row numbers
        for (int i = 0; i < SIZE; i++) {
            if (i + 1 < 10) sb.append(" "); // Align single-digit numbers
            sb.append(i + 1).append(" ");

            for (int j = 0; j < SIZE; j++) {
                sb.append(grid[i][j].hasTile() ? grid[i][j].getTile().getLetter() : '.').append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
