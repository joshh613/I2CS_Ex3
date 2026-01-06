# Pac-Man is Going Places

## Overview

This is the third assignment for the 'Intro to Computer Science' course at Ariel University (2026).

This project is divided into:

1. Client side - An algorithm that guides the pac-man to eat dots and avoid ghosts.
2. Server side - An engine that manages game logic, ghost AI, and GUI

### The algorithm

#### Data

We have access to:

- `int[][] board = game.getGame(0)` the game board.
    - `0`/`black` is empty
    - `1`/`blue` is wall
    - `3`/`pink` is food
    - `5`/`green` is power-up
- `game.isCyclic()` boolean whether is board is cyclic
- `String pos = game.getPos(0)` pac-man's position as a string (`x,y`)
- `GhostCL[] ghosts = game.getGhosts(0)` array of ghost data. For each ghost `g` we have:
    - `g.getStatus()` whether the ghost is eatable
    - `g.getType()` the ghosts algorithm type (based on the decompiled classes, apparently 10/11 are random walk
      while 12 is shortest path)
    - `g.getPos(0)` the ghost position (starting at 11,11)
    - `g.remainTimeAsEatable(0)` the time remaining eatable (5ish seconds after eating power-up, and then it counts
      down (with no lower limit))
- `game.getData(0)` contains 1. time, 2. score, 3. steps, 4. kills, 5. pos, 6. dots

#### Factors

An optimal algorithm would perform as follows:

1. Avoid colliding with (non edible) ghosts at all costs.
    1. Create a buffer between pacman and the ghosts (larger for type 12 ghosts).
    2. Make sure that pacman won't get trapped between a ghost and a wall/ghost.
2. If both ghosts and power-ups are nearby, try to get the power-up and eat the ghosts
3. Otherwise, route the (cyclic) map and collect all the food

#### Logic

Draft 2:

First, consider the distances to each ghost and power-up.

- If there is a reachable and eatable ghosts, try path towards them (in order of proximity)
- If there is a power-up nearby and a ghost nearby, try path towards it
- Otherwise, consider all ghosts as obstacles (plus a buffer around them)

Next, find a path towards food. Slightly penalise paths that require turning around.
