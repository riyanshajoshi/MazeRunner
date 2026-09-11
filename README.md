# MazeRunner

MazeRunner is a Java adventure game built around a graph map and Breadth-First Search (BFS) pathfinding.

The player explores a connected set of rooms, collects the Brass Key, unlocks the Vault, and escapes while The Watcher follows the shortest path through the map.

## Features

- Graph-based room map using an adjacency list
- Packaged Java source structure for cleaner compilation
- BFS shortest-path logic for enemy movement
- Locked Vault that requires the Brass Key
- Inventory, map, look, and movement commands
- Swing desktop interface with a room map, status panel, movement controls, and event log
- Polished console status display with location, inventory, exits, and threat level
- Deterministic room/exits output for easier demo presentation

## Algorithms And Concepts

### Graph Representation

Each room is a vertex in the graph. Each room exit is an edge to another room.

The graph is stored as an adjacency list:

```text
Room -> direction -> connected Room
```

### Breadth-First Search

The Watcher uses BFS to find the shortest path from its current room to the player's current room. BFS is appropriate because every edge in the map has the same movement cost.

For an unweighted graph:

- Time complexity: `O(V + E)`
- Space complexity: `O(V)`

Where `V` is the number of rooms and `E` is the number of exits/connections.

## Project Structure

```text
MazeRunner/
|-- README.md
|-- .gitignore
`-- src/
    |-- algorithm/
    |   `-- Pathfinder.java
    |-- game/
    |   |-- CommandResult.java
    |   |-- Game.java
    |   `-- GameSession.java
    |-- map/
    |   `-- GameMap.java
    |-- model/
    |   |-- Enemy.java
    |   |-- Player.java
    |   `-- Room.java
    `-- ui/
        `-- MazeRunnerUI.java
```

## How To Run

Compile:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

Run the Swing version:

```powershell
java -cp out ui.MazeRunnerUI
```

Run the console version:

```powershell
java -cp out game.Game
```

## Commands

| Command | Description |
|---|---|
| `north`, `south`, `east`, `west` | Move between rooms |
| `n`, `s`, `e`, `w` | Short movement aliases |
| `take` | Pick up the item in the current room |
| `inventory`, `inv`, `i` | Show collected items |
| `look`, `l` | Reprint the current room details |
| `map` | Show visited rooms |
| `help`, `h` | Show available commands |
| `quit`, `exit` | Exit the game |

## Objective

1. Explore the house.
2. Find and take the Brass Key.
3. Reach the locked Vault.
4. Escape before The Watcher catches you.
