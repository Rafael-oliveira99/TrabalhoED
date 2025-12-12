package Game;

/**
 * Representa uma conquista que pode ser desbloqueada durante o jogo
 */
public class Achievement {
    private String name;
    private String description;
    private String icon;
    private boolean unlocked;

    public Achievement(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
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

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        String status = unlocked ? "Desbloqueada" : "Bloqueada";
        return icon + " " + name + " - " + description + " [" + status + "]";
    }
}
