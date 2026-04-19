# Java Minesweeper (GUI & Console)

Object-oriented implementation of the classic computer game Minesweeper, written in Java. 

Originally built as a console application, this project has been refactored with a decoupled architecture to support a graphical desktop interface using Java Swing, while retaining the classic Command Line Interface (CLI) functionality.

## Key Functionalities

* **Dual Interface:** Play visually via the Swing GUI, or via the terminal using the CLI.
* **Custom Board Shapes:** The game dynamically maps grids to allow generation of boards in the shapes of a **Cross**, **Z**, **Circle**, or **Ring**.
* **Custom Difficulty:** Choose from standard presets (Beginner, Intermediate, Expert) or specify your own exact grid dimensions and bomb-to-tile ratios.
* **First-Click Safe:** Mines are strictly generated *after* your first move, guaranteeing you will never explode on your very first click.
* **Chording:** Clicking an already revealed number that has the correct amount of surrounding flags will automatically reveal the remaining unflagged adjacent tiles, greatly speeding up gameplay.
* **Flood-Fill:** Revealing an empty cell (0 adjacent mines) automatically cascades to reveal all connected safe neighbours.
* **Stuck System (Hint):** A custom 'stuck' command allows the player to ask the game to automatically, and safely, flag one guaranteed bomb near a discovered tile if they are stuck.

## Tech Stack

* **Language:** Java (JDK 8+)
* **UI Framework:** Java Swing
* **Architecture:** Model-View-Controller (MVC) inspired, decoupling game state logic from rendering mechanisms.

## Project Structure

* **View / Entry Points:**
    * `MinesweeperGUI.java` - The main entry point for the desktop graphical version. Handles window rendering, mouse inputs, and visual updates.
    * `Main.java` - The main entry point for the terminal/console version. Handles standard input parsing and text-based board rendering.
* **Model / Game Logic:**
    * `Game.java` - The core controller. Manages game state (win/loss), coordinates board generation, handles move validations, and processes hints.
    * `Board.java` - The grid engine. Manages the mathematical generation of custom shapes, random mine placement, and neighbor-discovery algorithms (like flood-fill).
    * `Cell.java` - The data class representing an individual tile (stores mine state, revealed state, flag state, and adjacent mine count).
    * `Point.java` - A lightweight wrapper for `(col, row)` coordinates, used heavily by the internal `HashMap` to manage the grid.

## How to Play

### Running the Graphical GUI
Run the `main` method inside `MinesweeperGUI.java`.
* **Left-Click:** Reveal a tile.
* **Right-Click:** Place/Remove a flag.
* **Left-Click on a Revealed Number:** Perform a "Chord" (reveals surrounding tiles if flags match).

### Running the Console CLI
Run the `main` method inside `Main.java`.
* Type `C [column] [row]` to **C**heck/Reveal a cell (e.g., `C 4 5`).
* Type `F [column] [row]` to **F**lag a cell (e.g., `F 4 5`).
* Type `stuck` to get a hint.
* Type `help` at any time for a full list of commands.
