package Game;

import Collections.Queue.LinkedQueue;
import Collections.ListasIterador.Classes.LinkedUnorderedList;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Manages the core game logic, including the game loop, turn management,
 * player movement, and rule enforcement for "Labirinto da Glória".
 *
 * @author Rafael Oliveira
 * @version 1.3
 */
public class GameEngine {

    private MazeMap map;
    private LinkedQueue<Player> turnQueue;
    private LinkedUnorderedList<Player> allPlayers;
    private Room treasureRoom;
    private boolean gameRunning;
    private ArrayUnorderedList<Enigma> availableEnigmas;
    private AchievementTracker achievementTracker; // Rastreador de conquistas

    private Scanner consoleScanner;

    public GameEngine() {
        this.map = new MazeMap();
        this.turnQueue = new LinkedQueue<>();
        this.allPlayers = new LinkedUnorderedList<>();
        this.gameRunning = true;
        this.consoleScanner = new Scanner(System.in);
    }

    public MazeMap getMap() {
        return map;
    }

    public void stopGame() {
        this.gameRunning = false;
        printMsg("Jogo parado. A gerar relatório...");
    }

    private void printMsg(String msg) {
        System.out.println(msg);
    }

    private void movePlayerWithAnimation(Player p, Room target) {
        p.setCurrentRoom(target);
        try {
            Thread.sleep(500); // Pequena pausa para melhor legibilidade
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadMapData(String mapFile) {
        DataLoader.loadMap(mapFile, map);
        this.treasureRoom = map.getRoom("Tesouro");
        if (this.treasureRoom == null) {
            Iterator<Room> it = map.getRooms();
            while (it.hasNext()) {
                Room r = it.next();
                if (r.getType().equals("TREASURE")) {
                    this.treasureRoom = r;
                    break;
                }
            }
        }
        // Try to find enigmas.json in common locations
        String[] possiblePaths = {
                "src/Map/enigmas.json",
                "Map/enigmas.json",
                "enigmas.json",
                "ED/src/Map/enigmas.json", // If running from parent dir
                "../src/Map/enigmas.json" // If running from bin/out
        };
        String enigmaPath = null;

        for (String path : possiblePaths) {
            if (new java.io.File(path).exists()) {
                enigmaPath = path;
                break;
            }
        }

        if (enigmaPath != null) {
            this.availableEnigmas = DataLoader.loadEnigmas(enigmaPath);
        } else {
            System.out.println("Warning: enigmas.json not found. Enigmas will be disabled.");
            this.availableEnigmas = new ArrayUnorderedList<>();
        }
    }

    public void addPlayer(String name, boolean isBot, Room startRoom) {
        Room realRoom = map.getRoom(startRoom.getId());
        if (realRoom != null) {
            startRoom = realRoom;
        } else {
            System.out.println("Warning: Start room '" + startRoom.getId() + "' not found in map!");
        }
        Player p = new Player(name, isBot, startRoom);
        allPlayers.addToRear(p);
        turnQueue.enqueue(p);
    }

    public void start() {
        printMsg("--- A Iniciar Labirinto da Glória ---");

        // Inicializar rastreador de conquistas para o primeiro jogador humano
        Player trackedPlayer = null;
        Iterator<Player> it = allPlayers.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (!p.isBot()) {
                trackedPlayer = p;
                break;
            }
        }

        // Se houver jogador humano, rastrear conquistas
        if (trackedPlayer != null) {
            int totalRooms = 0;
            Iterator<Room> roomIt = map.getRooms();
            while (roomIt.hasNext()) {
                roomIt.next();
                totalRooms++;
            }
            achievementTracker = new AchievementTracker(trackedPlayer, totalRooms);
        }

        while (gameRunning && !turnQueue.isEmpty()) {
            Player current = turnQueue.dequeue();

            // Check for Stun
            if (current.getSkipTurns() > 0) {
                printMsg("\n>> Turno: " + current.getName() + " está atordoado! (Salta turno)");
                current.addToLog("Skipped turn due to stun.");
                current.setSkipTurns(current.getSkipTurns() - 1);
                turnQueue.enqueue(current);

                // Rastrear atordoamento
                if (achievementTracker != null && !current.isBot()) {
                    achievementTracker.recordStun();
                }

                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                }
                continue;
            }

            if (!gameRunning)
                break;

            printMsg("\n>> Turno: " + current.getName() + " (" + (current.isBot() ? "Bot" : "Humano") + ")");
            printMsg("Localização atual: " + current.getCurrentRoom());

            // Incrementar contador de turnos
            if (achievementTracker != null && !current.isBot()) {
                achievementTracker.incrementTurn();
            }

            if (current.isBot()) {
                playBotTurn(current);
            } else {
                playHumanTurn(current);
            }

            // Check Win Condition
            if (current.getCurrentRoom() != null && current.getCurrentRoom().getType().equals("TREASURE")) {
                printMsg("!!! TEMOS UM VENCEDOR: " + current.getName() + " !!!");
                current.addToLog("Found the treasure and won!");
                gameRunning = false;
            } else {
                turnQueue.enqueue(current);
            }
        }

        generateJSONReport();

        // Mostrar conquistas se houver rastreador
        if (achievementTracker != null) {
            achievementTracker.checkAndUnlockAchievements();
            achievementTracker.displayAchievementsReport();
        }
    }

    private void playBotTurn(Player bot) {
        // Verificar se há um enigma na sala atual antes de mover
        if (!bot.hasInteracted() && bot.getCurrentRoom().getInteraction().equals("enigma")) {
            solveBotEnigma(bot);
            bot.setHasInteracted(true);
        }

        Iterator<Room> path = map.getShortestPath(bot.getCurrentRoom(), treasureRoom);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        if (path.hasNext()) {
            path.next();
            if (path.hasNext()) {
                Room nextMove = path.next();
                double weight = map.getWeight(bot.getCurrentRoom(), nextMove);

                if (weight > 100) {
                    printMsg("Bot está bloqueado por uma porta trancada em " + nextMove.getId());
                    if (bot.getCurrentRoom().getInteraction().equals("lever")) {
                        pullLever(bot);
                    } else {
                        printMsg("Bot espera.");
                    }
                } else {
                    printMsg("Bot a mover para: " + nextMove.getId());
                    movePlayerWithAnimation(bot, nextMove);
                    bot.setHasInteracted(false);

                    // FIX 1: Only trigger event if NOT in treasure room
                    if (!nextMove.getType().equals("TREASURE")) {
                        triggerRandomEvent(bot);
                    }
                }
            } else {
                printMsg("Bot está confuso.");
            }
        }
    }

    private void playHumanTurn(Player player) {
        Room current = player.getCurrentRoom();

        if (!player.hasInteracted()) {
            if (current.getInteraction().equals("enigma")) {
                solveEnigma(player);
            } else if (current.getInteraction().equals("lever")) {
                pullLever(player);
            }
            player.setHasInteracted(true);
        }

        printMsg("Vizinhos:");
        Iterator<Room> rooms = map.getRooms();
        while (rooms.hasNext()) {
            Room r = rooms.next();
            if (map.isNeighbor(current, r)) {
                double weight = map.getWeight(current, r);
                String status = (weight > 100) ? "[TRANCADA]" : "[ABERTA]";
                printMsg(" - " + r.getId() + " " + status);
            }
        }

        String targetName = null;
        System.out.println("Digite nome da sala para mover:");
        if (consoleScanner.hasNextLine())
            targetName = consoleScanner.nextLine();

        if (targetName == null || targetName.trim().isEmpty())
            return;

        Room target = map.getRoom(targetName);

        if (target == null) {
            printMsg("Sala não encontrada!");
            return;
        }

        if (!map.isNeighbor(current, target)) {
            printMsg("Não podes mover diretamente para lá!");
            return;
        }

        double weight = map.getWeight(current, target);
        if (weight < 100) {
            movePlayerWithAnimation(player, target);
            player.setHasInteracted(false);

            // FIX 1: Don't trigger events if we just won!
            if (!target.getType().equals("TREASURE")) {
                triggerRandomEvent(player);
            }
        } else {
            printMsg("Caminho bloqueado! Encontra uma alavanca.");
        }
    }

    private void solveEnigma(Player p) {
        if (availableEnigmas == null || availableEnigmas.isEmpty()) {
            this.availableEnigmas = DataLoader.loadEnigmas("src/Map/enigmas.json");
        }
        if (availableEnigmas.isEmpty())
            return;

        // FIX 2: Random Enigma Logic
        int randomIndex = (int) (Math.random() * availableEnigmas.size());

        // Iterate to find the Nth enigma
        Iterator<Enigma> it = availableEnigmas.iterator();
        Enigma e = null;
        for (int i = 0; i <= randomIndex; i++) {
            e = it.next();
        }
        // Remove it to prevent repetition
        availableEnigmas.remove(e);

        printMsg("Enigma: " + e.getQuestion());

        String input = "";
        System.out.print("Resposta: ");
        if (consoleScanner.hasNext())
            input = consoleScanner.next();
        consoleScanner.nextLine(); // consume newline

        if (e.checkAnswer(input)) {
            printMsg("Correto!");
            p.addToLog("Solved enigma: " + e.getQuestion());

            // Rastrear enigma resolvido
            if (achievementTracker != null && !p.isBot()) {
                achievementTracker.recordEnigmaSolved();
            }
        } else {
            printMsg("Errado!");
            p.addToLog("Failed enigma.");

            // Rastrear enigma falhado
            if (achievementTracker != null && !p.isBot()) {
                achievementTracker.recordEnigmaFailed();
            }
        }
    }

    private void solveBotEnigma(Player bot) {
        if (availableEnigmas == null || availableEnigmas.isEmpty()) {
            this.availableEnigmas = DataLoader.loadEnigmas("src/Map/enigmas.json");
        }
        if (availableEnigmas.isEmpty())
            return;

        // Selecionar um enigma aleatório
        int randomIndex = (int) (Math.random() * availableEnigmas.size());

        Iterator<Enigma> it = availableEnigmas.iterator();
        Enigma e = null;
        for (int i = 0; i <= randomIndex; i++) {
            e = it.next();
        }
        // Remover para evitar repetição
        availableEnigmas.remove(e);

        printMsg("Bot encontrou um enigma: " + e.getQuestion());

        // 33% de probabilidade de acertar
        double chance = Math.random();
        if (chance < 0.33) {
            printMsg("Bot resolveu o enigma corretamente! ✓");
            bot.addToLog("Solved enigma: " + e.getQuestion());
        } else {
            printMsg("Bot falhou o enigma. ✗");
            bot.addToLog("Failed enigma.");
        }
    }

    private void pullLever(Player p) {
        printMsg("Encontraste uma alavanca! Puxaste-a.");
        p.addToLog("Pulled a lever.");

        Iterator<Room> neighbors = map.getNeighbors(p.getCurrentRoom());
        boolean unlockedAny = false;
        while (neighbors.hasNext()) {
            Room r = neighbors.next();
            if (map.getWeight(p.getCurrentRoom(), r) > 100) {
                map.openPassage(p.getCurrentRoom(), r);
                unlockedAny = true;
            }
        }

        if (unlockedAny)
            printMsg("*CLICK* Uma porta próxima destrancou!");

        // Explicitly unlock treasure if adjacent
        Room treasure = map.getRoom("Tesouro");
        if (treasure != null && map.isNeighbor(p.getCurrentRoom(), treasure)) {
            map.openPassage(p.getCurrentRoom(), treasure);
        }
    }

    private void triggerRandomEvent(Player p) {
        int chance = (int) (Math.random() * 100);

        if (chance < 10) {
            printMsg("!!! EVENTO: Um vento misterioso empurra-te para trás! !!!");
            p.addToLog("Event: Pushed back.");
            if (p.getPreviousRoom() != null) {
                Room prev = p.getPreviousRoom();
                movePlayerWithAnimation(p, prev);
                printMsg("Foste movido de volta para " + prev.getId());
            }
        } else if (chance < 20) {
            printMsg("!!! EVENTO: Caíste numa armadilha! Estás atordoado por 1 turno. !!!");
            p.addToLog("Event: Stunned by trap.");
            p.setSkipTurns(1);

            // Rastrear armadilha
            if (achievementTracker != null && !p.isBot()) {
                achievementTracker.recordTrap();
                achievementTracker.recordStun();
            }
        } else if (chance < 30) {
            if (allPlayers.size() > 1) {
                printMsg("!!! EVENTO: FEITIO DE TELETRANSPORTE! Escolhe um jogador para trocar posições... !!!");
                p.addToLog("Event: Swapped positions.");

                // Build list of other players
                ArrayUnorderedList<Player> otherPlayers = new ArrayUnorderedList<>();
                Iterator<Player> it = allPlayers.iterator();
                while (it.hasNext()) {
                    Player other = it.next();
                    if (!other.equals(p)) {
                        otherPlayers.addToRear(other);
                    }
                }

                Player target = null;

                if (otherPlayers.isEmpty()) {
                    printMsg("Sem outros jogadores para trocar!");
                } else if (p.isBot()) {
                    // Bot picks first available player
                    target = otherPlayers.first();
                } else {
                    // Console mode: show numbered list
                    printMsg("Seleciona um jogador para trocar:");
                    Object[] playerArray = new Object[otherPlayers.size()];
                    int idx = 0;
                    for (Player other : otherPlayers) {
                        playerArray[idx] = other;
                        printMsg((idx + 1) + ". " + other.getName() + " [em " + other.getCurrentRoom().getId()
                                + "]");
                        idx++;
                    }

                    int choice = -1;
                    while (choice < 1 || choice > playerArray.length) {
                        System.out.print("Tua escolha (1-" + playerArray.length + "): ");
                        try {
                            if (consoleScanner.hasNextLine()) {
                                String input = consoleScanner.nextLine().trim();
                                choice = Integer.parseInt(input);
                            }
                        } catch (NumberFormatException e) {
                            printMsg("Entrada inválida!");
                        }
                    }
                    target = (Player) playerArray[choice - 1];
                }

                if (target != null) {
                    Room myRoom = p.getCurrentRoom();
                    Room targetRoom = target.getCurrentRoom();
                    movePlayerWithAnimation(p, targetRoom);
                    movePlayerWithAnimation(target, myRoom);
                    printMsg("Posições trocadas com " + target.getName());
                }
            }
        } else if (chance < 40) {
            printMsg("!!! EVENTO: Descarga de Adrenalina! Ganhas um turno extra! !!!");
            p.addToLog("Event: Gained extra turn.");
            if (p.isBot())
                playBotTurn(p);
            else
                playHumanTurn(p);
        }
    }

    public void generateJSONReport() {
        exportReport();
        printMsg("Relatório gerado: report.json");
    }

    public void exportReport() {
        try (FileWriter file = new FileWriter("report.json")) {
            file.write("[\n");
            boolean firstPlayer = true;
            for (Player p : allPlayers) {
                if (!firstPlayer)
                    file.write(",\n");
                file.write("  {\n");
                file.write("    \"name\": \"" + p.getName() + "\",\n");
                file.write("    \"log\": [\n");
                Iterator<String> logs = p.getHistoryLog().iterator();
                while (logs.hasNext()) {
                    file.write("      \"" + logs.next() + "\"");
                    if (logs.hasNext())
                        file.write(",");
                    file.write("\n");
                }
                file.write("    ]\n");
                file.write("  }");
                firstPlayer = false;
            }
            file.write("\n]");
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}