package Game;

import Collections.Queue.LinkedQueue;
import Collections.ListasIterador.Classes.LinkedUnorderedList;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Motor principal do jogo "Labirinto da Glória".
 * Gerencia toda a lógica do jogo incluindo turnos, movimento de jogadores,
 * eventos aleatórios, enigmas, alavancas e condições de vitória.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class GameEngine {

    private MazeMap map;
    private LinkedQueue<Player> turnQueue;
    private LinkedUnorderedList<Player> allPlayers;
    private Room treasureRoom;
    private boolean gameRunning;
    private ArrayUnorderedList<Enigma> availableEnigmas;
    private AchievementTracker achievementTracker;
    private Scanner consoleScanner;

    /**
     * Construtor do motor do jogo.
     * Inicializa todas as estruturas de dados necessárias.
     */
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
        System.out.println("Jogo parado. A gerar relatório...");
    }

    private void movePlayerWithAnimation(Player p, Room target) {
        p.setCurrentRoom(target);
        try {
            Thread.sleep(500);
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

        String[] possiblePaths = {
                "src/Map/enigmas.json",
                "Map/enigmas.json",
                "enigmas.json",
                "ED/src/Map/enigmas.json",
                "../src/Map/enigmas.json"
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
        System.out.println("--- A Iniciar O Labirinto da Glória ---");

        Player trackedPlayer = null;
        Iterator<Player> it = allPlayers.iterator();
        if (it.hasNext()) {
            trackedPlayer = it.next();
        }

        if (trackedPlayer != null) {
            achievementTracker = new AchievementTracker(trackedPlayer);
        }

        while (gameRunning && !turnQueue.isEmpty()) {
            Player current = turnQueue.dequeue();

            if (current.getSkipTurns() > 0) {
                System.out.println("\n>> Turno: " + current.getName() + " está atordoado! (Salta turno)");
                current.addToLog("Skipped turn due to stun.");
                current.setSkipTurns(current.getSkipTurns() - 1);
                turnQueue.enqueue(current);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
                continue;
            }

            if (!gameRunning)
                break;

            System.out.println("\n>> Turno: " + current.getName() + " (" + (current.isBot() ? "Bot" : "Humano") + ")");
            System.out.println("Localização atual: " + current.getCurrentRoom());

            if (current.isBot()) {
                playBotTurn(current);
            } else {
                playHumanTurn(current);
            }

            if (current.getCurrentRoom() != null && current.getCurrentRoom().getType().equals("TREASURE")) {
                System.out.println("!!! VENCEDOR: " + current.getName() + " !!!");
                current.addToLog("Found the treasure and won!");
                gameRunning = false;
            } else {
                turnQueue.enqueue(current);
            }
        }

        exportReport();
        System.out.println("Relatório gerado: report.json");

        if (achievementTracker != null) {
            achievementTracker.checkAndUnlockAchievements();
            achievementTracker.displayAchievementsReport();
        }
    }

    private void playBotTurn(Player bot) {
        if (!bot.hasInteracted() && bot.getCurrentRoom().getInteraction().equals("enigma")) {
            solveBotEnigma(bot);
            bot.setHasInteracted(true);
        }

        Iterator<Room> path = map.getShortestPath(bot.getCurrentRoom(), treasureRoom);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        if (path.hasNext()) {
            path.next();
            if (path.hasNext()) {
                Room nextMove = path.next();
                double weight = map.getWeight(bot.getCurrentRoom(), nextMove);

                if (weight > 100) {
                    System.out.println("O Bot está bloqueado por uma porta trancada em " + nextMove.getId());
                    if (bot.getCurrentRoom().getInteraction().equals("lever")) {
                        pullLever(bot);
                    } else {
                        System.out.println("O Bot espera.");
                    }
                } else {
                    System.out.println("O Bot move-se para: " + nextMove.getId());
                    movePlayerWithAnimation(bot, nextMove);
                    bot.setHasInteracted(false);

                    if (!nextMove.getType().equals("TREASURE")) {
                        triggerRandomEvent(bot);
                    }
                }
            } else {
                System.out.println("O Bot está confuso.");
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

        System.out.println("Vizinhos:");
        Iterator<Room> rooms = map.getRooms();
        while (rooms.hasNext()) {
            Room r = rooms.next();
            if (map.isNeighbor(current, r)) {
                double weight = map.getWeight(current, r);
                String status = (weight > 100) ? "[TRANCADA]" : "[ABERTA]";
                System.out.println(" - " + r.getId() + " " + status);
            }
        }

        System.out.println("Digita o nome da sala para ires:");
        String targetName = consoleScanner.hasNextLine() ? consoleScanner.nextLine() : null;

        if (targetName == null || targetName.trim().isEmpty())
            return;

        Room target = map.getRoom(targetName);

        if (target == null) {
            System.out.println("Sala não encontrada!");
            return;
        }

        if (!map.isNeighbor(current, target)) {
            System.out.println("Não podes mover-te diretamente para lá!");
            return;
        }

        double weight = map.getWeight(current, target);
        if (weight < 100) {
            movePlayerWithAnimation(player, target);
            player.setHasInteracted(false);

            if (!target.getType().equals("TREASURE")) {
                triggerRandomEvent(player);
            }
        } else {
            System.out.println("Caminho bloqueado! Encontra uma alavanca.");
        }
    }

    private Enigma getRandomEnigma() {
        if (availableEnigmas == null || availableEnigmas.isEmpty()) {
            this.availableEnigmas = DataLoader.loadEnigmas("src/Map/enigmas.json");
        }
        if (availableEnigmas.isEmpty())
            return null;

        int randomIndex = (int) (Math.random() * availableEnigmas.size());
        Iterator<Enigma> it = availableEnigmas.iterator();
        Enigma e = null;
        for (int i = 0; i <= randomIndex; i++) {
            e = it.next();
        }
        availableEnigmas.remove(e);
        return e;
    }

    private void solveEnigma(Player p) {
        Enigma e = getRandomEnigma();
        if (e == null)
            return;

        System.out.println("Enigma: " + e.getQuestion());
        System.out.print("Resposta: ");

        String input = consoleScanner.hasNext() ? consoleScanner.next() : "";
        consoleScanner.nextLine();

        if (e.checkAnswer(input)) {
            System.out.println("Correto!");
            p.addToLog("Solved enigma: " + e.getQuestion());
            if (achievementTracker != null && p.equals(achievementTracker.getTrackedPlayer())) {
                achievementTracker.recordEnigmaSolved();
            }
        } else {
            System.out.println("Errado!");
            p.addToLog("Failed enigma.");
            if (achievementTracker != null && p.equals(achievementTracker.getTrackedPlayer())) {
                achievementTracker.recordEnigmaFailed();
            }
        }
    }

    private void solveBotEnigma(Player bot) {
        Enigma e = getRandomEnigma();
        if (e == null)
            return;

        System.out.println("Bot encontrou um enigma: " + e.getQuestion());

        if (Math.random() < 0.33) {
            System.out.println("Bot resolveu o enigma corretamente!");
            bot.addToLog("Solved enigma: " + e.getQuestion());
        } else {
            System.out.println("Bot falhou o enigma.");
            bot.addToLog("Failed enigma.");
        }
    }

    private void pullLever(Player p) {
        System.out.println("Encontraste uma alavanca! A tentar ativá-la...");
        p.addToLog("Tried to pull lever.");

        boolean success = Math.random() < 0.5;

        if (success) {
            System.out.println("*CLICK* Conseguiste! A alavanca funcionou e as portas próximas destrancaram!");
            p.addToLog("Lever worked - doors unlocked.");

            Iterator<Room> neighbors = map.getNeighbors(p.getCurrentRoom());
            boolean unlockedAny = false;
            while (neighbors.hasNext()) {
                Room r = neighbors.next();
                if (map.getWeight(p.getCurrentRoom(), r) > 100) {
                    map.openPassage(p.getCurrentRoom(), r);
                    unlockedAny = true;
                }
            }

            Room treasure = map.getRoom("Tesouro");
            if (treasure != null && map.isNeighbor(p.getCurrentRoom(), treasure)) {
                map.openPassage(p.getCurrentRoom(), treasure);
            }

            if (!unlockedAny && treasure == null) {
                System.out.println("(Mas não havia portas trancadas por perto)");
            }
        } else {
            System.out.println("*CLUNK* A alavanca ficou presa! Não funcionou...");
            System.out.println("Terás de tentar novamente numa próxima jogada.");
            p.addToLog("Lever failed.");
            p.setHasInteracted(false);
        }
    }

    private void triggerRandomEvent(Player p) {
        int chance = (int) (Math.random() * 100);

        if (chance < 10) {
            System.out.println("!!! EVENTO: Uma rajada de vento empurra-te para trás! !!!");
            p.addToLog("Event: Pushed back.");
            if (p.getPreviousRoom() != null) {
                movePlayerWithAnimation(p, p.getPreviousRoom());
                System.out.println("Foste movido de volta para " + p.getPreviousRoom().getId());
            }
        } else if (chance < 20) {
            System.out.println("!!! EVENTO: Caíste numa armadilha! Estás stun durante 1 turno. !!!");
            p.addToLog("Event: Stunned by trap.");
            p.setSkipTurns(1);

            if (achievementTracker != null && p.equals(achievementTracker.getTrackedPlayer())) {
                achievementTracker.recordTrap();
            }
        } else if (chance < 30) {
            if (allPlayers.size() > 1) {
                System.out.println(
                        "!!! EVENTO: PODER DO TELEPORT! Escolhe um jogador para trocar posições... !!!");
                p.addToLog("Event: Swapped positions.");

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
                    System.out.println("Sem outros jogadores para trocar!");
                } else if (p.isBot()) {
                    target = otherPlayers.first();
                } else {
                    System.out.println("Seleciona um jogador para trocar:");
                    Object[] playerArray = new Object[otherPlayers.size()];
                    int idx = 0;
                    for (Player other : otherPlayers) {
                        playerArray[idx] = other;
                        System.out.println((idx + 1) + ". " + other.getName() + " [em " +
                                other.getCurrentRoom().getId() + "]");
                        idx++;
                    }

                    int choice = -1;
                    while (choice < 1 || choice > playerArray.length) {
                        System.out.print("Tua escolha (1-" + playerArray.length + "): ");
                        try {
                            if (consoleScanner.hasNextLine()) {
                                choice = Integer.parseInt(consoleScanner.nextLine().trim());
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida!");
                        }
                    }
                    target = (Player) playerArray[choice - 1];
                }

                if (target != null) {
                    Room myRoom = p.getCurrentRoom();
                    Room targetRoom = target.getCurrentRoom();
                    p.setCurrentRoomAfterSwap(targetRoom);
                    target.setCurrentRoomAfterSwap(myRoom);
                    System.out.println("Posições trocadas com " + target.getName());
                }
            }
        } else if (chance < 35) {
            if (allPlayers.size() > 2) {
                System.out.println("!!! EVENTO: CAOS TOTAL! Todos os jogadores trocam de posição! !!!");
                p.addToLog("Event: All players shuffled.");

                ArrayUnorderedList<Room> currentPositions = new ArrayUnorderedList<>();
                Iterator<Player> it = allPlayers.iterator();
                while (it.hasNext()) {
                    currentPositions.addToRear(it.next().getCurrentRoom());
                }

                ArrayUnorderedList<Room> shuffledPositions = new ArrayUnorderedList<>();
                while (!currentPositions.isEmpty()) {
                    int randomIndex = (int) (Math.random() * currentPositions.size());
                    Iterator<Room> roomIt = currentPositions.iterator();
                    Room selectedRoom = null;
                    for (int i = 0; i <= randomIndex; i++) {
                        selectedRoom = roomIt.next();
                    }
                    shuffledPositions.addToRear(selectedRoom);
                    currentPositions.remove(selectedRoom);
                }

                Iterator<Player> playerIt = allPlayers.iterator();
                Iterator<Room> roomIt = shuffledPositions.iterator();
                while (playerIt.hasNext() && roomIt.hasNext()) {
                    Player player = playerIt.next();
                    Room newRoom = roomIt.next();
                    player.setCurrentRoomAfterSwap(newRoom);
                    System.out.println(player.getName() + " foi movido para " + newRoom.getId());
                }
            } else {
                System.out.println("!!! EVENTO: CAOS TOTAL! (Mas são poucos jogadores para trocar) !!!");
            }
        } else if (chance < 45) {
            System.out.println("!!! EVENTO: Adrenalina! Tens um turno extra! !!!");
            p.addToLog("Event: Gained extra turn.");
            if (p.isBot())
                playBotTurn(p);
            else
                playHumanTurn(p);
        }
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