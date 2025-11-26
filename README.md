# Scrabble - Milestone 4

A Java Scrabble implementation with GUI using MVC and Observer patterns.

**Deliverables:** Source code, UML diagrams, sequence diagrams, documentation, JUnit tests
**Authors:** Bashar Saadi, Kalid Wehbe, Madhav Sharma, Sammy Eyongorock
**Last Updated:** November 26, 2025

---

## How to Run

### Compile and Run

```bash
cd "scrabble/src"
javac *.java
java Main
```

### Gameplay

1. Enter 2-4 players and names at console prompt
2. Choose whether each player is Human or AI
3. Select a board (1. Standard, 2. Diagonal, 3 Corner Star)
4. GUI window opens with board, command input, and player info
5. Type commands in top text field and press Enter (for human players)
6. AI players automatically take their turns
7. Use File menu to Save/Load games (Ctrl+S / Ctrl+L)
8. Use Edit menu for Undo/Redo (Ctrl+Z / Ctrl+Y)

---

## Commands

### First Move (Auto-centers at row 8, column H)

```
PLACE <WORD> <H|V>
Example: PLACE HELLO H
```

### Normal Moves

```
PLACE <WORD> <ROW> <COL> <H|V>
Example: PLACE CAT 8 H H
Rows: 1-15, Columns: A-O, Direction: H (horizontal) or V (vertical)
```

### Placing Words with Blank Tiles

```
PLACE <WORD> <ROW> <COL> <H|V> BLANK <LETTERS>
Example: PLACE CAT 8 H H BLANK T
(Uses a blank tile to represent the letter T)
```

### Other Commands

```
SWAP <TILES>    - Example: SWAP ABC
PASS            - Skip turn
EXIT            - End game
```

---

## New Features in Milestone 4

### 1. Custom Board Selection

- Players can select from multiple custom board layouts at game start
- **Standard Board:** Traditional Scrabble premium square placement
- **Diagonal Board:** Premium squares arranged in diagonal patterns
- **Corner Star Board:** Star-shaped premium square arrangement radiating from corners
- Boards are defined in XML format for easy customization

### 2. Undo/Redo Functionality

- **Undo:** Revert the last move (Ctrl+Z or Edit → Undo)
- **Redo:** Re-apply an undone move (Ctrl+Y or Edit → Redo)
- **Multi-level:** Supports multiple consecutive undo/redo operations
- Complete game state restoration including board, scores, and tile racks
- Efficient implementation using stack-based state snapshots

### 3. Save/Load Game

- **Save Game:** Save current game state to a file (Ctrl+S or File → Save Game)
- **Load Game:** Resume a previously saved game (Ctrl+L or File → Load Game)
- Saves complete game state including:
  - Board configuration and placed tiles
  - All player scores and tile racks
  - Current turn and game progress
  - Undo/redo history
- File format: Serialized `.sav` files with error handling
- User-friendly file chooser dialogs

---

## New Features in Milestone 3

### 1. Blank Tiles

- Two blank tiles are included in the tile bag (standard Scrabble rules)
- Blank tiles can represent any letter when placing a word
- Blank tiles are worth 0 points, even when representing high-value letters
- Players can use multiple blank tiles in a single word

### 2. Premium Squares

The board now includes all standard Scrabble premium squares:

- **Double Letter Score (DL):** Doubles the value of a letter placed on it
- **Triple Letter Score (TL):** Triples the value of a letter placed on it
- **Double Word Score (DW):** Doubles the entire word score (including center star)
- **Triple Word Score (TW):** Triples the entire word score (corners)

Premium squares only apply when a tile is first placed on them. Subsequent words using that tile do not receive the bonus.

### 3. AI Players

- Users can configure 2-4 players as either Human or AI
- AI players automatically take their turns without user input
- AI uses a greedy strategy to maximize score each turn (see AI Strategy section below)

---

## AI Player Strategy

The AI player (`AIPlayer.java`) implements a **greedy strategy** that selects the highest-scoring legal move available. Here is how the AI makes decisions:

### Move Selection Algorithm

1. **Build Available Letters:** The AI examines its rack, noting regular tiles and blank tiles (represented as `*` internally).
2. **Iterate Through Dictionary:** The AI checks every word in the dictionary to see if it can be formed using the current rack letters (including blank substitutions).
3. **Try All Positions:** For each formable word, the AI tries every position (row 0-14, column 0-14) and both orientations (horizontal and vertical).
4. **Validate Placement:** For each position, the AI checks:

   - The word fits within board boundaries
   - The player has the necessary tiles (using `canPlaceWordWithRack`)
   - For the first move: the word must cover the center square (7,7)
   - For subsequent moves: the word must connect to at least one existing tile
   - New tiles do not create invalid perpendicular adjacencies
5. **Score Calculation:** The AI computes the score for each valid placement, including premium square bonuses (DL, TL, DW, TW).
6. **Select Best Move:** The AI tracks the highest-scoring valid move found and executes it.
7. **Pass if No Move:** If no valid word placement is found, the AI passes its turn.

### Key Methods

- `makeMove(GameModel model)`: Main entry point that orchestrates move selection
- `canFormWord(String word, List<Character> rackLetters)`: Checks if a word can be built from available tiles
- `connectsToBoard(Board board, String word, int row, int col, boolean horizontal)`: Ensures word connects to existing tiles
- `checkNeighbors(...)`: Validates perpendicular adjacency rules

### Strategy Characteristics

- **Greedy:** Always selects the immediate highest-scoring move
- **Exhaustive Search:** Considers all dictionary words and board positions
- **Legal Moves Only:** All AI moves are validated against game rules
- **Blank Tile Support:** Can use blank tiles as wildcards when forming words

---

## Architecture

### MVC Pattern

- **Model:** `GameModel.java` - Game state, logic, word validation, scoring
- **View:** `GameViewGUI.java` - Swing GUI, implements `GameObserver`
- **Controller:** `GameController.java` - Parses commands, coordinates model-view, manages AI turns

### Observer Pattern

- `GameObserver` interface enables automatic view updates
- `GameModel` notifies observers on state changes
- Decouples model from view

### Supporting Classes

- `Board.java` - 15x15 grid management, premium square setup
- `Player.java` - Player data (name, score, tiles)
- `AIPlayer.java` - Computer player with greedy move selection (extends Player)
- `Tile.java` - Letter, score, and blank tile support
- `Square.java` - Single board position with bonus type
- `Dictionary.java` - Word validation via HashSet

---

## Team Contributions

### Milestone 1

- **Kalid, Bashar:** Board class, word placement logic
- **Kalid, Bashar, Madhav:** Player class, tile management
- **Kalid, Bashar, Madhav:** Dictionary, game loop
- **All:** Code review, testing

### Milestone 2

- **Kalid, Bashar, Madhav, Sammy:** GameViewGUI (Swing), board display
- **Kalid, Bashar, Madhav:** GameController refactor, command parsing
- **Kalid, Bashar, Madhav, Sammy:** Observer pattern, MVC integration
- **All:** JavaDocs, testing, documentation

### Milestone 3

- **Kalid, Bashar:** Blank tile implementation, premium square scoring
- **Kalid, Bashar:** AI Player implementation, greedy strategy
- **Kalid, Bashar:** Premium square board setup (DL, TL, DW, TW)
- **Kalid, Bashar:** JUnit tests for Milestone 3 features, documentation
- **Madhav, Sammy:** UML and Sequence Diagram
- **All:** Code Review, testing

### Milestone 4

- **Kalid, Bashar:** Custom board XML files (Standard, Diagonal, Corner Star)
- **Kalid, Bashar:** Undo/Redo implementation with state snapshots
- **Kalid, Bashar:** Save/Load game functionality with serialization
- **Kalid, Bashar:** JUnit tests for undo/redo and serialization
- **Kalid, Bashar:** Project organization and documentation updates

---

## Changes from Milestone 1

### Architecture

- **Added:** Observer pattern (`GameObserver` interface)
- **Replaced:** Console view → `GameViewGUI` (Swing)
- **Enhanced:** `GameController` handles GUI commands

### Data Structures

**GameModel:**

- Added `observers: List<GameObserver>` for observer pattern
- Added `firstMove: boolean` for center placement rule
- Added methods: `addObserver()`, `notifyObservers()`, `isFirstMove()`, `setFirstMoveDone()`

**Player:**

- Added `lastError: String` for error propagation through observer updates

**Dictionary:**

- Added `loadedSuccessfully: boolean`, `loadError: String`
- Added methods: `isLoaded()`, `getLoadError()`, `getWordCount()`
- Enhanced error handling (FileNotFoundException, IOException, empty file detection)

**GameController:**

- Refactored `handleCommand()` into smaller methods: `handleSwapCommand()`, `handlePlaceCommand()`, `handleFirstMove()`, `handleNormalMove()`

**Code Quality:**

- Made fields `final` where appropriate (Tile, Player, GameModel, GameController, Dictionary)
- Added complete JavaDocs to all classes

**Rationale:** Observer pattern decouples view from model. First move flag simplifies center placement logic. Error tracking enables better user feedback. Refactored controller follows Single Responsibility Principle. Immutability improves thread safety.

---

## Changes from Milestone 2

### New Classes

**AIPlayer.java:**

- Extends `Player` class
- Implements greedy move selection algorithm
- Methods: `makeMove()`, `canFormWord()`, `connectsToBoard()`, `checkNeighbors()`

### Modified Classes

**Tile.java:**

- Added `blank: boolean` field to track blank tiles
- Added static factory methods: `blankTile()`, `placedBlank(char)`
- Added `isBlank()` method
- Added `getScore()` accessor method

**Board.java:**

- Added `setupPremiumSquares()` method
- Premium square positions now initialized in constructor
- Added center square (7,7) as Double Word Score

**Square.java:**

- Added `Bonus` enum: `NONE`, `DL`, `TL`, `DW`, `TW`
- Added `bonus: Bonus` field
- Added `setBonus()` and `getBonus()` methods

**GameModel.java:**

- Made `board` and `dictionary` fields private (MVC encapsulation)
- Added `LETTER_VALUES` static map for official Scrabble scoring
- Added `computeWordScore()` method with premium square calculations
- Added `placeWordWithBlanks()` method for blank tile support
- Fixed blank tile count from 4 to 2 (standard Scrabble rules)
- Added `addPlayer()` method for AI player support

**GameController.java:**

- Added `maybeDoAITurn()` method to handle AI player turns
- AI turns execute automatically after human moves
- Removed debug print statements (MVC compliance)

**Player.java:**

- Added `takeTileForLetter()` method for blank tile handling

### Data Structure Changes

| Class     | Change                      | Rationale                        |
| --------- | --------------------------- | -------------------------------- |
| Tile      | Added `blank` boolean     | Support for blank/wildcard tiles |
| Square    | Added `Bonus` enum        | Track premium square types       |
| Board     | Added premium square arrays | Standard Scrabble board layout   |
| GameModel | Added `LETTER_VALUES` map | Official Scrabble letter scoring |
| AIPlayer  | New class extending Player  | Computer-controlled players      |

### Rationale

- **Blank tiles:** Enable wildcard gameplay per Scrabble rules
- **Premium squares:** Add strategic depth with score multipliers
- **AI Player:** Enables single-player and mixed human/AI games
- **Private fields in GameModel:** Proper MVC encapsulation
- **computeWordScore():** Centralized scoring with premium square logic

---

## Changes from Milestone 3

### New Features

**Custom Board Selection:**

- Added board selection menu at game startup
- Created 3 XML board configuration files with different premium square layouts
- Enhanced `Board.java` to load boards from XML files

**Undo/Redo System:**

- Added `GameState` class to capture complete game snapshots
- Implemented `createStateSnapshot()` and `restoreState()` in `GameModel`
- Added undo/redo methods in `GameController` with stack-based history
- Integrated undo/redo menu items in `GameViewGUI` with keyboard shortcuts

**Save/Load Functionality:**

- Made all model classes `Serializable` (Board, Player, Tile, Square, GameModel, GameState, Dictionary)
- Added `saveGame()` and `loadGame()` methods in `GameController`
- Implemented file chooser dialogs with `.sav` file extension filter
- Added comprehensive error handling for I/O operations
- Created nested `SaveState` class to encapsulate model and undo/redo stacks

### Modified Classes

**GameModel.java:**

- Added `Serializable` interface with `serialVersionUID`
- Enhanced JavaDocs for `createStateSnapshot()` and `restoreState()`
- Made `observers` field `transient` to prevent GUI serialization
- Added null checks in `addObserver()` and `notifyObservers()` for transient field handling

**GameController.java:**

- Added imports for serialization and file choosers
- Implemented `saveGame()` method with file dialog and serialization
- Implemented `loadGame()` method with deserialization and state restoration
- Added nested `SaveState` class for complete game persistence
- Added undo/redo stack management

**GameViewGUI.java:**

- Updated menu bar to include File menu with Save/Load options
- Added Edit menu with Undo/Redo options
- Added keyboard shortcuts (Ctrl+S, Ctrl+L, Ctrl+Z, Ctrl+Y)
- Integrated with controller's save/load and undo/redo methods

**Board.java, Player.java, Tile.java, Square.java, GameState.java, Dictionary.java:**

- All implement `Serializable` interface
- Added `serialVersionUID` for version control
- Added `copy()` methods where needed for deep cloning

**Main.java:**

- Updated to include board file path selection
- Board file paths now point to `boards/` directory

### Data Structure Changes

| Class             | Change                                 | Rationale                                                   |
| ----------------- | -------------------------------------- | ----------------------------------------------------------- |
| GameController    | Added `SaveState` inner class        | Encapsulates complete save data including undo/redo history |
| GameController    | Added undo/redo stacks                 | Manages game state history for undo/redo                    |
| GameModel         | Made `observers` field `transient` | Prevents GUI components from being serialized               |
| GameState         | Made `Serializable`                  | Enables saving undo/redo history                            |
| All model classes | Added `Serializable`                 | Required for save/load functionality                        |
| Dictionary        | Made `Serializable`                  | Required since it's contained in GameModel                  |
| Board             | Added XML file loading support         | Supports custom board selection                             |

### Rationale

- **Custom Boards:** Adds variety and replayability to the game
- **Undo/Redo:** Essential quality-of-life feature for players to correct mistakes
- **Save/Load:** Enables long game sessions across multiple play sessions
- **Serialization:** Standard Java approach for object persistence
- **State Snapshots:** Immutable copies prevent reference issues in undo/redo
- **Transient Observers:** Prevents Swing GUI components from being serialized, avoiding platform-specific serialization issues

---

## Known Issues

1. **No Game End Detection:** Manual EXIT required
2. **No Cross-Word Validation:** Perpendicular words formed are not validated against dictionary
3. **AI Performance:** Large dictionaries may cause slight delays on AI turns
4. **Save File Compatibility:** Saved games may not be compatible across different Java versions due to serialization

---

## Testing

### JUnit Tests

GameModelTest.java includes comprehensive tests (requires JUnit 4):

**Basic Tests (Milestone 2):**

- Valid/invalid word placement
- Turn passing
- Out-of-bounds validation
- Tile swapping (valid/invalid)
- Score calculation

**Blank Tile Tests (Milestone 3):**

- `testBlankTilePlacement` - Placing words with blank tiles
- `testBlankTileScoresZero` - Blank tiles contribute 0 points
- `testMultipleBlankTiles` - Using multiple blanks in one word

**Premium Square Tests (Milestone 3):**

- `testDoubleLetterScore` - DL squares double letter value
- `testTripleLetterScore` - TL squares triple letter value
- `testDoubleWordScore` - DW squares double word score
- `testTripleWordScore` - TW squares triple word score
- `testPremiumSquareOnlyAppliesOnce` - Bonuses only on first tile placement

**AI Player Tests (Milestone 3):**

- `testAIPlayerCreation` - AI player instantiation and inheritance
- `testAIPlayerMakesLegalMove` - AI only places valid dictionary words
- `testAIPlayerPassesWhenNoMoves` - AI passes when no moves available
- `testAIPlayerSelectsHighScoringMove` - AI greedy strategy verification
- `testAIPlayerUsesBlankTiles` - AI can utilize blank tiles

**Undo/Redo Tests (Milestone 4):**

- `testUndoPlacedWord` - Undo correctly reverts a placed word
- `testRedoPlacedWord` - Redo re-applies an undone move
- `testMultipleUndo` - Multiple consecutive undo operations
- `testMultipleRedo` - Multiple consecutive redo operations
- `testGameStateSnapshotPreservesData` - State snapshots preserve all game data
- `testUndoSwapTiles` - Undo tile swapping operations

**Serialization/Deserialization Tests (Milestone 4):**

- `testSerializeGameModel` - GameModel can be serialized
- `testDeserializeGameModel` - GameModel can be deserialized
- `testSerializationPreservesPlayerData` - Player scores and names preserved
- `testSerializationPreservesBoardState` - Board tiles preserved after save/load
- `testSerializeGameState` - GameState objects are serializable
- `testDeserializationErrorHandling` - Proper error handling for corrupted data
- `testSerializationAfterMultipleMoves` - Save/load works after multiple turns
- `testSerializationPreservesTileBag` - Tile bag state is preserved

---

## Files

```
src/
├── Main.java              # Entry point
├── GameModel.java         # Model (game logic, scoring)
├── GameViewGUI.java       # View (Swing GUI)
├── GameController.java    # Controller (command handling, AI turns)
├── GameObserver.java      # Observer interface
├── Board.java             # 15x15 grid with premium squares
├── Player.java            # Player data
├── AIPlayer.java          # AI player with greedy strategy
├── Tile.java              # Tile representation (including blanks)
├── Square.java            # Board square with bonus type
├── Dictionary.java        # Word validation
├── GameModelTest.java     # Unit tests
└── dictionary.txt   
```

---

## Future Improvements (Milestone 5?)

1. File Organization
2. Automatic game end detection (empty bag + empty rack)
3. Cross-word validation for perpendicular words
4. Network multiplayer support
5. Timer for timed games
6. Hint system for suggesting valid words
7. Score history and statistics tracking

---
