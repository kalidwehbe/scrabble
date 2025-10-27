/**
 * The Square class represents a single cell on the Scrabble board.
 * Each square can hold at most one Tile.
 *
 * Squares are responsible for storing and displaying their tile,
 * as well as checking whether they are occupied.
 */
public class Square {
    private Tile tile;
    /**
     * Checks if this square currently contains a tile.
     *
     * @return true if the square has a tile; false otherwise
     */
    public boolean hasTile() {
        return tile != null;
    }
    /**
     * Returns the tile currently placed on this square.
     *
     * @return the Tile on this square, or null if empty
     */
    public Tile getTile() {
        return tile;
    }
    /**
     * Places a tile on this square if it is empty.
     * 
     * @param tile the Tile to place on this square
     * @throws IllegalStateException if the square already contains a tile
     */
    public void setTile(Tile tile) {
        if (!hasTile()) {
            this.tile = tile;
        } else {
            throw new IllegalStateException("Square already has a tile!");
        }
    }
    /**
     * Returns a string representation of this square.
     * - If it has a tile, it shows the tile’s letter.
     * - If it’s empty, it shows a dot '.' to represent a blank space.
     *
     * @return the tile letter as a String or "." if the square is empty
     */
    @Override
    public String toString() {
        return hasTile() ? Character.toString(tile.getLetter()) : ".";
    }
}
