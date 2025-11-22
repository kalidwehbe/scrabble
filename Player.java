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
        for (Tile t : rack) copy.add(t.isBlank() ? '*' : t.getLetter());

        for (char c : word.toUpperCase().toCharArray()) {
            if (!copy.remove((Character)c)) {
                // if specific letter not found, try to use a blank ('*')
                if (!copy.remove((Character)'*')) return false;
            }
        }
        return true;
    }

    public void useTilesForWord(String word) {
        for (char ch : word.toUpperCase().toCharArray()) {
            boolean removed = false;
            for (int i = 0; i < rack.size(); i++) {
                Tile t = rack.get(i);
                if (!t.isBlank() && t.getLetter() == ch) {
                    rack.remove(i);
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                // remove a blank if available
                for (int i = 0; i < rack.size(); i++) {
                    Tile t = rack.get(i);
                    if (t.isBlank()) {
                        rack.remove(i);
                        removed = true;
                        break;
                    }
                }
            }
        }
    }

    public Tile takeTileForLetter(char letter) {
        letter = Character.toUpperCase(letter);
        // prefer exact match
        for (int i = 0; i < rack.size(); i++) {
            Tile t = rack.get(i);
            if (!t.isBlank() && t.getLetter() == letter) {
                return rack.remove(i);
            }
        }
        // otherwise use a blank
        for (int i = 0; i < rack.size(); i++) {
            Tile t = rack.get(i);
            if (t.isBlank()) {
                return rack.remove(i); // returns an unassigned blank (letter = '*')
            }
        }
        return null; // no tile found
    }



    public boolean swapTiles(String letters, Queue<Tile> bag) {
        if (!hasTilesForWord(letters)) return false;
        for (char c : letters.toCharArray())
            for (int i = 0; i < rack.size(); i++)
                if (rack.get(i).getLetter() == c) { bag.offer(rack.remove(i)); break; }
        drawTiles(bag, letters.length());
        return true;
    }

    public void useBlankTile(char letter) {
        for (int i = 0; i < rack.size(); i++) {
            Tile t = rack.get(i);
            if (t.isBlank()) {
                rack.remove(i);
                break;
            }
        }
    }

}
