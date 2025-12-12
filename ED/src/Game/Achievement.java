package Game;

/**
 * Representa uma conquista do jogo.
 * Cada conquista tem um nome, descrição e estado de desbloqueio.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class Achievement {
    private String name;
    private String description;
    private boolean unlocked;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.unlocked = false;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
