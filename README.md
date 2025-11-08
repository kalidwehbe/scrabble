# Scrabble - Milestone 2

A Java Scrabble implementation with GUI using MVC and Observer patterns.

**Deliverables:** Source code, UML diagrams, documentation, tests
**Authors:** Bashar Saadi, Kalid Wehbe, Madhav Sharma, Sammy Eyongorock
**Last Updated:** November 8, 2025

---

## How to Run

### Compile and Run

```bash
cd "/Users/bs/Desktop/SYSC3110/Project/scrabble 2 copy/src"
javac *.java
java Main
```

### Gameplay

1. Enter 2-4 players and names at console prompt
2. GUI window opens with board, command input, and player info
3. Type commands in top text field and press Enter

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

### Other Commands

```
SWAP <TILES>    → Example: SWAP ABC
PASS            → Skip turn
EXIT            → End game
```

---

## Architecture

### MVC Pattern

- **Model:** `GameModel.java` - Game state, logic, word validation
- **View:** `GameViewGUI.java` - Swing GUI, implements `GameObserver`
- **Controller:** `GameController.java` - Parses commands, coordinates model-view

### Observer Pattern

- `GameObserver` interface enables automatic view updates
- `GameModel` notifies observers on state changes
- Decouples model from view

### Supporting Classes

- `Board.java` - 15×15 grid management
- `Player.java` - Player data (name, score, tiles)
- `Tile.java` - Letter and score (immutable)
- `Square.java` - Single board position
- `Dictionary.java` - Word validation via HashSet

---

## Team Contributions

### Milestone 1

- **Kalid, Bashar:** Board class, word placement logic
- **Kalid, Bashar, Madhav:** Player class, tile management
- **Kalid, Bashar, Madhav:** Dictionary, game loop
- **All:** Code review, testing

### Milestone 2

- **Kalid, Bashar, Madhav:** GameViewGUI (Swing), board display
- **Kalid, Bashar, Madhav:** GameController refactor, command parsing
- **Kalid, Bashar, Madhav:** Observer pattern, MVC integration
- **All:** JavaDocs, testing, documentation

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

## Known Issues

1. **No Premium Squares:** DW, TW, DL, TL not implemented
2. **No Blank Tiles:** Wildcard tiles not supported
3. **No Game End Detection:** Manual EXIT required
4. **No Word Adjacency:** Words don't need to connect
5. **No Undo:** Moves are permanent

---

## Testing

### JUnit Tests

GameModelTest.java includes 7 tests (requires JUnit 4):

- Valid/invalid word placement
- Turn passing
- Out-of-bounds validation
- Tile swapping (valid/invalid)
- Score calculation

### Run Tests

```bash
javac -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar src/GameModelTest.java src/*.java
java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore GameModelTest
```

---

## Files

```
src/
├── Main.java              # Entry point
├── GameModel.java         # Model (game logic)
├── GameViewGUI.java       # View (Swing GUI)
├── GameController.java    # Controller (command handling)
├── GameObserver.java      # Observer interface
├── Board.java             # 15×15 grid
├── Player.java            # Player data
├── Tile.java              # Tile representation
├── Square.java            # Board square
├── Dictionary.java        # Word validation
├── GameModelTest.java     # Unit tests
└── dictionary.txt         # 85k+ words for validation
```

---
