package Game;

// Representa uma sala do labirinto
public class Room implements Comparable<Room> {
    private String id; // nome da sala
    private String type; // ENTRANCE, TREASURE, NORMAL
    private String interaction; // none, enigma, lever
    private boolean hasLever;

    // coordenadas no mapa visual
    private int x;
    private int y;

    public Room(String id, String type, String interaction, int x, int y) {
        this.id = id;
        this.type = type;
        this.interaction = interaction;
        this.x = x;
        this.y = y;
        this.hasLever = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    public boolean hasLever() {
        return hasLever;
    }

    public void setHasLever(boolean hasLever) {
        this.hasLever = hasLever;
    }

    // getters e setters das coordenadas
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return id + " (" + type + ") [" + x + "," + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room room = (Room) o;
        // Comparação manual sem Objects.equals
        return (id == null ? room.id == null : id.equals(room.id));
    }

    @Override
    public int compareTo(Room o) {
        return this.id.compareTo(o.id);
    }
}