# MazeRunner

A graph-based Java adventure game using **Breadth-First Search (BFS)** for pathfinding.

## 🎮 About the Game

MazeRunner is a console-based adventure game where the player explores a connected map of rooms, collects items, unlocks areas, and tries to reach the final destination while avoiding an enemy.

The game map is represented as a **graph**, where each room is a node and each connection between rooms is an edge.

## ✨ Features

- 🗺️ Graph-based game map
- 🧭 Player movement between connected rooms
- 🔑 Item collection and locked rooms
- 👾 Enemy that follows the player
- 🧠 BFS-based shortest pathfinding
- 🎯 Goal-based gameplay
- 💻 Console-based Java interface

## 🧠 Algorithms & Concepts

### Graph Representation

The game world is represented using an **adjacency list**.

- Each room represents a vertex.
- Each exit represents an edge.
- The map contains connected rooms that the player can explore.

### Breadth-First Search (BFS)

BFS is used by the enemy to find the shortest path toward the player.

For an unweighted graph:

**Time Complexity:** `O(V + E)`

**Space Complexity:** `O(V)`

where:

- `V` = number of rooms
- `E` = number of connections between rooms

### Object-Oriented Programming

The project uses Java classes to separate different parts of the game:

- `Room` — represents a room/node
- `GameMap` — creates the game map
- `Player` — handles player movement and inventory
- `Enemy` — handles enemy behavior
- `Pathfinder` — implements BFS pathfinding
- `Game` — controls the main game loop

## 🕹️ How to Play

The player can use the following commands:

| Command | Description |
|---|---|
| `north` | Move north |
| `south` | Move south |
| `east` | Move east |
| `west` | Move west |
| `take` | Pick up an available item |
| `inventory` | View collected items |
| `help` | Show available commands |
| `quit` | Exit the game |

### Objective

1. Explore the rooms.
2. Find the key.
3. Use the key to access the locked area.
4. Reach the Vault.
5. Avoid being caught by the Watcher.

## 🛠️ Technologies

- Java
- Java Collections Framework
- Object-Oriented Programming
- Graphs
- Breadth-First Search (BFS)
- Command Line Interface

## 📁 Project Structure

```text
MazeRunner/
│
├── Game.java
├── GameMap.java
├── Room.java
├── Player.java
├── Enemy.java
├── Pathfinder.java
├── README.md
└── .gitignore