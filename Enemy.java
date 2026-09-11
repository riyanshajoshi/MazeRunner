public class Enemy {
    private Room currentRoom;
    private final String name;

    public Enemy(String name, Room startRoom) {
        this.name = name;
        this.currentRoom = startRoom;
    }

    /**
     * Called once per turn. The enemy asks Pathfinder for the shortest
     * route to the player's current room, then takes ONE step along it.
     * This is where the graph algorithm actually drives gameplay.
     */
    public void takeTurnToward(Room playerRoom) {
        Room next = Pathfinder.nextStepToward(currentRoom, playerRoom);
        if (next != currentRoom) {
            System.out.println("You hear footsteps moving through the " + next.getName() + "...");
        }
        currentRoom = next;
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
