package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.util.Iterator;

/**
 * Rastreador de conquistas durante o jogo.
 * Monitoriza estatísticas do jogador e desbloqueia conquistas.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class AchievementTracker {
    private ArrayUnorderedList<Achievement> achievements;
    private Player trackedPlayer;

    // Estatísticas
    private int trapsTriggered;
    private int enigmasSolved;
    private int enigmasFailed;

    public AchievementTracker(Player player) {
        this.trackedPlayer = player;
        this.achievements = new ArrayUnorderedList<>();
        this.trapsTriggered = 0;
        this.enigmasSolved = 0;
        this.enigmasFailed = 0;

        initializeAchievements();
    }

    public Player getTrackedPlayer() {
        return trackedPlayer;
    }

    private void initializeAchievements() {
        achievements.addToRear(new Achievement("Explorador", "Visitar mais de 5 salas"));
        achievements.addToRear(new Achievement("Mestre dos Enigmas", "Acertar todos os enigmas"));
        achievements.addToRear(new Achievement("Sortudo", "Não cair em nenhuma armadilha"));
    }

    public void recordTrap() {
        trapsTriggered++;
    }

    public void recordEnigmaSolved() {
        enigmasSolved++;
    }

    public void recordEnigmaFailed() {
        enigmasFailed++;
    }

    public void checkAndUnlockAchievements() {
        Iterator<Achievement> it = achievements.iterator();

        while (it.hasNext()) {
            Achievement a = it.next();

            if (a.isUnlocked())
                continue;

            switch (a.getName()) {
                case "Explorador":
                    if (trackedPlayer.getRoomsVisitedCount() > 5) {
                        a.unlock();
                    }
                    break;

                case "Mestre dos Enigmas":
                    if (enigmasSolved > 0 && enigmasFailed == 0) {
                        a.unlock();
                    }
                    break;

                case "Sortudo":
                    if (trapsTriggered == 0) {
                        a.unlock();
                    }
                    break;
            }
        }
    }

    public void displayAchievementsReport() {
        System.out.println("\n========== CONQUISTAS ALCANCADAS ==========");

        Iterator<Achievement> it = achievements.iterator();
        boolean hasAny = false;

        while (it.hasNext()) {
            Achievement a = it.next();
            if (a.isUnlocked()) {
                System.out.println("  * " + a.getName() + " - " + a.getDescription());
                hasAny = true;
            }
        }

        if (!hasAny) {
            System.out.println("  Nenhuma conquista desbloqueada neste jogo.");
        }

        System.out.println("===========================================\n");
    }
}
