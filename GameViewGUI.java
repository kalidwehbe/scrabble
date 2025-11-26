import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GameViewGUI extends JFrame implements GameObserver {

    private JTextArea boardArea;
    private JTextArea messageArea;
    private JTextArea rulesArea;
    private JTextArea playersArea;
    private JTextField commandInput;
    private JLabel scoreReferenceLabel;
    private GameController controller;

    public GameViewGUI() {
        setTitle("Scrabble Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 850);
        setLayout(new BorderLayout(10, 10));

        createMenuBar(); // <-- New menu bar for Undo/Redo

        // -------------------------------
        // CENTER PANEL: 2x2 grid
        // -------------------------------
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // Top-left: Players + Scores
        playersArea = new JTextArea();
        playersArea.setEditable(false);
        playersArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        playersArea.setLineWrap(true);
        playersArea.setWrapStyleWord(true);
        JScrollPane playersScroll = new JScrollPane(playersArea);
        centerPanel.add(playersScroll);

        // Top-right: Board
        boardArea = new JTextArea();
        boardArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        boardArea.setEditable(false);
        JScrollPane boardScroll = new JScrollPane(boardArea);
        centerPanel.add(boardScroll);

        // Bottom-left: Rules / Instructions
        rulesArea = new JTextArea();
        rulesArea.setEditable(false);
        rulesArea.setLineWrap(true);
        rulesArea.setWrapStyleWord(true);
        rulesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        rulesArea.setText(getRulesText());
        JScrollPane rulesScroll = new JScrollPane(rulesArea);
        centerPanel.add(rulesScroll);

        // Bottom-right: Messages / Logs
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane messageScroll = new JScrollPane(messageArea);
        centerPanel.add(messageScroll);

        add(centerPanel, BorderLayout.CENTER);

        // -------------------------------
        // BOTTOM PANEL: Command Input + Letter Scores
        // -------------------------------
        JPanel bottomPanel = new JPanel(new BorderLayout());

        scoreReferenceLabel = new JLabel(getLetterScoreString());
        scoreReferenceLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        scoreReferenceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreReferenceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        bottomPanel.add(scoreReferenceLabel, BorderLayout.NORTH);

        commandInput = new JTextField();
        commandInput.setFont(new Font("Monospaced", Font.PLAIN, 16));
        bottomPanel.add(commandInput, BorderLayout.SOUTH);

        commandInput.addActionListener(e -> {
            if (controller != null) {
                String cmd = commandInput.getText().trim();
                controller.handleCommand(cmd);
                commandInput.setText("");
            }
        });

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ----------------------------------------
    // MENU BAR WITH UNDO / REDO
    // ----------------------------------------
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");

        // Undo action
        undoItem.addActionListener(e -> {
            if (controller != null) {
                controller.undoMove();
            }
        });

        // Redo action
        redoItem.addActionListener(e -> {
            if (controller != null) {
                controller.redoMove();
            }
        });

        // Keyboard shortcuts
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    // ----------------------------------------
    // CONTROLLER LINK
    // ----------------------------------------
    public void setController(GameController controller) {
        this.controller = controller;
    }

    // ----------------------------------------
    // BOARD UPDATE
    // ----------------------------------------
    private void updateBoardDisplay(Board board) {
        boardArea.setText(board.toString());
    }

    // ----------------------------------------
    // OBSERVER UPDATE
    // ----------------------------------------
    @Override
    public void update(Board board, List<Player> players, Player currentPlayer) {
        updateBoardDisplay(board);

        // Update players + scores
        playersArea.setText("");
        for (Player p : players) {
            playersArea.append(p.getName() + " - Score: " + p.getScore() + "\n");
            playersArea.append("Tiles: ");
            for (Tile t : p.getRack()) {
                playersArea.append(t.toString() + " ");
            }
            playersArea.append("\n\n");
        }

        // Update messages/log
        messageArea.setText("Current Turn: " + currentPlayer.getName() + "\n\n");
        String lastError = currentPlayer.getLastError();
        if (lastError != null && !lastError.isEmpty()) {
            messageArea.append("Message: " + lastError + "\n");
        }
    }

    public void displayMessage(String msg) {
        messageArea.append(msg + "\n");
    }

    public void showMessage(String message) {
        messageArea.append(message + "\n");
    }

    // ----------------------------------------
    // RULES TEXT
    // ----------------------------------------
    private String getRulesText() {
        return "=== SCRABBLE COMMANDS ===\n" +
                "PLACE WORD ROW COL DIRECTION - Place a word\n" +
                "   Example: PLACE HELLO 8 H H\n" +
                "   Optional blanks at end if used: PLACE HELLO 8 H H LO\n\n" +
                "SWAP LETTERS - Swap tiles from your rack\n" +
                "   Example: SWAP ABC\n\n" +
                "PASS - Skip your turn\n" +
                "EXIT - Quit the game\n\n" +
                "=== NOTES ===\n" +
                "- Rows: 1 to 15\n" +
                "- Columns: A to O\n" +
                "- Direction: H = Horizontal, V = Vertical\n" +
                "- Blank tiles can represent any letter and have 0 points\n" +
                "- Letter scores are shown at the bottom";
    }

    // ----------------------------------------
    // LETTER SCORE STRING
    // ----------------------------------------
    private String getLetterScoreString() {
        return "Letter Values: " +
                "A=1  B=3  C=3  D=2  E=1  F=4  G=2  H=4  I=1  J=8  " +
                "K=5  L=1  M=3  N=1  O=1  P=3  Q=10 R=1  S=1  T=1  " +
                "U=1  V=4  W=4  X=8  Y=4  Z=10" + "\n 2=DL 3=TL d=DW t=TW";
    }
}


