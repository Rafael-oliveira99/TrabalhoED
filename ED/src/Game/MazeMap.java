package Game;

import Collections.Grafos.NetworkBiDirectional;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.util.Iterator;

/**
 * Represents the game map using a bidirectional network graph.
 * <p>
 * This class manages the rooms (vertices) and corridors (edges) of the maze.
 * It uses a custom implementation of a Network ADT to handle weighted connections,
 * allowing for locked doors (high weight) and open passages (low weight).
 * </p>
 *
 * @author YourGroup
 * @version 1.1
 */
public class MazeMap {

    /** The underlying graph data structure storing rooms and connections. */
    private NetworkBiDirectional<Room> graph;

    /** A secondary list to keep track of all rooms for easy lookup by ID. */
    private ArrayUnorderedList<Room> roomList;

    /**
     * Initializes an empty MazeMap.
     */
    public MazeMap() {
        this.graph = new NetworkBiDirectional<>();
        this.roomList = new ArrayUnorderedList<>();
    }

    /**
     * Adds a new room to the map.
     *
     * @param room The Room object to add.
     */
    public void addRoom(Room room) {
        graph.addVertex(room);
        roomList.addToRear(room);
    }

    /**
     * Creates a connection (corridor) between two rooms with a specific weight.
     *
     * @param from   The starting room.
     * @param to     The destination room.
     * @param weight The cost of traversing this edge (e.g., 1.0 for open, 1000.0 for locked).
     */
    public void addCorridor(Room from, Room to, double weight) {
        graph.addEdge(from, to, weight);
    }

    /**
     * Unlocks a passage between two rooms by setting its weight to 1.0.
     *
     * @param from The starting room.
     * @param to   The destination room.
     */
    public void openPassage(Room from, Room to) {
        // Re-adding the edge updates the weight to 1.0 (Open)
        graph.addEdge(from, to, 1.0);
    }

    /**
     * Calculates the shortest path between two rooms using Dijkstra's algorithm.
     *
     * @param start  The starting Room.
     * @param target The destination Room.
     * @return An iterator containing the sequence of Rooms from start to target.
     */
    public Iterator<Room> getShortestPath(Room start, Room target) {
        return graph.iteratorShortestPath(start, target);
    }

    /**
     * Retrieves an iterator for all rooms in the map.
     *
     * @return An iterator over all Room objects.
     */
    public Iterator<Room> getRooms() {
        return roomList.iterator();
    }

    /**
     * Gets the weight (cost) of the shortest path between two rooms.
     * Used to check if a direct connection is "locked" (weight > 100).
     *
     * @param from The starting room.
     * @param to   The destination room.
     * @return The weight of the path.
     */
    public double getWeight(Room from, Room to) {
        return graph.shortestPathWeight(from, to);
    }

    /**
     * Finds a room object in the map by its string ID.
     *
     * @param id The unique identifier (name) of the room.
     * @return The Room object if found, or null otherwise.
     */
    public Room getRoom(String id) {
        Iterator<Room> it = roomList.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Checks if two rooms are directly connected (immediate neighbors).
     *
     * @param a The first room.
     * @param b The second room.
     * @return true if they are adjacent in the graph, false otherwise.
     */
    public boolean isNeighbor(Room a, Room b) {
        if (a == null || b == null || a.equals(b)) return false;

        // We use the shortest path iterator.
        // If they are neighbors, the path should have exactly 2 nodes: [Start, End]
        Iterator<Room> it = graph.iteratorShortestPath(a, b);
        int steps = 0;
        while (it.hasNext()) {
            it.next();
            steps++;
        }
        return steps == 2;
    }

    /**
     * Retrieves a list of all rooms directly connected to the specified room.
     * <p>
     * This method iterates through all rooms in the map and checks for adjacency.
     * </p>
     *
     * @param current The room to find neighbors for.
     * @return An iterator of adjacent Room objects.
     */
    public Iterator<Room> getNeighbors(Room current) {
        ArrayUnorderedList<Room> neighbors = new ArrayUnorderedList<>();
        Iterator<Room> allRooms = getRooms();

        while (allRooms.hasNext()) {
            Room r = allRooms.next();
            if (r.equals(current)) continue;

            // Check if connected
            if (isNeighbor(current, r)) {
                neighbors.addToRear(r);
            }
        }
        return neighbors.iterator();
    }
}