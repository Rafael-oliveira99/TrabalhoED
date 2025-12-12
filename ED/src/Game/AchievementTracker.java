package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.util.Iterator;

/**
 * Tracker de conquistas e estatísticas do jogador
 */
public class AchievementTracker {
    private ArrayUnorderedList<Achievement> achievements;
    private Player trackedPlayer;

    // Estatísticas
    private int totalRooms;
    private int turnsPlayed;
    private int trapsTriggered;
    private int timesStunned;
    private int enigmasSolved;
    private int enigmasFailed;

    public AchievementTracker(Player player, int totalRoomsInMap) {
        this.trackedPlayer = player;
        this.totalRooms = totalRoomsInMap;
        this.achievements = new ArrayUnorderedList<>();
        this.turnsPlayed = 0;
        this.trapsTriggered = 0;
        this.timesStunned = 0;
        this.enigmasSolved = 0;
        this.enigmasFailed = 0;

        initializeAchievements();
    }

    private void initializeAchievements() {
        // Conquistas básicas
        achievements.addToRear(new Achievement(
                "Jogador rápido",
                "Ganhar em menos de 5 turnos", null));

        achievements.addToRear(new Achievement(
                "Sortudo",
                "Não cair em nenhuma armadilha", null));

        achievements.addToRear(new Achievement(
                "Mestre dos Enigmas",
                "Resolver enigmas sem falhar nenhum", null));

    }

    // Métodos para atualizar estatísticas
    public void incrementTurn() {
        turnsPlayed++;
    }

    public void recordTrap() {
        trapsTriggered++;
    }

    public void recordStun() {
        timesStunned++;
    }

    public void recordEnigmaSolved() {
        enigmasSolved++;
    }

    public void recordEnigmaFailed() {
        enigmasFailed++;
    }

    /**
     * Verifica todas as condições e desbloqueia conquistas
     */
    public void checkAndUnlockAchievements() {
        Iterator<Achievement> it = achievements.iterator();

        while (it.hasNext()) {
            Achievement a = it.next();

            if (a.isUnlocked())
                continue; // Já desbloqueada

            boolean shouldUnlock = false;

            switch (a.getName()) {
                case "Jogador rápido":
                    shouldUnlock = turnsPlayed < 5;
                    break;

                case "Sortudo":
                    shouldUnlock = trapsTriggered == 0;
                    break;

                case "Mestre dos Enigmas":
                    shouldUnlock = (enigmasSolved > 0) && (enigmasFailed == 0);
                    break;
            }

            if (shouldUnlock) {
                a.unlock();
            }
        }
    }

    /**
     * Mostra relatório bonito de conquistas
     */
    public void displayAchievementsReport() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║          *** RELATÓRIO DE CONQUISTAS ***              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        System.out.println("\n>>> ESTATÍSTICAS DE " + trackedPlayer.getName().toUpperCase());
        System.out.println("─────────────────────────────────────────────────────────");
        System.out.println("  Turnos jogados: " + turnsPlayed);
        System.out.println("  Salas visitadas: " + trackedPlayer.getRoomsVisitedCount() + "/" + totalRooms);
        System.out.println("  Armadilhas ativadas: " + trapsTriggered);
        System.out.println("  Vezes atordoado: " + timesStunned);
        System.out.println("  Enigmas resolvidos: " + enigmasSolved);
        System.out.println("  Enigmas falhados: " + enigmasFailed);

        System.out.println("\n>>> CONQUISTAS");
        System.out.println("─────────────────────────────────────────────────────────");

        int unlockedCount = 0;
        Iterator<Achievement> it = achievements.iterator();

        while (it.hasNext()) {
            Achievement a = it.next();
            System.out.println("  " + a.toString());
            if (a.isUnlocked())
                unlockedCount++;
        }

        System.out.println("\n─────────────────────────────────────────────────────────");
        System.out.println("  Total: " + unlockedCount + "/" + achievements.size() + " conquistas desbloqueadas");

        // Mensagem especial se desbloqueou todas
        if (unlockedCount == achievements.size()) {
            System.out.println("\n  *** PARABÉNS! TODAS AS CONQUISTAS DESBLOQUEADAS! ***");
        }

        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }
}
