import java.util.*;

public class AIPlayer extends Player {

    public AIPlayer(String name) {
        super(name);
    }

    public boolean makeMove(GameModel model) {
        Board board = model.getBoard();
        boolean firstMove = model.isFirstMove();
        String bestWord = null;
        int bestRow = -1, bestCol = -1;
        boolean bestHorizontal = true;
        int maxScore = -1;

        // Build the rack with '*' representing blanks
        List<Character> rackLetters = new ArrayList<>();
        for (Tile t : getRack()) {
            rackLetters.add(t.isBlank() ? '*' : t.getLetter());
        }

        // Test every dictionary word
        for (String w : model.getDictionary().getAllWords()) {
            String word = w.toUpperCase();

            // Must be buildable from rack (+ blanks)
            if (!canFormWord(word, rackLetters)) continue;

            for (int r = 0; r < 15; r++) {
                for (int c = 0; c < 15; c++) {
                    for (boolean horizontal : new boolean[]{true, false}) {

                        // First move forced to center
                        if (firstMove) {
                            r = 7;
                            c = 7;
                        } else {
                            // Must attach to a tile on the board
                            if (!connectsToBoard(board, word, r, c, horizontal)) continue;
                        }

                        // Must be placeable using rack (blank substitution handled inside your board logic)
                        if (!board.canPlaceWordWithRack(word, r, c, horizontal, this)) continue;

                        // NEW letters cannot be adjacent perpendicularly to existing letters
                        if (!checkNeighbors(board, word, r, c, horizontal)) continue;

                        // Score move
                        int score = model.computeWordScore(word, r, c, horizontal, null);

                        // Track best scoring move
                        if (score > maxScore) {
                            maxScore = score;
                            bestWord = word;
                            bestRow = r;
                            bestCol = c;
                            bestHorizontal = horizontal;
                        }

                        if (firstMove) break;
                    }
                    if (firstMove) break;
                }
                if (firstMove) break;
            }
        }

        // If we found a move → place it
        if (bestWord != null) {
            System.out.println("AI placing word: " + bestWord +
                    " at (" + bestRow + "," + bestCol + ")" +
                    (bestHorizontal ? " horizontally" : " vertically"));

            model.placeWord(bestWord, bestRow, bestCol, bestHorizontal);
            return true;
        }

        // Otherwise → pass turn
        model.passTurn();
        return false;
    }

    // --------------------------
    //    BLANK TILE SUPPORT
    // --------------------------

    private boolean canFormWord(String word, List<Character> rackLetters) {
        List<Character> copy = new ArrayList<>(rackLetters);

        for (char c : word.toCharArray()) {
            if (copy.contains(c)) {
                copy.remove((Character) c);
            } else if (copy.contains('*')) {
                copy.remove((Character) '*'); // blank used
            } else {
                return false;
            }
        }
        return true;
    }

    // Must touch at least one tile on board (except first move)
    private boolean connectsToBoard(Board board, String word, int row, int col, boolean horizontal) {
        for (int i = 0; i < word.length(); i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (board.squareHasTile(r, c)) return true;
        }
        return false;
    }

    // New letters cannot have perpendicular neighbors
    private boolean checkNeighbors(Board board, String word, int row, int col, boolean horizontal) {
        for (int i = 0; i < word.length(); i++) {

            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);

            // Do NOT check neighbors for letters already on the board
            if (board.squareHasTile(r, c)) continue;

            if (horizontal) {
                if ((r > 0 && board.squareHasTile(r - 1, c)) ||
                        (r < 14 && board.squareHasTile(r + 1, c))) {
                    return false;
                }
            } else {
                if ((c > 0 && board.squareHasTile(r, c - 1)) ||
                        (c < 14 && board.squareHasTile(r, c + 1))) {
                    return false;
                }
            }
        }
        return true;
    }
}
