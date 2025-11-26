import java.io.Serializable;

public class Square implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Bonus { NONE, DL, TL, DW, TW }

    private Tile tile;
    private Bonus bonus;

    public Square() {
        this.tile = null;
        this.bonus = Bonus.NONE; // default square
    }

    public boolean hasTile() { return tile != null; }

    public Tile getTile() { return tile; }

    public void setTile(Tile tile) {
        if (!hasTile()) this.tile = tile;
        else throw new IllegalStateException("Square already has a tile!");
    }

    public Bonus getBonus() { return bonus; }
    public void setBonus(Bonus bonus) { this.bonus = bonus; }
    public char getLetter() { return getTile().getLetter();
    }
    public Square copy() {
        Square s = new Square();
        s.setBonus(this.getBonus());

        if (this.hasTile()) {
            s.setTile(this.getTile().copy());
        }

        return s;
    }

    @Override
    public String toString() {
        if (hasTile()) return Character.toString(tile.getLetter());
        switch (bonus) {
            case DL: return "2"; // double letter
            case TL: return "3"; // triple letter
            case DW: return "d"; // double word
            case TW: return "t"; // triple word
            default: return ".";
        }
    }
}
