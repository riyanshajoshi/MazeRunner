package game;

import java.util.ArrayList;
import java.util.List;

import algorithm.Pathfinder;
import map.GameMap;
import model.Enemy;
import model.Player;
import model.Room;

public class GameSession {
    private final GameMap map;
    private final Player player;
    private final Enemy enemy;
    private boolean running;
    private int playerMoveCount;

    public GameSession() {
        map = new GameMap();
        player = new Player(map.getStartRoom());
        enemy = new Enemy("The Watcher", map.getEnemyStartRoom());
        running = true;
        playerMoveCount = 0;
    }

    public CommandResult processCommand(String rawInput) {
        if (!running) {
            return new CommandResult(List.of("The game is over. Start a new game to play again."),
                    CommandResult.Status.QUIT, false, false);
        }

        String input = rawInput == null ? "" : rawInput.trim().toLowerCase();
        Room previousRoom = player.getCurrentRoom();
        List<String> messages = new ArrayList<>();

        switch (input) {
            case "" -> messages.add("Enter a command, or type 'help'.");
            case "help", "h" -> messages.add(getHelpText());
            case "look", "l" -> messages.add(getRoomDetails());
            case "take" -> takeItem(messages);
            case "inventory", "inv", "i" -> messages.add("Inventory: " + player.getInventorySummary());
            case "map" -> messages.add(getVisitedMapText());
            case "quit", "exit" -> {
                running = false;
                messages.add("You step away from the house.");
                return new CommandResult(messages, CommandResult.Status.QUIT, false, false);
            }
            default -> movePlayer(input, messages);
        }

        boolean playerMoved = player.getCurrentRoom() != previousRoom;
        if (playerMoved) {
            playerMoveCount++;
        }
        CommandResult.Status status = getEndStatus();
        if (status != CommandResult.Status.CONTINUE) {
            running = false;
            addEndingMessage(messages, status);
            return new CommandResult(messages, status, playerMoved, false);
        }

        boolean enemyMoved = false;
        if (playerMoved) {
            enemyMoved = enemy.takeTurnToward(player.getCurrentRoom());
            if (enemyMoved) {
                messages.add(enemy.getName() + " is getting closer. Movement echoes near "
                        + enemy.getCurrentRoom().getName() + ".");
            }

            status = getEndStatus();
            if (status != CommandResult.Status.CONTINUE) {
                running = false;
                addEndingMessage(messages, status);
            }
        }

        return new CommandResult(messages, status, playerMoved, enemyMoved);
    }

    private void movePlayer(String input, List<String> messages) {
        String direction = normalizeDirection(input);
        Room destination = player.getCurrentRoom().getExit(direction);
        String blockedReason = player.getMoveBlockReason(destination);

        if (blockedReason != null) {
            messages.add(blockedReason);
            return;
        }

        player.enter(destination);
        messages.add("You move " + direction + " to the " + destination.getName() + ".");
    }

    private void takeItem(List<String> messages) {
        String item = player.takeItemHere();
        if (item == null) {
            messages.add("There is nothing here to pick up.");
            return;
        }
        messages.add("You picked up: " + item + ".");
    }

    private CommandResult.Status getEndStatus() {
        if (player.getCurrentRoom() == map.getGoalRoom()) {
            return CommandResult.Status.WIN;
        }
        if (enemy.hasCaughtPlayer(player.getCurrentRoom())) {
            return CommandResult.Status.LOSS;
        }
        return CommandResult.Status.CONTINUE;
    }

    private void addEndingMessage(List<String> messages, CommandResult.Status status) {
        if (status == CommandResult.Status.WIN) {
            messages.add("You unlock the Vault, slip through the exit hatch, and escape. YOU WIN!");
        } else if (status == CommandResult.Status.LOSS) {
            messages.add("The Watcher found you. GAME OVER.");
        }
    }

    private String normalizeDirection(String input) {
        return switch (input) {
            case "n" -> "north";
            case "s" -> "south";
            case "e" -> "east";
            case "w" -> "west";
            default -> input;
        };
    }

    public String getRoomDetails() {
        Room room = player.getCurrentRoom();
        StringBuilder details = new StringBuilder();
        details.append(room.getName()).append("\n");
        details.append(room.getDescription()).append("\n");
        if (room.getItemHere() != null) {
            details.append("Item: ").append(room.getItemHere()).append("\n");
        }
        details.append("Exits: ").append(room.getExitSummary());
        return details.toString();
    }

    public String getVisitedMapText() {
        StringBuilder visited = new StringBuilder("Visited Rooms");
        for (Room room : map.getAllRooms()) {
            if (room.isVisited()) {
                visited.append("\n- ").append(room.getName()).append(" (").append(room.getExitSummary()).append(")");
            }
        }
        return visited.toString();
    }

    public String getHelpText() {
        return "Commands:\n"
                + "north/south/east/west or n/s/e/w - Move between rooms\n"
                + "take - Pick up the item in the room\n"
                + "inventory or i - View your inventory\n"
                + "look or l - Reprint current room details\n"
                + "map - Show visited rooms\n"
                + "quit - Exit the game";
    }

    public String describeThreatLevel() {
        int distance = getEnemyDistance();
        if (distance <= 0) {
            return "Caught";
        }
        if (distance == 1) {
            return "Critical - one room away";
        }
        if (distance == 2) {
            return "High - footsteps are close";
        }
        return "Low - distant movement";
    }

    public int getEnemyDistance() {
        return Pathfinder.bfsShortestPath(enemy.getCurrentRoom(), player.getCurrentRoom()).size() - 1;
    }

    public GameMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public boolean isRunning() {
        return running;
    }

    public int getPlayerMoveCount() {
        return playerMoveCount;
    }
}
