package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;
import java.awt.Color; // Added for GUI

public class Player implements Comparable<Player> {
    private String name;
    private Room currentRoom;
    private Room previousRoom;
    private boolean isBot;
    private boolean hasInteracted;
    private int skipTurns;
    private LinkedUnorderedList<String> historyLog;

    // --- VISUAL FIELDS ---
    private Color color;

    public Player(String name, boolean isBot, Room startRoom, Color color) {
        this.name = name;
        this.isBot = isBot;
        this.currentRoom = startRoom;
        this.previousRoom = null;
        this.historyLog = new LinkedUnorderedList<>();
        this.hasInteracted = false;
        this.skipTurns = 0;
        this.color = color; // Assign color
        addToLog("Started game at " + startRoom.getId());
    }

    // ... (Keep all existing Getters/Setters) ...
    public String getName() { return name; }
    public boolean isBot() { return isBot; }
    public Room getCurrentRoom() { return currentRoom; }
    public Room getPreviousRoom() { return previousRoom; }
    public boolean hasInteracted() { return hasInteracted; }
    public void setHasInteracted(boolean status) { this.hasInteracted = status; }
    public int getSkipTurns() { return skipTurns; }
    public void setSkipTurns(int turns) { this.skipTurns = turns; }

    // New Getter for Color
    public Color getColor() { return color; }

    public void setCurrentRoom(Room room) {
        this.previousRoom = this.currentRoom;
        this.currentRoom = room;
        addToLog("Moved to " + room.getId());
    }

    public void addToLog(String event) {
        historyLog.addToRear(event);
    }

    public LinkedUnorderedList<String> getHistoryLog() {
        return historyLog;
    }

    @Override
    public String toString() {
        return name + " [" + currentRoom.getId() + "]";
    }

    @Override
    public int compareTo(Player o) {
        return this.name.compareTo(o.name);
    }
}