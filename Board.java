import java.io.File;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Board {
    private String name;
    private static final int SIZE = 15;
    private final Square[][] grid;
    private Map<String, List<int[]>> bonuses;
    private String boardFile;

    public Board(String boardFile) {
        this.boardFile = boardFile;
        grid = new Square[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = new Square();
        bonuses = new HashMap<>();
        loadBoardFromXML(boardFile);
        applyBonuses();
    }

    // Load board and bonuses from XML file
    private void loadBoardFromXML(String fileName) {
        try {
            File file = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Element boardElement = doc.getDocumentElement();
            name = boardElement.getAttribute("name");

            NodeList bonusList = boardElement.getElementsByTagName("bonus");
            for (int i = 0; i < bonusList.getLength(); i++) {
                Element bonus = (Element) bonusList.item(i);
                String type = bonus.getAttribute("type");
                int row = Integer.parseInt(bonus.getAttribute("row"));
                int col = Integer.parseInt(bonus.getAttribute("col"));
                bonuses.computeIfAbsent(type, k -> new ArrayList<>()).add(new int[]{row, col});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyBonuses() {
        for (Map.Entry<String, List<int[]>> entry : bonuses.entrySet()) {
            String type = entry.getKey();
            for (int[] pos : entry.getValue()) {
                int row = pos[0], col = pos[1];
                grid[row][col].setBonus(Square.Bonus.valueOf(type));
            }
        }
    }

    public boolean squareHasTile(int row, int col) {
        return inBounds(row, col) && grid[row][col].hasTile();
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean canPlaceWordWithRack(String word, int row, int col, boolean horizontal, Player p) {
        List<Character> rackCopy = new ArrayList<>();
        for (Tile t : p.getRack()) rackCopy.add(t.isBlank() ? '*' : t.getLetter());

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (!inBounds(r, c)) return false;

            char letter = Character.toUpperCase(word.charAt(i));
            if (!grid[r][c].hasTile()) {
                if (!rackCopy.remove((Character) letter) && !rackCopy.remove((Character) '*'))
                    return false;
            } else if (grid[r][c].getTile().getLetter() != letter) return false;
        }
        return true;
    }

    public boolean placeWord(String word, int row, int col, boolean horizontal, Player p) {
        if (!canPlaceWordWithRack(word, row, col, horizontal, p)) return false;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            char letter = Character.toUpperCase(word.charAt(i));

            if (!grid[r][c].hasTile()) {
                Tile removed = p.takeTileForLetter(letter);
                if (removed == null) return false;
                if (removed.isBlank()) grid[r][c].setTile(Tile.placedBlank(letter));
                else grid[r][c].setTile(removed);
            }
        }
        return true;
    }

    public Square getSquare(int row, int col) {
        if (!inBounds(row, col)) throw new IndexOutOfBoundsException();
        return grid[row][col];
    }

    public char getLetterAt(int row, int col) {
        return inBounds(row, col) && grid[row][col].hasTile()
                ? grid[row][col].getTile().getLetter() : ' ';
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (char c = 'A'; c <= 'O'; c++) sb.append(c).append(" ");
        sb.append("\n");

        for (int i = 0; i < SIZE; i++) {
            if (i + 1 < 10) sb.append(" ");
            sb.append(i + 1).append(" ");
            for (int j = 0; j < SIZE; j++)
                sb.append(grid[i][j].toString()).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

    public char getTileLetter(int row, int col) {
        if (grid[row][col] != null) {
            return grid[row][col].getLetter();
        }
        return ' '; // or any placeholder for empty
    }

    public Board copy() {
        Board b = new Board(this.boardFile);

        // deep copy squares
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                b.grid[r][c] = this.grid[r][c].copy();
            }
        }

        // deep copy bonuses map
        b.bonuses = new HashMap<>();
        for (String key : this.bonuses.keySet()) {
            List<int[]> newList = new ArrayList<>();
            for (int[] pos : this.bonuses.get(key)) {
                newList.add(new int[]{pos[0], pos[1]});
            }
            b.bonuses.put(key, newList);
        }

        return b;
    }

