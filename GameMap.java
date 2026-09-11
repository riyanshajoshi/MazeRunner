import java.util.ArrayList;
import java.util.List;

/**
 * GameMap owns all Rooms (nodes) and wires up the exits (edges) between them.
 * Keeping this separate from Room keeps "graph construction" cleanly apart
 * from "what a single node looks like" — good practice for readability.
 *
 * This is a placeholder layout. Replace with your own campus/dungeon theme.
 */
public class GameMap {
    private final List<Room> allRooms;
    private Room startRoom;
    private Room goalRoom;

    public GameMap() {
        allRooms = new ArrayList<>();
        buildSampleMap();
    }

    private void buildSampleMap() {
        Room entrance = new Room("Entrance", "A dusty entrance hall. Cold air seeps in.");
        Room hallway = new Room("Hallway", "A long hallway lined with flickering lights.");
        Room library = new Room("Library", "Shelves of old books. Something glints on a shelf.");
        Room kitchen = new Room("Kitchen", "Pots and pans, untouched for years.");
        Room vault = new Room("Vault", "A locked vault. This is the exit.");

        // Wire up exits both ways (undirected graph)
        connect(entrance, "north", hallway);
        connect(hallway, "east", library);
        connect(hallway, "west", kitchen);
        connect(library, "north", vault);

        library.setItemHere("key");
        vault.setLocked(true); // needs the key from library, add that logic in Game.java

        allRooms.add(entrance);
        allRooms.add(hallway);
        allRooms.add(library);
        allRooms.add(kitchen);
        allRooms.add(vault);

        startRoom = entrance;
        goalRoom = vault;
    }

    // Adds the edge in both directions so movement works both ways.
    // (For a directed graph / one-way passages, just call addExit once.)
    private void connect(Room a, String directionFromA, Room b) {
        a.addExit(directionFromA, b);
        b.addExit(opposite(directionFromA), a);
    }

    private String opposite(String direction) {
        return switch (direction.toLowerCase()) {
            case "north" -> "south";
            case "south" -> "north";
            case "east" -> "west";
            case "west" -> "east";
            default -> "back";
        };
    }

    public List<Room> getAllRooms() {
        return allRooms;
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public Room getGoalRoom() {
        return goalRoom;
    }
}
