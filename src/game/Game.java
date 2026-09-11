package game;

import java.util.Scanner;

import model.Room;

/**
 * Console entry point.
 *
 * Run after compiling: java -cp out game.Game
 */
public class Game {
    private final GameSession session;
    private final Scanner scanner;

    public Game() {
        session = new GameSession();
        scanner = new Scanner(System.in);
    }

    public void start() {
        printTitleScreen();

        while (session.isRunning()) {
            describeCurrentRoom();
            System.out.print("\nCommand > ");

            if (!scanner.hasNextLine()) {
                break;
            }

            CommandResult result = session.processCommand(scanner.nextLine());
            for (String message : result.getMessages()) {
                System.out.println(message);
            }
        }

        System.out.println("\nThanks for playing MazeRunner.");
        scanner.close();
    }

    private void printTitleScreen() {
        System.out.println("==================================================");
        System.out.println("                 MAZERUNNER");
        System.out.println("          A BFS Graph Adventure Game");
        System.out.println("==================================================");
        System.out.println("Find the Brass Key, unlock the Vault, and escape.");
        System.out.println("Avoid " + session.getEnemy().getName() + ". Type 'help' for commands.");
    }

    private void describeCurrentRoom() {
        Room room = session.getPlayer().getCurrentRoom();
        System.out.println("\n--------------------------------------------------");
        System.out.println("Location: " + room.getName());
        System.out.println("Inventory: " + session.getPlayer().getInventorySummary());
        System.out.println("Threat: " + session.describeThreatLevel());
        System.out.println("--------------------------------------------------");
        System.out.println(room.getDescription());
        if (room.getItemHere() != null) {
            System.out.println("Item: " + room.getItemHere());
        }
        System.out.println("Exits: " + room.getExitSummary());
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
