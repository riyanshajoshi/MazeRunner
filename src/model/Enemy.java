package model;

import algorithm.Pathfinder;

public class Enemy {
    private Room currentRoom;
    private final String name;
    private int turns = 0;

    public Enemy(String name, Room startRoom) {
        this.name = name;
        this.currentRoom = startRoom;
    }

    public boolean takeTurnToward(Room playerRoom) {
        turns++;

        // The Watcher moves every second player move so the game stays fair.
        if (turns % 2 != 0) {
            return false;
        }

        Room next = Pathfinder.nextStepToward(currentRoom, playerRoom);

        if (next != currentRoom) {
            currentRoom = next;
            return true;
        }

        return false;
    }

    public boolean hasCaughtPlayer(Room playerRoom) {
        return currentRoom == playerRoom;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public String getName() {
        return name;
    }

}
