/**
 * GameController manages interactions between the GameModel and GameViewGUI.
 * It interprets user commands, updates the model, and triggers view updates.
 */
public class GameController {

    private final GameModel model;   // The game model managing state
    private final GameViewGUI view;  // The GUI for displaying the game

    /**
     * Constructs a GameController with a model and view.
     * Sets this controller in the view for handling user input.
     * @param model The GameModel containing the game state
     * @param view The GameViewGUI to update and receive input from
     */
    public GameController(GameModel model, GameViewGUI view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
    }

    /**
     * Converts a column letter (A–O) to a 0-based column index.
     * @param col Column letter as a string (e.g., "A", "B", ..., "O")
     * @return 0-based index corresponding to the column letter
     */
    private int colLetterToIndex(String col) {
        return col.toUpperCase().charAt(0) - 'A';
    }

    /**
     * Handles a command input from the user via the GUI.
     * Commands supported:
     * <ul>
     *   <li>PLACE WORD ROW COL DIRECTION - place a word on the board</li>
     *   <li>SWAP TILES - swap tiles from the player's rack</li>
     *   <li>PASS - skip the current turn</li>
     *   <li>EXIT - quit the game</li>
     * </ul>
     * Provides validation for row/column, direction, and tile availability.
     * @param input User input string from the GUI
     */
    public void handleCommand(String input) {
        input = input.trim().toUpperCase();

        // Handle simple commands
        if (input.equals("PASS")) {
            model.passTurn();
            return;
        }

        if (input.equals("EXIT")) {
            view.displayMessage("Game ended.");
            System.exit(0);
        }

        String[] parts = input.split("\\s+");
        if (parts.length == 0) {
            return;
        }

        // Route to appropriate handler
        switch (parts[0]) {
            case "SWAP":
                handleSwapCommand(parts);
                break;
            case "PLACE":
                handlePlaceCommand(parts);
                break;
            default:
                view.displayMessage("Unknown command. Use PLACE, SWAP, PASS, or EXIT.");
        }
    }

    /**
     * Handles the SWAP command to exchange tiles.
     * @param parts Command parts split by whitespace
     */
    private void handleSwapCommand(String[] parts) {
        Player current = model.getCurrentPlayer();

        if (parts.length != 2) {
            view.displayMessage("Invalid swap command! Use: SWAP ABC");
            return;
        }

        String tilesToSwap = parts[1];
        if (current.swapTiles(tilesToSwap, model.getBag())) {
            view.displayMessage("Tiles swapped successfully.");
            model.passTurn(); // swapping counts as a turn
        } else {
            view.displayMessage("Invalid swap! You don't have these tiles.");
        }
    }

    /**
     * Handles the PLACE command to place a word on the board.
     * @param parts Command parts split by whitespace
     */
    private void handlePlaceCommand(String[] parts) {
        if (model.isFirstMove()) {
            handleFirstMove(parts);
        } else {
            handleNormalMove(parts);
        }
    }

    /**
     * Handles the first move which must be placed at the center (7,7).
     * @param parts Command parts split by whitespace
     */
    private void handleFirstMove(String[] parts) {
        if (parts.length != 3) {
            view.displayMessage("First move: PLACE WORD DIRECTION (H/V)");
            return;
        }

        String word = parts[1];
        String dir = parts[2];

        boolean horizontal;
        if (dir.equalsIgnoreCase("H")) {
            horizontal = true;
        } else if (dir.equalsIgnoreCase("V")) {
            horizontal = false;
        } else {
            view.displayMessage("Direction must be H or V.");
            return;
        }

        // Place word at center (7,7)
        boolean success = model.placeWord(word, 7, 7, horizontal);
        if (success) {
            model.setFirstMoveDone();
        }
    }

    /**
     * Handles a normal word placement after the first move.
     * @param parts Command parts split by whitespace
     */
    private void handleNormalMove(String[] parts) {
        Player current = model.getCurrentPlayer();

        if (parts.length != 5) {
            view.displayMessage("Invalid PLACE command! Use: PLACE WORD ROW COL DIRECTION");
            return;
        }

        String word = parts[1];

        // Parse row
        int row;
        try {
            row = Integer.parseInt(parts[2]);
            if (row < 1 || row > 15) {
                view.displayMessage("Row must be 1–15.");
                return;
            }
            row--; // convert to 0-based index
        } catch (NumberFormatException e) {
            view.displayMessage("Invalid row number!");
            return;
        }

        // Parse column
        int col = colLetterToIndex(parts[3]);
        if (col < 0 || col > 14) {
            view.displayMessage("Column must be A–O.");
            return;
        }

        // Parse direction
        boolean horizontal;
        if (parts[4].equalsIgnoreCase("H")) {
            horizontal = true;
        } else if (parts[4].equalsIgnoreCase("V")) {
            horizontal = false;
        } else {
            view.displayMessage("Direction must be H or V.");
            return;
        }

        // Attempt to place word
        boolean placed = model.placeWord(word, row, col, horizontal);
        if (!placed) {
            // Error message already set inside model
            String lastError = current.getLastError();
            if (lastError != null) {
                view.displayMessage(lastError);
            }
        }
    }
}
