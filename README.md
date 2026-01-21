# Pac-Man is Going Places

## Overview

This is the third assignment for the 'Intro to Computer Science' course at Ariel University (2026).

This project is divided into:

1. Client side - An algorithm that guides the pac-man to eat dots and avoid ghosts.
2. Server side - An engine that manages game logic, ghost AI, and GUI

## The algorithm

Each time the `move()` function is called, pac-man decided whether to move (`UP`,`DOWN`,`LEFT`,`RIGHT`) or not to move (
`STAY`). Each of his five choices are considered, and the best choice is executed.

### Step 1 - Build the danger map

First, a 'danger map' is built(/updated). Each cell in this 2d-array represents how close that map tile is from the nearest ghost.

### Step 2 - Choose a mode

Next, we determine which mode(s) the pac-man is in. There are:
- **Powered** (ghosts can be eaten)
- **Panic** (pac-man is _not_ powered-up _and_ a ghost is "nearby")
- **Normal** (a ghost is not nearby)

### Step 3 - Choose a direction

The pac-man evaluates each of the four directions, giving it a score (assuming it's a legal move, i.e. not a wall).

The scoring function works as follows:

#### Safety
If not powered up, moving towards a ghost is negative and moving away is positive.

The closer pac-man is to a ghost, the higher this coefficient is weighted.

#### Traps
We assume the given direction was chosen and then check how much open space remains.
Large areas are positive, small areas are negative.

This is always weighted quite high.

#### Food
Pac-man has two (sometimes three) choices of food. Dots and power-ups (and sometimes edible ghosts).

If reachable (i.e. nearby and a high `timeLeftEatable`), we score moving towards a ghost highly.

If ghosts are nearby (and pac-man is not powered up), we score moving towards power-ups highly.

Otherwise, we score moving towards dots highly.

#### Best route
Tiles already visited have a small penalty.

Also, moving 'backwards' (i.e. opposite to the previous direction taken) has a small penalty.

---

Once the score of each of the four directions is calculated, we consider the direction with the highest score.

If it's above a given threshold, we choose that direction.
Otherwise, we choose to stay still.

### Step 4 - Make a move
Before executing the move, we store the current tile in memory and record the chosen direction.

Finally, we execute the move (or lack thereof).

## Possible improvements

The danger map could be improved by exploring different algorithms for deciding the danger of a cell. Options include
minimum distance, cumulative distance, and product of distances.

# Demonstrations

## Algorithm
See our algorithm being demonstrated on the original game server.


## Game engine
A quick guide for how to use our game engine. Currently, only the (above) AI is playable.
