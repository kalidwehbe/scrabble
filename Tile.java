public class Tile {
    private char letter;
    private int score;
    private boolean blank; // true for blank tiles

    // Regular tile constructor
    public Tile(char letter, int score) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
        this.blank = false;
    }

    // Internal constructor used for blank creation
    private Tile(char letter, int score, boolean blank) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
        this.blank = blank;
    }

    // Create an unassigned blank tile (in the bag / rack)
    public static Tile blankTile() {
        return new Tile('*', 0, true); // '*' used internally to represent an unused blank
    }

    // Create a tile to place on board that represents a blank set to chosenLetter
    // Display letter = chosenLetter, score remains 0, isBlank = true
    public static Tile placedBlank(char chosenLetter) {
        return new Tile(chosenLetter, 0, true);
    }

    public char getLetter() { return letter; }
    public void setScore(int point) {
        score = point;
    }
    public boolean isBlank() { return blank; }

    @Override
    public String toString() {
        return Character.toString(letter);
    }
}

