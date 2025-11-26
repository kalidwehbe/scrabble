import java.io.Serializable;
import java.util.*;

/**
 * Represents a snapshot of the game state for Undo/Redo.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    public Board board;
    public List<Player> players;
    public Queue<Tile> bag;
    public int currentPlayerIndex;
    public boolean firstMove;

    public GameState(Board board, List<Player> players, Queue<Tile> bag, int currentPlayerIndex, boolean firstMove) {
        this.board = board;
        this.players = players;
        this.bag = bag;
        this.currentPlayerIndex = currentPlayerIndex;
        this.firstMove = firstMove;
    }
}
