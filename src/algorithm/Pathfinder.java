package algorithm;

import java.util.*;

import model.Room;

/**
 * This is the actual DSA piece of the project: BFS shortest path on a graph
 * of Rooms. The Enemy uses this every turn to find the next room to move into
 * while hunting the Player.
 *
 * Why BFS and not DFS: BFS guarantees the SHORTEST path in an unweighted
 * graph, which is exactly what a "hunting" enemy needs. Once you're
 * comfortable, try swapping this for Dijkstra (if you give some corridors
 * a "cost", e.g. slippery floors) or A* (if you add a heuristic like
 * straight-line distance) — same idea, smarter enemy.
 */
public class Pathfinder {

    /**
     * Returns the shortest path from start to goal as a list of Rooms,
     * INCLUDING start and goal. Returns an empty list if unreachable.
     */
    public static List<Room> bfsShortestPath(Room start, Room goal) {
        if (start == goal) {
            return List.of(start);
        }

        Map<Room, Room> cameFrom = new HashMap<>(); // child -> parent, used to rebuild path
        Set<Room> visited = new HashSet<>();
        Queue<Room> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Room current = queue.poll();

            for (Room neighbor : current.getExits().values()) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                cameFrom.put(neighbor, current);

                if (neighbor == goal) {
                    return reconstructPath(cameFrom, start, goal);
                }
                queue.add(neighbor);
            }
        }

        return new ArrayList<>(); // goal unreachable from start
    }

    private static List<Room> reconstructPath(Map<Room, Room> cameFrom, Room start, Room goal) {
        LinkedList<Room> path = new LinkedList<>();
        Room step = goal;
        while (step != start) {
            path.addFirst(step);
            step = cameFrom.get(step);
        }
        path.addFirst(start);
        return path;
    }

    /**
     * Convenience method: given the enemy's current room and the player's
     * current room, returns the SINGLE next room the enemy should step into
     * this turn (i.e. path.get(1), since path.get(0) is where it already is).
     */
    public static Room nextStepToward(Room from, Room target) {
        List<Room> path = bfsShortestPath(from, target);
        if (path.size() < 2) {
            return from; // no path found, or already there — stay put
        }
        return path.get(1);
    }
}
