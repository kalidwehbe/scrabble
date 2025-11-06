import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * GameViewGUI is a graphical user interface (GUI) for a Scrabble game.
 * It displays the board, instructions, messages, player racks, and allows
 * the user to input commands.
 * 
 * Implements the GameObserver interface to receive updates from the game model.
 */
public class GameViewGUI extends JFrame implements GameObserver {

    private JTextArea boardArea;      // Displays the Scrabble board
    private JTextField inputField;    // Field for user command input
    private JTextArea messageArea;    // Displays game messages, errors, and scores
    private JTextArea instructionArea;// Displays instructions and commands
    private GameController controller;// Reference to the game controller
    private JLabel turnLabel;         // Displays the current player's turn

    /**
     * Constructs the GUI window for the Scrabble game.
     * Initializes the board display, input field, message area, and instructions.
     */
    public GameViewGUI() {
        super("Scrabble GUI");
        setLayout(new BorderLayout());
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Board display
        boardArea = new JTextArea(20, 20);
        boardArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        boardArea.setEditable(false);
        add(new JScrollPane(boardArea), BorderLayout.CENTER);

        // Instructions display
        instructionArea = new JTextArea(5, 20);
        instructionArea.setEditable(false);
        instructionArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        instructionArea.setText(
                "=== Commands ===\n" +
                "PLACE WORD ROW COL DIRECTION - Place a word\n" +
                "   Example: PLACE HELLO 8 H H\n" +
                "SWAP TILES - Swap tiles from your rack\n" +
                "   Example: SWAP ABC\n" +
                "PASS - Skip your turn\n" +
                "EXIT - Quit the game\n"
        );
        add(new JScrollPane(instructionArea), BorderLayout.SOUTH);

        // Input field for commands
        inputField = new JTextField();
        add(inputField, BorderLayout.NORTH);

        // Message area for errors, info, and scores
        messageArea = new JTextArea(5, 20);
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.EAST);

        // Handle user input
        inputField.addActionListener(e -> {
            if (controller != null) controller.handleCommand(inputField.getText());
            inputField.setText("");
        });

        setVisible(true);
    }

    /**
     * Sets the controller for this GUI.
     * 
     * @param controller The GameController that handles user commands
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Updates the board display with the current board state.
     * 
     * @param board The Board object representing the current state of the game
     */
    private void updateBoardDisplay(Board board) {
        boardArea.setText(board.toString());
    }

    /**
     * Displays a message in the message area.
     * 
     * @param msg The message string to display
     */
    public void displayMessage(String msg) {
        messageArea.append(msg + "\n");
    }

    /**
     * Updates the turn label to show the current player's turn.
     * 
     * @param playerName Name of the player whose turn it is
     */
    public void updateTurn(String playerName) {
        turnLabel.setText("Turn: " + playerName);
    }

    /**
     * Called whenever the model notifies observers of a change.
     * Updates the board display, current turn, and player scores/racks.
     * 
     * @param board The current game board
     * @param players List of all players in the game
     * @param currentPlayer The player whose turn it currently is
     */
    @Override
    public void update(Board board, List<Player> players, Player currentPlayer) {
        // Update board
        updateBoardDisplay(board);

        // Show current turn
        messageArea.setText("Current Turn: " + currentPlayer.getName() + "\n\n");

        // Show scores and player racks
        for (Player p : players) {
            messageArea.append(p.getName() + " - Score: " + p.getScore() + "\n");
            messageArea.append("Tiles: ");
            for (Tile t : p.getRack()) {
                messageArea.append(t.toString() + " ");
            }
            messageArea.append("\n\n");
        }
    }

    /**
     * Convenience method to show temporary messages or errors in the message area.
     * 
     * @param message The message string to display
     */
    public void showMessage(String message) {
        messageArea.append(message + "\n");
    }
}
