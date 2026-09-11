package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {
    private Room currentRoom;
    private final Set<String> inventory;

    public Player(Room startRoom) {
        this.currentRoom = startRoom;
        this.inventory = new HashSet<>();
        currentRoom.markVisited();
    }

    public boolean moveTo(Room destination) {
        String blockedReason = getMoveBlockReason(destination);
        if (blockedReason != null) {
            System.out.println(blockedReason);
            return false;
        }
        enter(destination);
        return true;
    }

    public String getMoveBlockReason(Room destination) {
        if (destination == null) {
            return "You can't go that way.";
        }
        if (destination.isLocked() && !hasItem("brass key")) {
            return "The vault door is locked. You need the Brass Key.";
        }
        return null;
    }

    public void enter(Room destination) {
        currentRoom = destination;
        currentRoom.markVisited();
    }

    public void pickUpItemHere() {
        String item = takeItemHere();
        if (item == null) {
            System.out.println("There's nothing here to pick up.");
            return;
        }
        System.out.println("You picked up: " + item);
    }

    public String takeItemHere() {
        String item = currentRoom.getItemHere();
        if (item == null) {
            return null;
        }
        inventory.add(item);
        currentRoom.setItemHere(null);
        return item;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Set<String> getInventory() {
        return inventory;
    }

    public boolean hasItem(String itemName) {
        for (String item : inventory) {
            if (item.equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public String getInventorySummary() {
        if (inventory.isEmpty()) {
            return "empty";
        }
        List<String> sortedInventory = new ArrayList<>(inventory);
        sortedInventory.sort(String.CASE_INSENSITIVE_ORDER);
        return String.join(", ", sortedInventory);
    }
}
