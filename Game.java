import java.util.Scanner;

/**
 * Main game loop. Text-adventure style:
 *  - print current room
 *  - read a command
 *  - update world state (player move, enemy chases via BFS)
 *  - check win/lose conditions
 *
 * Run: after compiling, `java Game`
 */
public class Game {
    private final GameMap map;
    private final Player player;
    private final Enemy enemy;
    private final Scanner scanner;
    private boolean running;

    public Game() {
        map = new GameMap();
        player = new Player(map.getStartRoom());
        // Spawn the enemy in a room away from the player's start (not the goal room).
        Room enemyStart = map.getAllRooms().get(map.getAllRooms().size() - 2);
        enemy = new Enemy("The Watcher", enemyStart);
        scanner = new Scanner(System.in);
        running = true;
    }

    public void start() {
        System.out.println("=== ESCAPE THE HOUSE ===");
        System.out.println("Reach the Vault to win. Avoid The Watcher. Type 'help' for commands.\n");

        while (running) {
            describeCurrentRoom();
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            handleCommand(input);

            if (!running) break;

            enemy.takeTurnToward(player.getCurrentRoom());
            checkEndConditions();
        }

        scanner.close();
    }

    private void describeCurrentRoom() {
        Room room = player.getCurrentRoom();
        System.out.println("\n-- " + room.getName() + " --");
        System.out.println(room.getDescription());
        if (room.getItemHere() != null) {
            System.out.println("You notice a(n) " + room.getItemHere() + " here.");
        }
        System.out.println("Exits: " + room.getExits().keySet());
    }

    private void handleCommand(String input) {
        switch (input) {
            case "help" -> System.out.println("Commands: north/south/east/west, take, inventory, quit");
            case "take" -> player.pickUpItemHere();
            case "inventory" -> System.out.println("You are carrying: " + player.getInventory());
            case "quit" -> running = false;
            default -> {
                Room destination = player.getCurrentRoom().getExit(input);
                player.moveTo(destination);
            }
        }
    }

    private void checkEndConditions() {
        if (enemy.hasCaughtPlayer(player.getCurrentRoom())) {
            System.out.println("\nThe Watcher found you. GAME OVER.");
            running = false;
        } else if (player.getCurrentRoom() == map.getGoalRoom()) {
            // Reaching this room at all means moveTo() already verified the key,
            // so no need to re-check isLocked() here.
            System.out.println("\nYou reached the Vault and escaped. YOU WIN!");
            running = false;
        }
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
