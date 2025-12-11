package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;
import java.awt.Color; // Para a cor do jogador na GUI

// Classe que representa um jogador (humano ou bot)
public class Player implements Comparable<Player> {
    private String name;
    private Room currentRoom; // sala atual
    private Room previousRoom; // sala anterior (para eventos de recuar)
    private boolean isBot;
    private boolean hasInteracted; // flag para saber se já interagiu na sala atual
    private int skipTurns; // turnos que tem de saltar (quando fica atordoado)
    private LinkedUnorderedList<String> historyLog;

    // cor do jogador para aparecer no mapa visual
    private Color color;

    // Construtor
    public Player(String name, boolean isBot, Room startRoom, Color color) {
        this.name = name;
        this.isBot = isBot;
        this.currentRoom = startRoom;
        this.previousRoom = null;
        this.historyLog = new LinkedUnorderedList<>();
        this.hasInteracted = false;
        this.skipTurns = 0;
        this.color = color;
        addToLog("Started game at " + startRoom.getId());
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

    // getter para a cor
    public Color getColor() {
        return color;
    }

    // muda a sala do jogador e guarda a anterior
    public void setCurrentRoom(Room room) {
        this.previousRoom = this.currentRoom;
        this.currentRoom = room;
        addToLog("Moved to " + room.getId());
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
