# Java Console Minesweeper

Classic computer game Minesweeper implemented in Java for consoles.

Game includes generating boards with non-standard shapes and functionalities known from newer versions of the game.

## Key Functionalities

* **Custom Shapes:** Game allows for generation of boards with custom shapes, currently implemented: `Cross`, `Z`, `Circle` or `Ring`.
* **First-Click Safe:** Mines are generated only after the first move of the player, excluding the selected cell, guaranteeing that the player cannot lose on his first move.
* **Chording:** Checking already checked cell that has as many flags as the number on the cell automatically checks all unchecked cells around the selected cell.
* **Flood-Fill:** Empty cells automatically discover their neighbours.
* **Stuck system:** Stuck command allows player to let the game automatically flag a bomb that is near checked tile.
