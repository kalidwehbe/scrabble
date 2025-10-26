public class Square {
    private Tile tile;

    public boolean hasTile() {
        return tile != null;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        if (!hasTile()) {
            this.tile = tile;
        } else {
            throw new IllegalStateException("Square already has a tile!");
        }
    }

    @Override
    public String toString() {
        return hasTile() ? Character.toString(tile.getLetter()) : ".";
    }
}
