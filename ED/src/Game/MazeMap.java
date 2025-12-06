package Game;

import Collections.Grafos.NetworkBiDirectional;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.util.Iterator;

/**
 * Represents the game map using a bidirectional network graph.
 * <p>
 * This class manages the rooms (vertices) and corridors (edges) of the maze.
 * It uses a custom implementation of a Network ADT to handle weighted
 * connections,
 * allowing for locked doors (high weight) and open passages (low weight).
 * </p>
 *
 * @author YourGroup
 * @version 1.1
 */
public class MazeMap {

    /** Grafo bidirecional que armazena salas e conexões. */
    private NetworkBiDirectional<Room> grafo;

    /** Lista secundária para manter todas as salas para pesquisa fácil por ID. */
    private ArrayUnorderedList<Room> listaSalas;

    /**
     * Inicializa um Mapa vazio.
     */
    public MazeMap() {
        this.grafo = new NetworkBiDirectional<>();
        this.listaSalas = new ArrayUnorderedList<>();
    }

    /**
     * Adiciona uma nova sala ao mapa.
     *
     * @param room A sala a adicionar.
     */
    public void addRoom(Room room) {
        grafo.addVertex(room);
        listaSalas.addToRear(room);
    }

    /**
     * Cria uma conexão (corredor) entre duas salas com um peso específico.
     *
     * @param from   Sala de origem.
     * @param to     Sala de destino.
     * @param weight Custo de atravessar (ex: 1.0 para aberto, 1000.0 para
     *               trancado).
     */
    public void addCorridor(Room from, Room to, double weight) {
        grafo.addEdge(from, to, weight);
    }

    /**
     * Destranca uma passagem entre duas salas definindo o peso para 1.0.
     *
     * @param from Sala de origem.
     * @param to   Sala de destino.
     */
    public void openPassage(Room from, Room to) {
        // Re-adicionar a aresta atualiza o peso para 1.0 (Aberto)
        grafo.addEdge(from, to, 1.0);
    }

    /**
     * Calcula o caminho mais curto entre duas salas usando Dijkstra.
     *
     * @param start  Sala inicial.
     * @param target Sala destino.
     * @return Iterador com a sequência de salas.
     */
    public Iterator<Room> getShortestPath(Room start, Room target) {
        return grafo.iteratorShortestPath(start, target);
    }

    /**
     * Devolve um iterador para todas as salas do mapa.
     *
     * @return Iterador sobre objetos Room.
     */
    public Iterator<Room> getRooms() {
        return listaSalas.iterator();
    }

    /**
     * Obtém o peso do caminho mais curto entre duas salas.
     *
     * @param from Sala de origem.
     * @param to   Sala de destino.
     * @return O peso do caminho.
     */
    public double getWeight(Room from, Room to) {
        return grafo.shortestPathWeight(from, to);
    }

    /**
     * Encontra uma sala pelo seu ID.
     *
     * @param id Identificador único da sala.
     * @return A Sala se encontrada, ou null.
     */
    public Room getRoom(String id) {
        Iterator<Room> it = listaSalas.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Verifica se duas salas são vizinhas diretas.
     */
    public boolean isNeighbor(Room a, Room b) {
        if (a == null || b == null || a.equals(b))
            return false;

        Iterator<Room> it = grafo.iteratorShortestPath(a, b);
        int steps = 0;
        while (it.hasNext()) {
            it.next();
            steps++;
        }
        return steps == 2;
    }

    /**
     * Devolve lista de vizinhos diretos.
     */
    public Iterator<Room> getNeighbors(Room current) {
        ArrayUnorderedList<Room> neighbors = new ArrayUnorderedList<>();
        Iterator<Room> allRooms = getRooms();

        while (allRooms.hasNext()) {
            Room r = allRooms.next();
            if (r.equals(current))
                continue;

            if (isNeighbor(current, r)) {
                neighbors.addToRear(r);
            }
        }
        return neighbors.iterator();
    }
}