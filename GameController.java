/**
 * GameController manages interactions between the GameModel and GameViewGUI.
 * It interprets user commands, updates the model, and triggers view updates.
 */
public class GameController {

    private GameModel model;   // The game model managing state
    private GameViewGUI view;  // The GUI for displaying the game

    /**
     * Constructs a GameController with a model and view.
     * Sets this controller in the view for handling user input.
     * 
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
     * 
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
     * 
     * @param input User input string from the GUI
     */
    public void handleCommand(String input) {
        Player current = model.getCurrentPlayer();
        input = input.trim().toUpperCase();

        // Handle simple commands
        if (input.equals("PASS")) {
            model.passTurn();
            return;
        }
        if (input.equals("EXIT")) {
            System.exit(0);
        }

        String[] parts = input.split("\\s+");

        // Handle tile swap
        if (parts[0].equals("SWAP") && parts.length == 2) {
            String tilesToSwap = parts[1];
            if (current.swapTiles(tilesToSwap, model.getBag())) {
                model.passTurn();
            } else {
                view.displayMessage("Invalid swap! You don't have these tiles.");
            }
            return;
        }

        // Validate PLACE command
        if (parts.length != 5 || !parts[0].equals("PLACE")) {
            view.displayMessage("Invalid command! Use PLACE WORD ROW COL DIRECTION");
            return;
        }

        String word = parts[1];

        // Parse row number
        int row;
        try {
            row = Integer.parseInt(parts[2]);
            if (row < 1 || row > 15) {
                view.displayMessage("Row must be 1–15");
                return;
            }
            row--; // Convert to 0-based index
        } catch (NumberFormatException e) {
            view.displayMessage("Invalid row number!");
            return;
        }

        // Parse column letter
        int col = colLetterToIndex(parts[3]);
        if (col < 0 || col > 14) {
            view.displayMessage("Column must be A–O");
            return;
        }

        // Parse direction
        boolean horizontal;
        if (parts[4].equalsIgnoreCase("H")) horizontal = true;
        else if (parts[4].equalsIgnoreCase("V")) horizontal = false;
        else {
            view.displayMessage("Direction must be H or V");
            return;
        }

        // Place the word on the board
        model.placeWord(word, row, col, horizontal);
    }
}
