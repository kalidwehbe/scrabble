import java.util.*;

public class Player {
    private String name;
    private List<Tile> rack;
    private int score;

    public Player(String name) {
        this.name = name;
        this.rack = new ArrayList<>();
        this.score = 0;
    }
    public String getName() { return name; }
    public int getScore() { return score; }
    public List<Tile> getRack() { return rack; }

    public void addScore(int points) { score += points; }

    public void drawTiles(Queue<Tile> bag, int count) {
        while (rack.size() < 7 && !bag.isEmpty() && count > 0) {
            rack.add(bag.poll());
            count--;
        }
    }

    public boolean hasTilesForWord(String word) {
        List<Character> copy = new ArrayList<>();
        for (Tile t : rack) copy.add(t.getLetter());
        for (char c : word.toCharArray()) {
            if (!copy.remove((Character)c)) return false;
        }
        return true;
    }

    public void useTilesForWord(String word) {
        for (char c : word.toCharArray()) {
            for (int i = 0; i < rack.size(); i++) {
                if (rack.get(i).getLetter() == c) {
                    rack.remove(i);
                    break;
                }
            }
        }
    }

    public void showRack() {
        System.out.print(name + "'s Tiles: ");
        for (Tile t : rack) System.out.print(t + " ");
        System.out.println();
    }
}
