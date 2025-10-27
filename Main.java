import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numPlayers = 0;

        while (numPlayers < 2 || numPlayers > 4) {
            System.out.print("Enter number of players (2-4): ");
            numPlayers = scanner.nextInt();
            scanner.nextLine(); // consume newline
        }

        List<String> names = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Player" + i;
            names.add(name);
        }

        // Pass the dictionary file path
        String dictionaryFile = "dictionary.txt";
        GameModel model = new GameModel(names, dictionaryFile);

        ConsoleView view = new ConsoleView();
        model.addObserver(view);

        GameController controller = new GameController(model);
        controller.play();
    }
}
