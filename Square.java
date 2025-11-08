/**
 * Represents a single square on the Scrabble board.
 * A square can hold at most one tile. Once a tile is placed, it cannot be replaced.
 */
public class Square {
    private Tile tile;

    /**
     * Checks if this square currently has a tile placed on it.
     * @return true if a tile is present, false otherwise
     */
    public boolean hasTile() {
        return tile != null;
    }

    /**
     * Returns the tile currently on this square.
     * @return The Tile object on this square, or null if empty
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Places a tile on this square.
     * A tile can only be placed on an empty square.
     * @param tile The Tile to place on this square
     * @throws IllegalStateException if the square already has a tile
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
     * @return The tile's letter if occupied, or "." if empty
     */
    @Override
    public String toString() {
        return hasTile() ? Character.toString(tile.getLetter()) : ".";
    }
}
