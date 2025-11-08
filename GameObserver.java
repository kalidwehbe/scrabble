import java.util.List;

public interface GameObserver {
    void update(Board board, List<Player> players, Player player);
}
