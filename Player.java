import java.util.HashSet;
import java.util.Set;

public class Player {
    private Room currentRoom;
    private final Set<String> inventory;
    private boolean alive;

    public Player(Room startRoom) {
        this.currentRoom = startRoom;
        this.inventory = new HashSet<>();
        this.alive = true;
        currentRoom.markVisited();
    }

    public boolean moveTo(Room destination) {
        if (destination == null) {
            System.out.println("You can't go that way.");
            return false;
        }
        if (destination.isLocked() && !inventory.contains("key")) {
            System.out.println("That door is locked. You need a key.");
            return false;
        }
        currentRoom = destination;
        currentRoom.markVisited();
        return true;
    }

    public void pickUpItemHere() {
        String item = currentRoom.getItemHere();
        if (item == null) {
            System.out.println("There's nothing here to pick up.");
            return;
        }
        inventory.add(item);
        currentRoom.setItemHere(null);
        System.out.println("You picked up: " + item);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Set<String> getInventory() {
        return inventory;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
