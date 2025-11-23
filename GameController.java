/**
 * GameController handles user input and coordinates between the Model and View.
 * It processes commands like PLACE, SWAP, PASS, and EXIT, and manages AI turns.
 */
public class GameController {

    private final GameModel model;
    private final GameViewGUI view;

    public GameController(GameModel model, GameViewGUI view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);

        // If the first player is AI, make their move immediately
        maybeDoAITurn();
    }

    private int colLetterToIndex(String col) {
        return col.toUpperCase().charAt(0) - 'A';
    }

    public void handleCommand(String input) {
        Player current = model.getCurrentPlayer();
        input = input.trim().toUpperCase();

        // -----------------------
        // PASS
        // -----------------------
        if (input.equals("PASS")) {
            model.passTurn();
            maybeDoAITurn();
            return;
        }

        // -----------------------
        // EXIT
        // -----------------------
        if (input.equals("EXIT")) {
            view.displayMessage("Game ended.");
            System.exit(0);
        }

        String[] parts = input.split("\\s+");

        // -----------------------
        // SWAP
        // -----------------------
        if (parts[0].equals("SWAP")) {
            if (parts.length != 2) {
                view.displayMessage("Invalid swap command! Use: SWAP ABC");
                return;
            }
            String tilesToSwap = parts[1];
            if (current.swapTiles(tilesToSwap, model.getBag())) {
                view.displayMessage("Tiles swapped successfully.");
                model.passTurn();
                maybeDoAITurn();
            } else {
                view.displayMessage("Invalid swap! You don't have these tiles.");
            }
            return;
        }

        // -----------------------
        // PLACE
        // -----------------------
        if (parts[0].equals("PLACE")) {
            Player currentPlayer = model.getCurrentPlayer();

            if (model.isFirstMove()) {
                if (parts.length != 3 && parts.length != 4) {
                    view.displayMessage("First move: PLACE WORD DIRECTION (H/V) [BLANKS]");
                    return;
                }
                String word = parts[1];
                String dir = parts[2];
                boolean horizontal;
                if (dir.equalsIgnoreCase("H")) horizontal = true;
                else if (dir.equalsIgnoreCase("V")) horizontal = false;
                else {
                    view.displayMessage("Direction must be H or V.");
                    return;
                }

                boolean success;
                if (parts.length == 4) {
                    String blanks = parts[3];
                    success = model.placeWordWithBlanks(word, 7, 7, horizontal, blanks);
                } else {
                    success = model.placeWord(word, 7, 7, horizontal);
                }

                if (success) model.setFirstMoveDone();
                maybeDoAITurn();
                return;
            }

            if (parts.length != 5 && parts.length != 6) {
                view.displayMessage("Invalid PLACE command! Use: PLACE WORD ROW COL DIRECTION [BLANKS]");
                return;
            }

            String word = parts[1];

            // Parse row
            int row;
            try {
                row = Integer.parseInt(parts[2]) - 1;
                if (row < 0 || row > 14) {
                    view.displayMessage("Row must be 1–15.");
                    return;
                }
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
            if (parts[4].equalsIgnoreCase("H")) horizontal = true;
            else if (parts[4].equalsIgnoreCase("V")) horizontal = false;
            else {
                view.displayMessage("Direction must be H or V.");
                return;
            }

            // Parse blanks if provided
            String blanks = "";
            if (parts.length == 6) blanks = parts[5];

            boolean placed;
            if (!blanks.isEmpty()) placed = model.placeWordWithBlanks(word, row, col, horizontal, blanks);
            else placed = model.placeWord(word, row, col, horizontal);

            if (!placed) {
                String lastError = currentPlayer.getLastError();
                if (lastError != null) view.displayMessage(lastError);
            }

            maybeDoAITurn();
        }
    }

    /**
     * If the current player is an AI, make its move automatically.
     */
    private void maybeDoAITurn() {
        while (model.getCurrentPlayer() instanceof AIPlayer) {
            AIPlayer ai = (AIPlayer) model.getCurrentPlayer();
            boolean moveMade = ai.makeMove(model);
            if (!moveMade) {
                model.passTurn();
            }
            model.setFirstMoveDone();
        }
    }

}
