package Game;

import Collections.Grafos.NetworkBiDirectional;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.util.Iterator;

/**
 * Representa o mapa do labirinto usando um grafo bidirecional.
 * As salas são vértices e os corredores são arestas com pesos.
 * Portas trancadas têm peso alto (1000), portas abertas têm peso baixo (1).
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class MazeMap {

    private NetworkBiDirectional<Room> graph;
    private ArrayUnorderedList<Room> roomList;

    /**
     * Construtor do mapa.
     * Inicializa o grafo e a lista de salas.
     */
    public MazeMap() {
        this.graph = new NetworkBiDirectional<>();
        this.roomList = new ArrayUnorderedList<>();
    }

    public void addRoom(Room room) {
        graph.addVertex(room);
        roomList.addToRear(room);
    }

    public void addCorridor(Room from, Room to, double weight) {
        graph.addEdge(from, to, weight);
    }

    public void openPassage(Room from, Room to) {
        graph.addEdge(from, to, 1.0);
    }

    public Iterator<Room> getShortestPath(Room start, Room target) {
        return graph.iteratorShortestPath(start, target);
    }

    public Iterator<Room> getRooms() {
        return roomList.iterator();
    }

    public double getWeight(Room from, Room to) {
        return graph.shortestPathWeight(from, to);
    }

    public Room getRoom(String id) {
        Iterator<Room> it = roomList.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    public boolean isNeighbor(Room a, Room b) {
        if (a == null || b == null || a.equals(b))
            return false;

        Iterator<Room> it = graph.iteratorShortestPath(a, b);
        int steps = 0;
        while (it.hasNext()) {
            it.next();
            steps++;
        }
        return steps == 2;
    }

    public Iterator<Room> getNeighbors(Room current) {
        ArrayUnorderedList<Room> neighbors = new ArrayUnorderedList<>();
        Iterator<Room> allRooms = getRooms();

        while (allRooms.hasNext()) {
            Room r = allRooms.next();
            if (!r.equals(current) && isNeighbor(current, r)) {
                neighbors.addToRear(r);
            }
        }
        return neighbors.iterator();
    }
}