import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask for number of players (2-4)
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            System.out.print("Enter number of players (2-4): ");
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                numPlayers = 0;
            }
        }

        List<Player> players = new ArrayList<>();

        // Get player names and type (Human or AI)
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Player" + i;

            String aiChoice = "";
            while (!aiChoice.equalsIgnoreCase("Y") && !aiChoice.equalsIgnoreCase("N")) {
                System.out.print("Is this player an AI? (Y/N): ");
                aiChoice = scanner.nextLine().trim();
            }


            if (aiChoice.equalsIgnoreCase("Y")) {
                players.add(new AIPlayer(name));
            } else {
                players.add(new Player(name));
            }

        }

        // 1. Create model without players first
        GameModel model = new GameModel(new ArrayList<>(), "dictionary.txt");

        // 2. Add the actual Player and AIPlayer instances into the model
        for (Player p : players) {
            if (p instanceof AIPlayer) {
                ((AIPlayer) p).drawTiles(model.getBag(), 7); // fill rack
            } else {
                p.drawTiles(model.getBag(), 7);
            }
            model.addPlayer(p);
        }

        // 3. Create view and controller
        GameViewGUI view = new GameViewGUI();
        model.addObserver(view);
        GameController controller = new GameController(model, view);

        // 4. Start the game
        model.start();

    }

    // Helper method to get names of players for GameModel constructor
    private static List<String> getPlayerNames(List<Player> players) {
        List<String> names = new ArrayList<>();
        for (Player p : players) names.add(p.getName());
        return names;
    }
}
