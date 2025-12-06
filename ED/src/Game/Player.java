package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;
import java.awt.Color;

public class Player implements Comparable<Player> {
    private String nome;
    private Room salaAtual;
    private Room salaAnterior;
    private boolean isBot;
    private boolean jaInteragiu;
    private int turnosParaSaltar;
    private LinkedUnorderedList<String> historico;

    // --- Parte Visual ---
    private Color cor;

    public Player(String nome, boolean isBot, Room salaInicial, Color cor) {
        this.nome = nome;
        this.isBot = isBot;
        this.salaAtual = salaInicial;
        this.salaAnterior = null;
        this.historico = new LinkedUnorderedList<>();
        this.jaInteragiu = false;
        this.turnosParaSaltar = 0;
        this.cor = cor;
        adicionarAoLog("Começou o jogo na sala " + salaInicial.getId());
    }

    public String getName() {
        return nome;
    }

    public boolean isBot() {
        return isBot;
    }

    public Room getCurrentRoom() {
        return salaAtual;
    }

    public Room getPreviousRoom() {
        return salaAnterior;
    }

    public boolean hasInteracted() {
        return jaInteragiu;
    }

    public void setHasInteracted(boolean status) {
        this.jaInteragiu = status;
    }

    public int getSkipTurns() {
        return turnosParaSaltar;
    }

    public void setSkipTurns(int turnos) {
        this.turnosParaSaltar = turnos;
    }

    public Color getColor() {
        return cor;
    }

    public void setCurrentRoom(Room sala) {
        this.salaAnterior = this.salaAtual;
        this.salaAtual = sala;
        adicionarAoLog("Moveu-se para " + sala.getId());
    }

    public void addToLog(String evento) {
        historico.addToRear(evento);
    }

    // Método auxiliar renomeado para interno mas mantendo compatibilidade se
    // necessário
    private void adicionarAoLog(String evento) {
        historico.addToRear(evento);
    }

    public LinkedUnorderedList<String> getHistoryLog() {
        return historico;
    }

    @Override
    public String toString() {
        return nome + " [" + salaAtual.getId() + "]";
    }

    @Override
    public int compareTo(Player o) {
        // Compara por nome
        return this.nome.compareTo(o.nome);
    }
}