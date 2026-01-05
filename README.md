# Pac-Man is Going Places

## Overview

This is the third assignment for the 'Intro to Computer Science' course at Ariel University (2026).

This project is divided into:

1. Client side - An algorithm that guides the pac-man to eat dots and avoid ghosts.
2. Server side - An engine that manages game logic, ghost AI, and GUI

### The algorithm

#### Data

Our algorithm has access to:

- `int[][] board = game.getGame(0)` the game board.
    - `0`/`black` is empty
    - `1`/`blue` is wall
    - `3`/`pink` is food
    - `5`/`green` is power-up
- `String[] pos = game.getPos(code).split(",")` pac-man's position
- `GhostCL[] ghosts = game.getGhosts(code)` array of ghost data
    - `g.getStatus()` is whether the ghost is eatable
    - `g.getType()` is the ghosts algorithm type (based on the decompiled classes, it seems 10 and 11 are random walk
      while 12 is a shortest path algo)
    - `g.getPos(0)` is the ghost position (starting at 11,11)
    - `g.remainTimeAsEatable(0)` is the time remaining eatable (5ish seconds after eating power-up and then it counts
      down (with no lower limit))

#### Factors

1. Our algorithm must avoid colliding with ghosts at all costs.
2. Ideally, we want to finish the level ASAP (i.e. collect all the food and power-ups).
3. Eating ghosts (via power-ups) is a bonus.

> [!NOTE]
> The map is cyclic.

#### Logic

We will create the following 'modes':

1. Normal - path towards the closest food / power-up / eatable ghost (with `remainTimeAsEatable>2`)
2. Flee - path away from the ghosts

We will use BFS to implement normal mode, as follows:

1. Treat walls and tiles with "_close_" to (non-eatable) ghosts as obstacles. ("_Close_" meaning 'has a path distance
   less than `5`')
2. Find the shortest path to (in the following order) eatable ghost (with `remainTimeAsEatable>2`) > power-up > food

We will use a distance map to implement flee mode, all follows:

1. Using `allDistance`, create a "danger map" for each ghost
2. Combine all said danger maps (by taking the minimum value for each tile)
3. Move towards the least dangerous (reachable) tile

The algorithm defaults to normal mode and only switches to flee if:

- A (non-eatable) ghost has a path distance less than `3`
- Normal mode fails to produce a path

Once the condition for flee mode is no longer satisfied, we return to normal mode.
