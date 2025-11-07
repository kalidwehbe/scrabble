import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask for number of players
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            System.out.print("Enter number of players (2-4): ");
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                numPlayers = 0; // Invalid input
            }
        }

        // Get player names
        List<String> names = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Player" + i;
            names.add(name);
        }

        // Create the model with dictionary file
        GameModel model = new GameModel(names, "dictionary.txt");

        // Create the GUI view
        GameViewGUI view = new GameViewGUI();

        // Add the observer
        model.addObserver(view);

        // Create the controller
        GameController controller = new GameController(model, view);

        // Start the game
        model.start();
    }
}
