package Game;

import Collections.Queue.LinkedQueue;
import Collections.ListasIterador.Classes.LinkedUnorderedList;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private GameGUI gui;
    private ArrayUnorderedList<Enigma> availableEnigmas;

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

    public void enableGUI() {
        this.gui = new GameGUI(map, allPlayers);
        this.gui.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
        this.gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopGame();
                generateJSONReport();
                System.exit(0);
            }
        });
    }

    public void stopGame() {
        this.gameRunning = false;
        printMsg("Game stopped. Generating report...");
    }

    private void printMsg(String msg) {
        System.out.println(msg);
        if (gui != null) {
            gui.log(msg);
        }
    }

    private void movePlayerWithAnimation(Player p, Room target) {
        if (gui != null) {
            gui.animateMove(p, p.getCurrentRoom(), target);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        p.setCurrentRoom(target);
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

        // Assign unique colors based on player count (supports up to 4 players)
        Color[] playerColors = {
                new Color(80, 150, 255), // Player 1: Blue
                new Color(80, 255, 150), // Player 2: Green
                new Color(255, 180, 80), // Player 3: Orange
                new Color(180, 80, 255) // Player 4: Purple
        };

        int playerIndex = allPlayers.size() % playerColors.length;
        Color c = playerColors[playerIndex];

        Player p = new Player(name, isBot, startRoom, c);
        turnQueue.enqueue(p);
        allPlayers.addToRear(p);
    }

    public void start() {
        printMsg("--- Starting Labirinto da Glória ---");

        while (gameRunning && !turnQueue.isEmpty()) {
            Player current = turnQueue.dequeue();

            // Check for Stun
            if (current.getSkipTurns() > 0) {
                printMsg("\n>> Turn: " + current.getName() + " is stunned! (Skips turn)");
                current.addToLog("Skipped turn due to stun.");
                current.setSkipTurns(current.getSkipTurns() - 1);
                turnQueue.enqueue(current);
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                }
                continue;
            }

            if (!gameRunning)
                break;

            printMsg("\n>> Turn: " + current.getName() + " (" + (current.isBot() ? "Bot" : "Human") + ")");
            printMsg("Current Location: " + current.getCurrentRoom());

            if (current.isBot()) {
                playBotTurn(current);
            } else {
                playHumanTurn(current);
            }

            // Check Win Condition
            if (current.getCurrentRoom() != null && current.getCurrentRoom().getType().equals("TREASURE")) {
                printMsg("!!! WE HAVE A WINNER: " + current.getName() + " !!!");
                current.addToLog("Found the treasure and won!");
                gameRunning = false;
                if (gui != null)
                    JOptionPane.showMessageDialog(gui, "Winner: " + current.getName());
            } else {
                turnQueue.enqueue(current);
            }
        }

        generateJSONReport();
    }

    private void playBotTurn(Player bot) {
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
                    printMsg("Bot is blocked by a locked door at " + nextMove.getId());
                    if (bot.getCurrentRoom().getInteraction().equals("lever")) {
                        pullLever(bot);
                    } else {
                        printMsg("Bot waits.");
                    }
                } else {
                    printMsg("Bot moving to: " + nextMove.getId());
                    movePlayerWithAnimation(bot, nextMove);

                    // FIX 1: Only trigger event if NOT in treasure room
                    if (!nextMove.getType().equals("TREASURE")) {
                        triggerRandomEvent(bot);
                    }
                }
            } else {
                printMsg("Bot is confused.");
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

        printMsg("Neighbors:");
        Iterator<Room> rooms = map.getRooms();
        while (rooms.hasNext()) {
            Room r = rooms.next();
            if (map.isNeighbor(current, r)) {
                double weight = map.getWeight(current, r);
                String status = (weight > 100) ? "[LOCKED]" : "[OPEN]";
                printMsg(" - " + r.getId() + " " + status);
            }
        }

        String targetName = null;
        if (gui != null) {
            targetName = JOptionPane.showInputDialog(gui, "Type room name to move:");
        } else {
            System.out.println("Type room name to move:");
            if (consoleScanner.hasNextLine())
                targetName = consoleScanner.nextLine();
        }

        if (targetName == null || targetName.trim().isEmpty())
            return;

        Room target = map.getRoom(targetName);

        if (target == null) {
            printMsg("Room not found!");
            return;
        }

        if (!map.isNeighbor(current, target)) {
            printMsg("You can't move there directly!");
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
            printMsg("Path blocked! Find a lever.");
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
        if (gui != null) {
            input = JOptionPane.showInputDialog(gui, "Answer the Enigma:\n" + e.getQuestion());
        } else {
            System.out.print("Answer: ");
            if (consoleScanner.hasNext())
                input = consoleScanner.next();
            consoleScanner.nextLine(); // consume newline
        }

        if (e.checkAnswer(input)) {
            printMsg("Correct!");
            p.addToLog("Solved enigma: " + e.getQuestion());
        } else {
            printMsg("Wrong!");
            p.addToLog("Failed enigma.");
        }
    }

    private void pullLever(Player p) {
        printMsg("You found a lever! You pulled it.");
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
            printMsg("*CLICK* A nearby door unlocks!");

        // Explicitly unlock treasure if adjacent
        Room treasure = map.getRoom("Tesouro");
        if (treasure != null && map.isNeighbor(p.getCurrentRoom(), treasure)) {
            map.openPassage(p.getCurrentRoom(), treasure);
        }
    }

    private void triggerRandomEvent(Player p) {
        int chance = (int) (Math.random() * 100);

        if (chance < 10) {
            printMsg("!!! EVENT: A mysterious wind pushes you back! !!!");
            p.addToLog("Event: Pushed back.");
            if (p.getPreviousRoom() != null) {
                Room prev = p.getPreviousRoom();
                movePlayerWithAnimation(p, prev);
                printMsg("You were moved back to " + prev.getId());
            }
        } else if (chance < 20) {
            printMsg("!!! EVENT: You fell into a trap! You are stunned for 1 turn. !!!");
            p.addToLog("Event: Stunned by trap.");
            p.setSkipTurns(1);
        } else if (chance < 30) {
            if (allPlayers.size() > 1) {
                printMsg("!!! EVENT: TELEPORT SPELL! Choose a player to swap positions with... !!!");
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
                    printMsg("No other players to swap with!");
                } else if (p.isBot()) {
                    // Bot picks first available player
                    target = otherPlayers.first();
                } else {
                    // Human player chooses
                    if (gui != null) {
                        // GUI mode: show dialog with player list
                        Object[] playerArray = new Object[otherPlayers.size()];
                        int idx = 0;
                        for (Player other : otherPlayers) {
                            playerArray[idx++] = other.getName() + " [at " + other.getCurrentRoom().getId() + "]";
                        }

                        String choice = (String) JOptionPane.showInputDialog(
                                gui,
                                "Choose a player to swap positions with:",
                                "Teleport Spell",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                playerArray,
                                playerArray[0]);

                        if (choice != null) {
                            // Find selected player
                            idx = 0;
                            for (Player other : otherPlayers) {
                                if (choice.startsWith(other.getName())) {
                                    target = other;
                                    break;
                                }
                                idx++;
                            }
                        }
                    } else {
                        // Console mode: show numbered list
                        printMsg("Select a player to swap with:");
                        Object[] playerArray = new Object[otherPlayers.size()];
                        int idx = 0;
                        for (Player other : otherPlayers) {
                            playerArray[idx] = other;
                            printMsg((idx + 1) + ". " + other.getName() + " [at " + other.getCurrentRoom().getId()
                                    + "]");
                            idx++;
                        }

                        int choice = -1;
                        while (choice < 1 || choice > playerArray.length) {
                            System.out.print("Your choice (1-" + playerArray.length + "): ");
                            try {
                                if (consoleScanner.hasNextLine()) {
                                    String input = consoleScanner.nextLine().trim();
                                    choice = Integer.parseInt(input);
                                }
                            } catch (NumberFormatException e) {
                                printMsg("Invalid input!");
                            }
                        }
                        target = (Player) playerArray[choice - 1];
                    }
                }

                if (target != null) {
                    Room myRoom = p.getCurrentRoom();
                    Room targetRoom = target.getCurrentRoom();
                    movePlayerWithAnimation(p, targetRoom);
                    movePlayerWithAnimation(target, myRoom);
                    printMsg("Swapped positions with " + target.getName());
                }
            }
        } else if (chance < 40) {
            printMsg("!!! EVENT: Adrenaline Rush! You get an extra turn! !!!");
            p.addToLog("Event: Gained extra turn.");
            if (p.isBot())
                playBotTurn(p);
            else
                playHumanTurn(p);
        }
    }

    public void generateJSONReport() {
        exportReport();
        printMsg("Report generated: report.json");
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