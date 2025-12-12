package Game;

/**
 * Representa uma sala do labirinto.
 * Cada sala tem um identificador, tipo (ENTRANCE, TREASURE, NORMAL),
 * interação (none, enigma, lever) e coordenadas para visualização.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class Room implements Comparable<Room> {
    private String id;
    private String type;
    private String interaction;
    private int x;
    private int y;

    /**
     * Construtor da sala.
     * 
     * @param id          Identificador único da sala
     * @param type        Tipo da sala (ENTRANCE, TREASURE, NORMAL)
     * @param interaction Tipo de interação (none, enigma, lever)
     * @param x           Coordenada X para visualização
     * @param y           Coordenada Y para visualização
     */
    public Room(String id, String type, String interaction, int x, int y) {
        this.id = id;
        this.type = type;
        this.interaction = interaction;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getInteraction() {
        return interaction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return id + " (" + type + ") [" + x + "," + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Room))
            return false;
        Room room = (Room) o;
        return id != null ? id.equals(room.id) : room.id == null;
    }

    @Override
    public int compareTo(Room o) {
        return this.id.compareTo(o.id);
    }
}