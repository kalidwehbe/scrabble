/**
 * Represents a single Scrabble tile with a letter and point value.
 * Each tile can be placed on the board and contributes to a word's score.
 */
public class Tile {
    private final char letter;
    private final int score;

    /**
     * Constructs a new Tile with the specified letter and score.
     * @param letter The character displayed on this tile
     * @param score The point value of this tile
     */
    public Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }

    /**
     * Returns the letter on this tile.
     * @return The character representing this tile's letter
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Returns the point value of this tile.
     * @return The score value for this tile
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns a string representation of this tile.
     * @return The letter as a String
     */
    @Override
    public String toString() {
        return Character.toString(letter);
    }
}
