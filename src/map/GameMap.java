package map;

import java.util.ArrayList;
import java.util.List;

import model.Room;

/**
 * GameMap owns all Rooms (nodes) and wires up the exits (edges) between them.
 * Keeping this separate from Room keeps "graph construction" cleanly apart
 * from "what a single node looks like" — good practice for readability.
 *
 * The map is intentionally small enough to explain in a mini-project demo,
 * but large enough to make BFS pathfinding feel meaningful.
 */
public class GameMap {
    private final List<Room> allRooms;
    private Room startRoom;
    private Room goalRoom;
    private Room enemyStartRoom;

    public GameMap() {
        allRooms = new ArrayList<>();
        buildSampleMap();
    }

    private void buildSampleMap() {
        Room foyer = new Room("Foyer", "A rain-soaked entryway with a cracked marble floor.");
        Room gallery = new Room("Gallery", "Portraits stare from the walls under cold museum lights.");
        Room study = new Room("Study", "A quiet room with maps, ledgers, and a brass key on the desk.");
        Room kitchen = new Room("Kitchen", "Copper pans hang above a stone counter. The back door is sealed.");
        Room conservatory = new Room("Conservatory", "Moonlight cuts through broken glass and overgrown vines.");
        Room armory = new Room("Armory", "Empty racks line the walls, but the room gives you a moment to think.");
        Room corridor = new Room("East Corridor", "A narrow corridor where every footstep echoes twice.");
        Room cellar = new Room("Cellar", "A cold storage room below the house. Something has been waiting here.");
        Room vault = new Room("Vault", "A reinforced chamber with the final exit hatch behind it.");

        // Wire up exits both ways (undirected graph)
        connect(foyer, "north", gallery);
        connect(gallery, "east", study);
        connect(gallery, "west", kitchen);
        connect(kitchen, "north", conservatory);
        connect(conservatory, "east", armory);
        connect(study, "north", corridor);
        connect(corridor, "west", armory);
        connect(corridor, "east", cellar);
        connect(cellar, "north", vault);

        study.setItemHere("Brass Key");
        vault.setLocked(true);

        allRooms.add(foyer);
        allRooms.add(gallery);
        allRooms.add(study);
        allRooms.add(kitchen);
        allRooms.add(conservatory);
        allRooms.add(armory);
        allRooms.add(corridor);
        allRooms.add(cellar);
        allRooms.add(vault);

        startRoom = foyer;
        goalRoom = vault;
        enemyStartRoom = kitchen;
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

    public Room getEnemyStartRoom() {
        return enemyStartRoom;
    }
}
