# Pac‑Man Game Server

`ex3.server` implements a Pac‑Man-like game:

- Each `Level` is generated procedurally with `LevelBuilder`
- The `Server` holds all game state and applies game rules each “tick”.
- The `Controller` reads keyboard input and converts it into actions.
- The `GUI` draws the board, Pac-Man, ghosts, and a HUD line.

## Representation

### Board (`int[][] board`)
The board is a 2D grid of integers. Each cell contains a tile type from `ex3.util.GameConstants`, i.e.:

- `WALL`
- `EMPTY`
- `DOT`
- `POWERUP`

Coordinates are used consistently as:

- `board[x][y]`
- `x` increases to the right
- `y` increases upward

### Directions
Directions are also integers from `GameConstants`:

- `UP`, `DOWN`, `LEFT`, `RIGHT`, and `STAY`

Helper methods in `Dir`:
- `isDir(d)` checks if a value is one of the four movement directions.
- `opposite(d)` returns the opposite direction (used so ghosts avoid reversing when possible).

## Classes

### `Level`
Immutable snapshot of an initialized level:
- `board` tiles
- Pac-Man start (`pacX`, `pacY`)
- ghost spawn positions + release delays
- ghost-house door position (`doorX`, `doorY`)
- initial `foodLeft` count

### `LevelBuilder`
Procedurally generates a level based on `scenario`:
- Board size grows with scenario (`w = 19 + 2*scenario`, `h = 15 + 2*scenario`)
- Creates outer corridors and a “ghost house” in the center
- Fills corridors with `DOT`s, places 4 `POWERUP`s in corners
- Places Pac-Man above the ghost house
- Spawns 3 ghosts inside the house, with delays: `0ms`, `2500ms`, `5000ms`
- Counts total food (`DOT` + `POWERUP`) into `foodLeft`

### `Server`
Holds and updates:
- Pac-Man position and direction
- Ghost array (`ex3.model.Ghost`)
- Score, steps, kills, food left
- Flags: `paused`, `done`, `won`, `cyclic`

Core logic:
- `step(dir)`:
    1. Move Pac-Man (`movePac`)
    2. Update ghosts (`updateGhosts`)
    3. Resolve collisions (`resolveCollisions`)
    4. Increment steps and check win condition (`foodLeft <= 0`)

Important mechanics:
- **Walls** block movement (obstacles).
- **Dots**: +1 score, decrements `foodLeft`.
- **Powerups**: +5 score, makes all ghosts edible for 5.2 seconds (`powerDurationMs`).
- **Ghost movement**: random walk every 180ms (`ghostStepMs`), avoids reversing direction if another option exists.
- **Ghost release**: ghosts start unreleased; they move toward the door tile and become “released” once they reach it.

Collision rules:
- If Pac-Man and a released ghost occupy the same tile:
    - If ghost is edible: Pac-Man “eats” it (+200 score, increments kills), ghost respawns and re-releases later.
    - Otherwise: game ends.

Cyclic vs non-cyclic:
- Wrapping behavior is implemented in `wrap(x, y)`.

### `GUI`
Draws:
- Walls (blue squares)
- Dots (small pink circles)
- Power-ups (green circles)
- Ghosts (white circles; cyan when edible)
- Pac-Man (yellow circle with a small “eye” indicating direction)
- HUD text line at the top (`Server.getHudLine()` is designed for this)

### `Controller` + `Actions`
`Controller.poll()` reads keys and returns an `Actions` object containing:
- `dir` (arrow keys or WASD)
- one-shot toggles:
    - Space: pause/unpause
    - C: toggle cyclic mode
    - R: restart
    - Q: quit

---

## Controls

- Move: Arrow keys or `W A S D`
- Pause/Unpause: `Space`
- Toggle cyclic wrap: `C`
- Restart: `R`
- Quit: `Q`
