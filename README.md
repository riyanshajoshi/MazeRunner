# Escape the House — a graph-based text adventure

A text-adventure game where the map is literally a **graph**: rooms are
nodes, corridors are edges, and an enemy hunts you using **BFS shortest-path**
every turn. Built in pure Java — no external libraries or frameworks.

## Why this project
Most beginner projects (to-do apps, library systems) don't showcase
algorithmic thinking. This one puts a real graph algorithm (BFS) at the
center of the gameplay itself — the enemy's chase behavior *is* the
algorithm running live.

## How to run
```bash
cd src
javac *.java
java Game
```

Commands once running: `north`, `south`, `east`, `west`, `take`, `inventory`, `quit`.
Goal: reach the **Vault**. You'll need the **key** from the Library first.
Avoid **The Watcher** — it uses BFS to find the shortest path to you every turn.

## Project structure
- `Room.java` — a graph node. Holds its exits (adjacency list), lock state, and any item.
- `GameMap.java` — builds the graph: creates rooms and wires up edges (exits).
- `Pathfinder.java` — **the core DSA piece.** BFS shortest-path implementation, used by the Enemy.
- `Player.java` — tracks player position and inventory.
- `Enemy.java` — each turn, asks `Pathfinder` for the next step toward the player.
- `Game.java` — main loop: read input, update state, check win/lose.

## Ideas to extend this (roughly in order of difficulty)
1. **Bigger map** — add more rooms, branching paths, multiple keys/locked doors.
2. **Weighted edges** — some corridors take longer to cross (e.g. "narrow passage").
   Swap BFS for **Dijkstra** in `Pathfinder.java` to handle this correctly.
3. **A\* search** — add a heuristic (e.g. precomputed room "distance to goal")
   so the enemy pathfinds faster on a large map.
4. **Multiple enemies** — each with independent pathfinding.
5. **Save/load** — serialize game state to a file (light I/O practice).
6. **Simple GUI** — swap the console for a grid drawn with Java Swing,
   color-coding visited rooms, the player, and the enemy.
7. **Difficulty settings** — change enemy speed (moves every turn vs. every 2 turns),
   or give the enemy limited vision (only chase if within N rooms).

## What to say about this in an interview
- Explain *why* BFS guarantees shortest path in an unweighted graph.
- Explain the trade-off if you upgrade to Dijkstra/A* (when it's worth the complexity).
- Talk through the adjacency-list design (`Map<String, Room>` per room) vs.
  an adjacency matrix, and why adjacency list fits a sparse map better.
