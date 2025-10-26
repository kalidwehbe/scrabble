import java.util.List;

public class ConsoleView implements GameObserver {

    @Override
    public void update(Board board, List<Player> players) {
        board.display();
        System.out.println("\nScores:");
        for (Player p : players) {
            System.out.println(p.getName() + ": " + p.getScore());
            p.showRack();
        }
        System.out.println();
    }
}
