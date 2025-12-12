package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;

/**
 * Representa um jogador no jogo (humano ou bot).
 * Mantém estado do jogador incluindo posição atual, histórico de ações,
 * salas visitadas e status de atordoamento.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class Player implements Comparable<Player> {
    private String name;
    private Room currentRoom;
    private Room previousRoom;
    private boolean isBot;
    private boolean hasInteracted;
    private int skipTurns;
    private LinkedUnorderedList<String> historyLog;
    private LinkedUnorderedList<String> roomsVisited;

    /**
     * Construtor do jogador.
     * 
     * @param name      Nome do jogador
     * @param isBot     Verdadeiro se for bot, falso se for humano
     * @param startRoom Sala inicial do jogador
     */
    public Player(String name, boolean isBot, Room startRoom) {
        this.name = name;
        this.isBot = isBot;
        this.currentRoom = startRoom;
        this.previousRoom = null;
        this.historyLog = new LinkedUnorderedList<>();
        this.roomsVisited = new LinkedUnorderedList<>();
        this.hasInteracted = false;
        this.skipTurns = 0;
        addToLog("Started game at " + startRoom.getId());
        roomsVisited.addToRear(startRoom.getId());
    }

    public String getName() {
        return name;
    }

    public boolean isBot() {
        return isBot;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Room getPreviousRoom() {
        return previousRoom;
    }

    public boolean hasInteracted() {
        return hasInteracted;
    }

    public void setHasInteracted(boolean status) {
        this.hasInteracted = status;
    }

    public int getSkipTurns() {
        return skipTurns;
    }

    public void setSkipTurns(int turns) {
        this.skipTurns = turns;
    }

    public void setCurrentRoom(Room room) {
        this.previousRoom = this.currentRoom;
        this.currentRoom = room;
        addToLog("Moved to " + room.getId());

        if (!hasVisited(room.getId())) {
            roomsVisited.addToRear(room.getId());
        }
    }

    public void setCurrentRoomAfterSwap(Room room) {
        this.currentRoom = room;
        this.previousRoom = room;
        addToLog("Swapped to " + room.getId());

        if (!hasVisited(room.getId())) {
            roomsVisited.addToRear(room.getId());
        }
    }

    private boolean hasVisited(String roomId) {
        for (String visitedRoomId : roomsVisited) {
            if (visitedRoomId.equals(roomId)) {
                return true;
            }
        }
        return false;
    }

    public int getRoomsVisitedCount() {
        return roomsVisited.size();
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
