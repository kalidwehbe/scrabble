import java.util.*;

public class Player {
    private String name;
    private List<Tile> rack;
    private int score;
    private String lastError = "";

    public Player(String name) { this.name = name; rack = new ArrayList<>(); score = 0; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public List<Tile> getRack() { return rack; }
    public String getLastError() { return lastError; }
    public void setLastError(String msg) { lastError = msg; }
    public void addScore(int pts) { score += pts; }

    public void drawTiles(Queue<Tile> bag, int count) {
        while (rack.size() < 7 && !bag.isEmpty() && count > 0) { rack.add(bag.poll()); count--; }
    }

    public boolean hasTilesForWord(String word) {
        List<Character> copy = new ArrayList<>();
        for (Tile t : rack) copy.add(t.getLetter());
        for (char c : word.toCharArray()) if (!copy.remove((Character)c)) return false;
        return true;
    }

    public void useTilesForWord(String word) {
        for (char c : word.toCharArray())
            for (int i = 0; i < rack.size(); i++)
                if (rack.get(i).getLetter() == c) { rack.remove(i); break; }
    }

    public boolean swapTiles(String letters, Queue<Tile> bag) {
        if (!hasTilesForWord(letters)) return false;
        for (char c : letters.toCharArray())
            for (int i = 0; i < rack.size(); i++)
                if (rack.get(i).getLetter() == c) { bag.offer(rack.remove(i)); break; }
        drawTiles(bag, letters.length());
        return true;
    }
}
