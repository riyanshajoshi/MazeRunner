# 🧩 MazeRunner — Daily Algorithmic Adventure

**A daily maze adventure featuring BFS-powered enemy pathfinding, deterministic puzzles, and web-based gameplay.**

🌐 **[Play MazeRunner Daily](https://riyanshajoshi.github.io/MazeRunner/)**

MazeRunner is an algorithm-driven maze adventure where every day presents a new deterministic challenge. Explore the maze, find the **Brass Key**, avoid the **Watcher**, and reach the locked **Vault** before you run out of moves.

Unlike a traditional maze game, MazeRunner combines **game development with Data Structures and Algorithms**. The maze is represented as a graph, while the Watcher uses **Breadth-First Search (BFS)** to determine the shortest path toward the player.

---

## 🎮 Gameplay

Your objective is simple:

1. 🧭 Explore the maze.
2. 🔑 Find the **Brass Key**.
3. 👾 Avoid the **Watcher**.
4. 🔐 Use the key to unlock the **Vault**.
5. 🏆 Reach the Vault before you run out of moves.

The Watcher moves after every second successful player move and uses BFS to determine its next step toward the player.

### Controls

| Action      | Controls       |
| ----------- | -------------- |
| Move North  | `W` / `↑`      |
| Move South  | `S` / `↓`      |
| Move West   | `A` / `←`      |
| Move East   | `D` / `→`      |
| Pick up Key | In-game button |

The game can be played using either the keyboard or the on-screen controls.

---

## ✨ Features

* 🧩 **Daily Puzzle** — Every day has a deterministic puzzle configuration shared by all players.
* 👾 **BFS-Powered Watcher** — The enemy dynamically finds a shortest path toward the player.
* 🔑 **Key & Vault System** — Find the Brass Key before entering the locked Vault.
* 🎯 **Limited Moves** — Complete each daily challenge within the move limit.
* 📅 **Daily Puzzle Number** — Track which daily challenge you are playing.
* 📊 **Statistics & Streaks** — Track wins, losses, current streak, and best streak.
* 📤 **Shareable Results** — Share your result without revealing the solution.
* 💾 **Local Progress** — Daily progress and statistics are stored using browser `localStorage`.
* 🌐 **Web-Based Gameplay** — Play directly in your browser without installing anything.
* ☕ **Java Implementation** — Original Java game architecture and algorithmic implementation are preserved.
* 🚀 **GitHub Pages** — The web version is deployed automatically through GitHub Actions.

---

## 🧠 Data Structures & Algorithms

MazeRunner models the game environment as a **graph**.

```text
Room  → Vertex
Exit  → Edge
Watcher → BFS traversal
Player → Current graph position
```

### Breadth-First Search

The Watcher uses **BFS (Breadth-First Search)** to find the shortest path from its current room toward the player's room.

Simplified process:

```text
Watcher
   │
   ▼
Start BFS
   │
   ▼
Explore connected rooms
   │
   ▼
Find player's room
   │
   ▼
Reconstruct shortest path
   │
   ▼
Move one step toward player
```

### Why BFS?

The maze connections are represented as an unweighted graph. BFS is therefore suitable for finding a shortest path in terms of the number of room-to-room moves.

### Complexity

For a graph with `V` rooms and `E` connections:

| Operation              | Algorithm               | Time Complexity | Space Complexity |
| ---------------------- | ----------------------- | --------------: | ---------------: |
| Watcher pathfinding    | BFS                     |      `O(V + E)` |           `O(V)` |
| Daily puzzle selection | Deterministic selection |          `O(1)` |           `O(1)` |
| Player movement        | Graph lookup            |          `O(1)` |           `O(1)` |

---

## 📅 Daily Puzzle System

MazeRunner uses a deterministic daily puzzle system.

The current date is converted into a daily index, which determines the puzzle configuration.

The daily configuration can vary:

* 🔑 Brass Key location
* 👾 Watcher starting location
* 🎯 Move limit

Because the puzzle is generated deterministically from the date, players receive the **same challenge on the same day**.

The daily format was inspired by the idea of games such as Wordle having a shared daily challenge, but **MazeRunner is not a Wordle clone**. The gameplay remains a maze exploration and pathfinding game.

---

## 🏗️ Project Architecture

The project contains both the original Java implementation and the browser-based version.

```text
MazeRunner
│
├── src/
│   ├── algorithm/
│   │   └── Pathfinder.java
│   │
│   ├── game/
│   │   ├── CommandResult.java
│   │   ├── Game.java
│   │   └── GameSession.java
│   │
│   ├── map/
│   │   └── GameMap.java
│   │
│   ├── model/
│   │   ├── Enemy.java
│   │   ├── Player.java
│   │   └── Room.java
│   │
│   └── ui/
│       └── MazeRunnerUI.java
│
├── web/
│   ├── index.html
│   ├── style.css
│   └── script.js
│
├── .github/
│   └── workflows/
│       └── pages.yml
│
├── .nojekyll
└── README.md
```

---

## ☕ Java Implementation

The original MazeRunner implementation is written in Java using an object-oriented design.

Important components include:

### `Room`

Represents a room in the maze and stores its connections to other rooms.

### `Player`

Stores the player's current position and inventory.

### `Enemy`

Represents the Watcher and controls its movement.

### `GameMap`

Constructs the maze and connects the rooms.

### `GameSession`

Controls game state, player movement, item collection, win/loss conditions, and enemy turns.

### `Pathfinder`

Contains the BFS implementation used for shortest-path navigation.

---

## 🗺️ Maze Structure

The original maze contains nine rooms:

```text
Foyer
Gallery
Study
Kitchen
Conservatory
Armory
East Corridor
Cellar
Vault
```

The maze is represented as a graph where rooms are nodes and exits are edges.

The general layout includes connections such as:

```text
Foyer
  │
  ▼
Gallery ─── Study
  │          │
  ▼          ▼
Kitchen   East Corridor
  │          │
  ▼          ▼
Conservatory ── Armory ── Cellar
                         │
                         ▼
                        Vault
```

The exact daily configuration can change the key and Watcher positions while maintaining the underlying maze structure.

---

## 🌐 Web Version

The browser version is built using:

* **HTML** — Page structure
* **CSS** — Interface and responsive design
* **JavaScript** — Game logic and BFS pathfinding
* **localStorage** — Daily progress and statistics
* **GitHub Pages** — Hosting
* **GitHub Actions** — Automated deployment

The web version implements the core MazeRunner mechanics directly in JavaScript so the game can run entirely in the browser.

---

## 🚀 Running Locally

### Web Version

Clone the repository:

```bash
git clone https://github.com/riyanshajoshi/MazeRunner.git
```

Move into the project:

```bash
cd MazeRunner
```

Open:

```text
web/index.html
```

in a web browser.

For the best development experience, you can also use the **Live Server** extension in VS Code.

---

## ☕ Running the Java Version

Make sure Java is installed:

```bash
java -version
```

Compile the project:

```bash
javac -d out src/model/*.java src/map/*.java src/algorithm/*.java src/game/*.java src/ui/*.java
```

Then run the Java application using the appropriate main class from the UI implementation.

---

## 🔄 Continuous Deployment

MazeRunner uses **GitHub Actions** to automatically deploy the web version to GitHub Pages.

The deployment workflow is located at:

```text
.github/workflows/pages.yml
```

The deployment process is:

```text
Local Changes
     │
     ▼
Git Commit
     │
     ▼
Git Push
     │
     ▼
GitHub Repository
     │
     ▼
GitHub Actions
     │
     ▼
GitHub Pages
     │
     ▼
Live Website
```

This allows changes pushed to the `main` branch to be automatically deployed to the live game.

---

## 🛠️ Technologies Used

| Technology     | Purpose                           |
| -------------- | --------------------------------- |
| Java           | Original game implementation      |
| JavaScript     | Browser game engine               |
| HTML5          | Web structure                     |
| CSS3           | User interface                    |
| BFS            | Enemy shortest-path navigation    |
| Graphs         | Maze representation               |
| LocalStorage   | Player statistics and daily state |
| Git            | Version control                   |
| GitHub         | Source code hosting               |
| GitHub Actions | Continuous deployment             |
| GitHub Pages   | Web hosting                       |

---

## 🎯 Learning Outcomes

This project demonstrates practical understanding of:

* Graph representation
* Breadth-First Search
* Shortest-path algorithms
* Object-oriented programming
* Java development
* JavaScript game development
* Browser state management
* Deterministic algorithms
* Git and GitHub workflows
* Continuous deployment
* Web hosting

---

## 🔮 Future Improvements

Possible future enhancements include:

* 🗺️ Procedurally generated mazes
* 🧠 Additional enemy behaviors and pathfinding algorithms
* 🏆 Global leaderboards
* 👥 Multiplayer challenges
* 📈 More detailed player statistics
* 🎨 Additional maze themes
* 🔊 Sound effects and animations
* 📱 Further mobile optimization
* 🧪 Automated tests for game logic and pathfinding

---

## 📸 Screenshots

Screenshots of the game will be added here.

<!-- Add screenshots here, for example:

![MazeRunner Gameplay](screenshots/gameplay.png)

![MazeRunner Statistics](screenshots/statistics.png)

-->

---

## 🌐 Live Demo

### 🎮 [Play MazeRunner Daily](https://riyanshajoshi.github.io/MazeRunner/)

No installation required — open the link and play the daily challenge directly in your browser.

---

## 📂 Repository

**GitHub:** [github.com/riyanshajoshi/MazeRunner](https://github.com/riyanshajoshi/MazeRunner)

---

## 👩‍💻 Author

**Riyansha Joshi**

Computer Science Engineering Student

---

## 📄 License

This project is intended for educational and portfolio purposes.
