package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;

// Classe que representa um jogador (humano ou bot)
public class Player implements Comparable<Player> {
    private String name;
    private Room currentRoom; // sala atual
    private Room previousRoom; // sala anterior (para eventos de recuar)
    private boolean isBot;
    private boolean hasInteracted; // flag para saber se já interagiu na sala atual
    private int skipTurns; // turnos que tem de saltar (quando fica atordoado)
    private LinkedUnorderedList<String> historyLog;
    private LinkedUnorderedList<String> roomsVisited; // Lista de IDs de salas visitadas

    // Construtor
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
        roomsVisited.addToRear(startRoom.getId()); // Adicionar sala inicial
    }

    // getters e setters básicos
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

    // muda a sala do jogador e guarda a anterior
    public void setCurrentRoom(Room room) {
        this.previousRoom = this.currentRoom;
        this.currentRoom = room;
        addToLog("Moved to " + room.getId());

        // Adicionar à lista de salas visitadas se ainda não visitou
        boolean alreadyVisited = false;
        for (String visitedRoomId : roomsVisited) {
            if (visitedRoomId.equals(room.getId())) {
                alreadyVisited = true;
                break;
            }
        }
        if (!alreadyVisited) {
            roomsVisited.addToRear(room.getId());
        }
    }

    // Retorna quantas salas DIFERENTES o jogador já visitou
    public int getRoomsVisitedCount() {
        return roomsVisited.size();
    }

    // adiciona evento ao histórico do jogador
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
