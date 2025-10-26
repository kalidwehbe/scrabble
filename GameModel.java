import java.util.*;

public class GameModel {
    private Board board;
    private List<Player> players;
    private Queue<Tile> bag;
    private int currentPlayerIndex;
    private List<GameObserver> observers;
    private Dictionary dictionary;

    public GameModel(List<String> names, String dictionaryFile) {
        board = new Board();
        players = new ArrayList<>();
        observers = new ArrayList<>();
        bag = createTileBag();
        dictionary = new Dictionary(dictionaryFile);

        for (String name : names) {
            Player p = new Player(name);
            p.drawTiles(bag, 7);
            players.add(p);
        }

        currentPlayerIndex = 0;
    }
