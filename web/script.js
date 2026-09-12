/*
 * MazeRunner Daily
 * ----------------
 * Browser version of the original Java game.
 * Rooms are graph nodes, doors are graph edges, and The Watcher uses BFS
 * to take a shortest-path step toward the player every second player move.
 */

const ROOMS = {
  Foyer: { description: "A rain-soaked entryway with a cracked marble floor.", exits: { north: "Gallery" } },
  Gallery: { description: "Portraits stare from the walls under cold museum lights.", exits: { south: "Foyer", east: "Study", west: "Kitchen" } },
  Study: { description: "A quiet room with maps, ledgers, and a desk where the Brass Key can be found.", exits: { south: "Gallery", north: "East Corridor" } },
  Kitchen: { description: "Copper pans hang above a stone counter. The back door is sealed.", exits: { east: "Gallery", north: "Conservatory" } },
  Conservatory: { description: "Moonlight cuts through broken glass and overgrown vines.", exits: { south: "Kitchen", east: "Armory" } },
  Armory: { description: "Empty racks line the walls, but the room gives you a moment to think.", exits: { west: "Conservatory", east: "East Corridor" } },
  "East Corridor": { description: "A narrow corridor where every footstep echoes twice.", exits: { south: "Study", west: "Armory", east: "Cellar" } },
  Cellar: { description: "A cold storage room below the house. Something has been waiting here.", exits: { west: "East Corridor", north: "Vault" } },
  Vault: { description: "A reinforced chamber with the final exit hatch behind it.", exits: { south: "Cellar" } }
};

const ROOM_ORDER = ["Foyer", "Gallery", "Study", "Kitchen", "Conservatory", "Armory", "East Corridor", "Cellar", "Vault"];
const START_ROOM = "Foyer";
const GOAL_ROOM = "Vault";
const DAILY_START = Date.UTC(2026, 0, 1);
const STORAGE_KEY = "mazerunner-daily-v2";
const DEFAULT_LIMIT = 15;
const KEY_ROOMS = ["Study", "Kitchen", "Conservatory", "Armory", "Gallery"];
const ENEMY_ROOMS = ["Kitchen", "Conservatory", "Armory", "Study", "Cellar"];
const POSITIONS = {
  Foyer: [8, 72], Gallery: [36, 50], Study: [63, 29], Kitchen: [8, 30],
  Conservatory: [36, 10], Armory: [63, 10], "East Corridor": [63, 52],
  Cellar: [82, 52], Vault: [82, 24]
};

const today = new Date();
const dateKey = getDateKey(today);
const dayNumber = dayIndex(today) + 1;
const puzzle = createDailyPuzzle(dayIndex(today));
let state = loadState();
let gameOver = state.completedDate === dateKey;

const els = {
  map: document.getElementById("map"), message: document.getElementById("message"),
  dateLabel: document.getElementById("dateLabel"), dailyLabel: document.getElementById("dailyLabel"),
  countdown: document.getElementById("countdown"), roomName: document.getElementById("roomName"),
  roomDescription: document.getElementById("roomDescription"), roomItem: document.getElementById("roomItem"),
  inventory: document.getElementById("inventory"), moves: document.getElementById("moves"), limit: document.getElementById("limit"),
  threatBadge: document.getElementById("threatBadge"), threatText: document.getElementById("threatText"),
  threatDetail: document.getElementById("threatDetail"), distance: document.getElementById("distance"),
  threatMeter: document.getElementById("threatMeter"), log: document.getElementById("log"), takeBtn: document.getElementById("takeBtn")
};

init();

function init() {
  els.dailyLabel.textContent = `MAZERUNNER DAILY #${dayNumber}`;
  els.dateLabel.textContent = today.toLocaleDateString(undefined, { weekday: "short", month: "short", day: "numeric" });
  els.limit.textContent = puzzle.moveLimit;
  if (!state.inventory) state.inventory = [];
  if (!state.log) state.log = [];
  if (!state.playerRoom) state.playerRoom = START_ROOM;
  if (!Number.isInteger(state.enemyRoomIndex)) state.enemyRoomIndex = 0;
  renderMap();
  renderAll();
  setInterval(updateCountdown, 1000);
  updateCountdown();
  document.addEventListener("keydown", handleKeyboard);
}

function createDailyPuzzle(index) {
  // Deterministic daily variations. Everyone gets the same challenge for a date.
  const keyRoom = KEY_ROOMS[index % KEY_ROOMS.length];
  const enemyRoom = ENEMY_ROOMS[(index * 2 + 1) % ENEMY_ROOMS.length];
  const limit = DEFAULT_LIMIT - (index % 3 === 0 ? 1 : 0);
  return { keyRoom, enemyRoom, moveLimit: limit };
}

function resetGame() {
  if (!confirm("Restart today's maze? Your result for today will be reset.")) return;
  state = freshState();
  gameOver = false;
  saveState();
  addLog("You step back into the Foyer. The house is waiting.", true);
  renderAll();
}

function freshState() {
  return { date: dateKey, playerRoom: START_ROOM, enemyRoom: puzzle.enemyRoom, inventory: [], moves: 0, log: [], completedDate: null, won: false };
}

function loadState() {
  const base = freshState();
  try {
    const raw = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (!raw) return base;
    // Keep lifetime statistics even when the daily puzzle changes.
    if (raw.date !== dateKey) return { ...base, stats: raw.stats || emptyStats() };
    return { ...base, ...raw, inventory: Array.isArray(raw.inventory) ? raw.inventory : [], log: Array.isArray(raw.log) ? raw.log : [], stats: raw.stats || emptyStats() };
  } catch (_) { return base; }
}

function saveState() { localStorage.setItem(STORAGE_KEY, JSON.stringify(state)); }

function move(direction) {
  if (gameOver) return;
  const room = ROOMS[state.playerRoom];
  const destination = room.exits[direction];
  if (!destination) return showMessage("You can't go that way.");

  if (destination === GOAL_ROOM && !hasKey()) {
    addLog("The Vault door is locked. You need the Brass Key.");
    return showMessage("The Vault is locked — find the Brass Key first.");
  }

  state.playerRoom = destination;
  state.moves += 1;
  addLog(`You move <strong>${direction}</strong> to the ${destination}.`);

  if (state.playerRoom === GOAL_ROOM) return finishGame(true);

  // Matches the Java game: The Watcher moves every second player move.
  if (state.moves % 2 === 0) {
    const moved = moveWatcherBFS();
    if (moved) addLog(`<strong>The Watcher moves.</strong> You hear movement near the ${state.enemyRoom}.`);
  }

  if (state.enemyRoom === state.playerRoom) return finishGame(false);
  if (state.moves >= puzzle.moveLimit) return finishGame(false, "You ran out of moves before reaching the Vault.");

  saveState();
  renderAll();
}

function takeItem() {
  if (gameOver) return;
  if (state.playerRoom !== puzzle.keyRoom) {
    return showMessage("There is nothing useful to take here.");
  }
  if (hasKey()) return showMessage("You already have the Brass Key.");
  state.inventory.push("Brass Key");
  addLog("You picked up the <strong>Brass Key</strong>. The Vault can now be opened.");
  saveState();
  renderAll();
}

function hasKey() { return state.inventory.some(item => item.toLowerCase() === "brass key"); }

function moveWatcherBFS() {
  const path = bfs(state.enemyRoom, state.playerRoom);
  if (path.length < 2) return false;
  state.enemyRoom = path[1];
  return true;
}

function bfs(start, goal) {
  if (start === goal) return [start];
  const queue = [start];
  const previous = new Map([[start, null]]);
  while (queue.length) {
    const current = queue.shift();
    for (const next of Object.values(ROOMS[current].exits)) {
      if (previous.has(next)) continue;
      previous.set(next, current);
      if (next === goal) {
        const path = [goal];
        let node = goal;
        while (previous.get(node) !== null) { node = previous.get(node); path.push(node); }
        return path.reverse();
      }
      queue.push(next);
    }
  }
  return [];
}

function finishGame(won, reason = "The Watcher found you.") {
  gameOver = true;
  state.completedDate = dateKey;
  state.won = won;
  state.lastResultMoves = state.moves;
  state.stats = updateStats(state.stats || emptyStats(), won, state.moves);
  if (won) addLog(`<strong>YOU ESCAPED!</strong> The Vault opens and you slip through the exit hatch.`);
  else addLog(`<strong>GAME OVER.</strong> ${reason}`);
  saveState();
  renderAll();
  setTimeout(() => openStats(), 250);
}

function updateStats(stats, won, moves) {
  const s = { ...emptyStats(), ...stats };
  s.played += 1;
  if (won) {
    s.wins += 1;
    s.currentStreak = s.lastPlayedDate === yesterdayKey() ? s.currentStreak + 1 : 1;
    s.bestStreak = Math.max(s.bestStreak, s.currentStreak);
    s.moves[moves] = (s.moves[moves] || 0) + 1;
  } else s.currentStreak = 0;
  s.lastPlayedDate = dateKey;
  return s;
}
function emptyStats() { return { played: 0, wins: 0, currentStreak: 0, bestStreak: 0, lastPlayedDate: null, moves: {} }; }

function renderAll() {
  renderMap(); renderRoom(); renderThreat(); renderStats(); renderLog();
  els.moves.textContent = state.moves;
  els.takeBtn.disabled = gameOver || state.playerRoom !== puzzle.keyRoom || hasKey();
}

function renderMap() {
  els.map.innerHTML = "";
  const drawn = new Set();
  for (const from of ROOM_ORDER) {
    for (const [dir, to] of Object.entries(ROOMS[from].exits)) {
      const id = [from, to].sort().join("|");
      if (drawn.has(id)) continue;
      drawn.add(id);
      const a = POSITIONS[from], b = POSITIONS[to];
      const x1 = a[0] + 7, y1 = a[1] + 7, x2 = b[0] + 7, y2 = b[1] + 7;
      const dx = x2 - x1, dy = y2 - y1;
      const line = document.createElement("div");
      line.className = "edge";
      line.style.left = `${x1}%`; line.style.top = `${y1}%`;
      line.style.width = `${Math.sqrt(dx * dx + dy * dy)}%`;
      line.style.transform = `rotate(${Math.atan2(dy, dx) * 180 / Math.PI}deg)`;
      els.map.appendChild(line);
    }
  }
  ROOM_ORDER.forEach(name => {
    const room = document.createElement("div");
    room.className = "room";
    if (state.playerRoom === name) room.classList.add("current");
    if (state.enemyRoom === name) room.classList.add("enemy");
    room.style.left = `${POSITIONS[name][0]}%`; room.style.top = `${POSITIONS[name][1]}%`;
    const visited = name === state.playerRoom || state.log.some(entry => entry.includes(`to the ${name}`));
    if (visited) room.classList.add("visited");
    const markers = [];
    if (state.playerRoom === name) markers.push('<span class="marker player">YOU</span>');
    if (state.enemyRoom === name) markers.push('<span class="marker enemy">WATCHER</span>');
    if (name === puzzle.keyRoom && !hasKey()) markers.push('<span class="marker key">KEY</span>');
    if (name === GOAL_ROOM) markers.push('<span class="marker vault">VAULT</span>');
    room.innerHTML = `<div class="room-name">${name}</div><div class="room-desc">${ROOMS[name].description}</div><div class="room-marker">${markers.join("")}</div>`;
    els.map.appendChild(room);
  });
}

function renderRoom() {
  const room = ROOMS[state.playerRoom];
  els.roomName.textContent = state.playerRoom;
  els.roomDescription.textContent = room.description;
  if (state.playerRoom === puzzle.keyRoom && !hasKey()) {
    els.roomItem.textContent = "🔑 Brass Key — take it to unlock the Vault.";
    els.roomItem.classList.remove("hidden");
  } else els.roomItem.classList.add("hidden");
  els.inventory.innerHTML = hasKey() ? '<span class="has-key">🔑 Brass Key</span>' : "Empty";
}

function renderThreat() {
  const distance = bfs(state.enemyRoom, state.playerRoom).length - 1;
  els.distance.textContent = distance < 0 ? "?" : distance;
  let level = "LOW", title = "Distant movement", detail = "The house is quiet... for now.", pct = 20;
  if (distance <= 0) { level = "CRITICAL"; title = "Caught"; detail = "The Watcher is here."; pct = 100; }
  else if (distance === 1) { level = "CRITICAL"; title = "One room away"; detail = "You can hear footsteps nearby."; pct = 90; }
  else if (distance === 2) { level = "HIGH"; title = "Footsteps are close"; detail = "The Watcher is closing in."; pct = 65; }
  else if (distance === 3) { level = "MEDIUM"; title = "Something is following you"; detail = "Movement echoes through the house."; pct = 45; }
  els.threatBadge.className = `threat ${level.toLowerCase()}`; els.threatBadge.textContent = `${level} THREAT`;
  els.threatText.textContent = title; els.threatDetail.textContent = detail; els.threatMeter.style.width = `${pct}%`;
  els.threatMeter.style.background = level === "CRITICAL" ? "var(--red)" : level === "HIGH" ? "var(--gold)" : "var(--green)";
}

function renderLog() {
  if (!state.log.length) { els.log.innerHTML = '<div class="log-entry">The front door closes behind you. Find the Brass Key.</div>'; return; }
  els.log.innerHTML = state.log.slice(-12).reverse().map(entry => `<div class="log-entry">${entry}</div>`).join("");
}
function addLog(text, persist = true) { state.log.push(text); if (persist) saveState(); renderLog(); }
function showMessage(text) { els.message.textContent = text; clearTimeout(showMessage.timer); showMessage.timer = setTimeout(() => { if (!gameOver) els.message.textContent = ""; }, 2200); }

function updateCountdown() {
  const now = new Date(); const next = new Date(now); next.setHours(24, 0, 0, 0);
  const diff = next - now; const h = String(Math.floor(diff / 3600000)).padStart(2, "0");
  const m = String(Math.floor((diff % 3600000) / 60000)).padStart(2, "0"); const s = String(Math.floor((diff % 60000) / 1000)).padStart(2, "0");
  els.countdown.textContent = `Next maze ${h}:${m}:${s}`;
}

function handleKeyboard(e) {
  const key = e.key.toLowerCase();
  if (key === "arrowup" || key === "w") move("north");
  else if (key === "arrowdown" || key === "s") move("south");
  else if (key === "arrowleft" || key === "a") move("west");
  else if (key === "arrowright" || key === "d") move("east");
}

function getDateKey(d) { return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`; }
function dayIndex(d) { const utc = Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()); return Math.max(0, Math.floor((utc - DAILY_START) / 86400000)); }
function yesterdayKey() { const d = new Date(); d.setDate(d.getDate() - 1); return getDateKey(d); }

function renderStats() {
  const stats = { ...emptyStats(), ...(state.stats || {}) };
  document.getElementById("played").textContent = stats.played;
  document.getElementById("winRate").textContent = stats.played ? Math.round(stats.wins / stats.played * 100) : 0;
  document.getElementById("streak").textContent = stats.currentStreak;
  document.getElementById("bestStreak").textContent = stats.bestStreak;
  const area = document.getElementById("resultArea");
  if (gameOver) { area.classList.remove("hidden"); document.getElementById("resultText").textContent = state.won ? `MazeRunner Daily #${dayNumber} · escaped in ${state.moves} moves.` : `MazeRunner Daily #${dayNumber} · The Watcher caught you.`; }
  else area.classList.add("hidden");
}

function openStats() { renderStats(); document.getElementById("statsModal").showModal(); }
function shareResult() {
  if (!gameOver) return;
  const icon = state.won ? "🟩" : "🟥";
  const trail = `${icon.repeat(Math.min(state.moves, 15))}`;
  const text = `MazeRunner Daily #${dayNumber}\n${state.won ? `Escaped in ${state.moves} moves.` : "The Watcher got me."}\n${trail}\nCan you escape?`;
  if (navigator.share) navigator.share({ title: `MazeRunner Daily #${dayNumber}`, text }).catch(() => {});
  else if (navigator.clipboard) navigator.clipboard.writeText(text).then(() => showMessage("Result copied — share it with your friends!"));
  else showMessage(text);
}

document.querySelectorAll("[data-direction]").forEach(btn => btn.addEventListener("click", () => move(btn.dataset.direction)));
els.takeBtn.addEventListener("click", takeItem);
document.getElementById("resetBtn").addEventListener("click", resetGame);
document.getElementById("helpBtn").addEventListener("click", () => document.getElementById("helpModal").showModal());
document.getElementById("statsBtn").addEventListener("click", openStats);
document.getElementById("shareBtn").addEventListener("click", shareResult);
document.querySelectorAll("[data-close]").forEach(btn => btn.addEventListener("click", () => document.getElementById(btn.dataset.close).close()));
