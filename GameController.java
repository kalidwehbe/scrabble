import java.util.Scanner;

public class GameController {
    private GameModel model;
    private Scanner scanner;

    public GameController(GameModel model) {
        this.scanner = new Scanner(System.in);
        this.model = model;
    }

    private int colLetterToIndex(String col) {
        col = col.toUpperCase();
        return col.charAt(0) - 65;
    }

    public void play() {
        this.model.start();

        while(true) {
            Player current = this.model.getCurrentPlayer();
            System.out.println(current.getName() + ", enter command:");
            System.out.println("PLACE word row ColumnLetter Direction (e.g., PLACE HELLO 8 H H)");
            System.out.println("PASS (skip turn) or EXIT (quit)");
            String input = this.scanner.nextLine().trim().toUpperCase();
            if (input.equals("EXIT")) {
                System.out.println("Game ended.");
                return;
            }

            String[] parts = input.split("\\s+");
            if (parts[0].equals("PASS")) {
                this.model.passTurn();
            } else if (parts[0].equals("PLACE") && parts.length == 5) {
                String word = parts[1];

                int row;
                try {
                    row = Integer.parseInt(parts[2]);
                    if (row < 1 || row > 15) {
                        System.out.println("Row must be between 1 and 15!");
                        continue;
                    }

                    --row;
                } catch (NumberFormatException var9) {
                    System.out.println("Invalid row number!");
                    continue;
                }

                String colLetter = parts[3];
                int col = this.colLetterToIndex(colLetter);
                if (col >= 0 && col <= 14) {
                    boolean horizontal;
                    if (parts[4].equalsIgnoreCase("H")) {
                        horizontal = true;
                    } else {
                        if (!parts[4].equalsIgnoreCase("V")) {
                            System.out.println("Invalid direction! Use H or V.");
                            continue;
                        }

                        horizontal = false;
                    }

                    this.model.placeWord(word, row, col, horizontal);
                } else {
                    System.out.println("Invalid column letter! Must be A–O.");
                }
            } else {
                System.out.println("Invalid command. Try again!");
            }
        }
    }
}
