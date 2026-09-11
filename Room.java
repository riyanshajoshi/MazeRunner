import java.util.HashMap;
import java.util.Map;

/**
 * A Room is a NODE in our graph.
 * The "exits" map is our adjacency list: direction -> connected Room.
 * This is the same graph structure you use in DSA (adjacency list),
 * just applied to a game map instead of a LeetCode problem.
 */
public class Room {
    private final String name;
    private final String description;
    private final Map<String, Room> exits;   // adjacency list: "north" -> Room
    private boolean locked;                  // example of a weighted/conditional edge
    private String itemHere;                 // optional item sitting in this room
    private boolean visited;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.locked = false;
        this.visited = false;
    }

    public void addExit(String direction, Room destination) {
        exits.put(direction.toLowerCase(), destination);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getItemHere() {
        return itemHere;
    }

    public void setItemHere(String itemHere) {
        this.itemHere = itemHere;
    }

    public boolean isVisited() {
        return visited;
    }

    public void markVisited() {
        this.visited = true;
    }

    @Override
    public String toString() {
        return name;
    }
}
