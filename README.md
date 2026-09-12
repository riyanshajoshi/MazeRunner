# MazeRunner

**MazeRunner** is a graph-based adventure game where the player explores a house, finds the **Brass Key**, unlocks the **Vault**, and tries to escape **The Watcher**.

The project now has two versions:

1. **Java desktop version** — the original console/GUI implementation and the DSA-focused BFS pathfinding implementation.
2. **MazeRunner Daily web version** — a browser game inspired by the *daily puzzle* format of games such as Wordle, while keeping MazeRunner's original gameplay.

## 🎮 MazeRunner Daily

Every calendar day, everyone receives the same deterministic challenge.

The objective is still the original MazeRunner objective:

> **Find the Brass Key → avoid The Watcher → reach the locked Vault → escape.**

The daily layer adds:

- One shared puzzle per day
- Daily puzzle number
- Limited moves
- Daily variations in the Brass Key and Watcher starting locations
- Countdown to the next maze
- Browser-saved progress
- Win/loss statistics and streaks
- Spoiler-free result sharing
- Responsive desktop/mobile UI
- Keyboard controls using **W/A/S/D** or arrow keys

This is **not a 5-letter Wordle clone**. The original MazeRunner game is the puzzle.

## 🧠 DSA: BFS

The house is represented as an **undirected graph**:

- Each room is a **vertex/node**.
- Each doorway is an **edge**.
- The Watcher uses **Breadth-First Search (BFS)** to find the shortest path to the player.
- The Watcher moves every second successful player move, matching the Java game.

For an unweighted graph, BFS gives a shortest path in:

- **Time:** `O(V + E)`
- **Space:** `O(V)`

where `V` is the number of rooms and `E` is the number of connections.

## 🗺️ Original Map

The web version preserves the Java game's core map:

```text
                    [Vault]
                       |
                    [Cellar]
                       |
                 [East Corridor]
                  /            \
              [Armory]       [Study]
                 |               |
          [Conservatory]      [Gallery]
                 |             /     \
              [Kitchen] ------       [Foyer]
```

The daily challenge changes the **key location** and **Watcher starting location** deterministically from the date, while the graph and core rules remain recognizable and fair.

## ▶️ Run the web game locally

No Java installation is needed for the web version.

1. Open the `web` folder.
2. Double-click `index.html`, or serve the project with a local web server.
3. Play the daily maze in your browser.

For example, with Python installed:

```bash
cd web
python -m http.server 8000
```

Then open `http://localhost:8000`.

## ☕ Run the Java version

From the project root:

```bash
javac -d out src/model/*.java src/map/*.java src/algorithm/*.java src/game/*.java src/ui/*.java
```

Run the console version:

```bash
java -cp out game.Game
```

Run the GUI version:

```bash
java -cp out ui.MazeRunnerUI
```

## 🌐 GitHub Pages

The repository includes `.github/workflows/pages.yml`, which deploys the `web/` folder to GitHub Pages.

After pushing the project to GitHub:

1. Open the repository's **Settings**.
2. Open **Pages**.
3. Set the source to **GitHub Actions** if it is not already selected.
4. Push to the `main` branch.
5. GitHub Actions will build/deploy the website.

## 📁 Project structure

```text
MazeRunner/
├── src/
│   ├── algorithm/             # BFS pathfinding
│   ├── game/                  # Java game/session logic
│   ├── map/                   # Graph construction
│   ├── model/                 # Room, Player, Enemy
│   └── ui/                    # Java GUI
├── web/
│   ├── index.html             # Daily game interface
│   ├── style.css              # MazeRunner visual design
│   └── script.js              # Web game + BFS + daily puzzle logic
├── .github/workflows/
│   └── pages.yml              # GitHub Pages deployment
├── .nojekyll
└── README.md
```

## ✨ Why the web version is different

The web version is designed as a **daily MazeRunner challenge**, not as a replacement Wordle clone. Wordle provides the daily-puzzle idea; MazeRunner supplies the actual gameplay, graph, BFS enemy, key, Vault, and escape mechanics.

## 👩‍💻 Project

**MazeRunner — A BFS Graph Adventure Game**

Built as a data-structures/algorithms project and extended into a daily browser game.
