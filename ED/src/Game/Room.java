package Game;

import java.util.Objects;

public class Room implements Comparable<Room> {
    private String id;
    private String type;
    private String interaction;
    private boolean hasLever;

    // --- NEW COORDINATES ---
    private int x;
    private int y;

    // Updated Constructor
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

    public String getType() {
        return type;
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

    // Getters for coords
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
        return Objects.equals(id, room.id);
    }

    @Override
    public int compareTo(Room o) {
        return this.id.compareTo(o.id);
    }
}