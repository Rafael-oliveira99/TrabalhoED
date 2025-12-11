package Game;

import java.util.Scanner;
import java.io.File;
import Collections.ListasIterador.Classes.LinkedUnorderedList;

// Classe principal - é aqui que o programa começa
public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        // loop principal do menu
        while (true) {
            System.out.println("\n=== LABIRINTO DA GLORIA ===");
            System.out.println("1. Play Game (Console)");
            System.out.println("2. Play Game (Graphical Interface)");
            System.out.println("3. Map Editor (Create/Generate)");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String op = s.next();
            s.nextLine(); // consumir o newline que sobra

            if (op.equals("0"))
                break; // sair do programa

            if (op.equals("3")) {
                // abrir o editor de mapas
                MapEditor editor = new MapEditor();
                editor.start();
            } else if (op.equals("1") || op.equals("2")) {
                startGame(op.equals("2"), s); // iniciar jogo (com ou sem GUI)
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void startGame(boolean useGui, Scanner s) {
        GameEngine game = new GameEngine();

        // pedir o ficheiro do mapa
        System.out.print("Enter map file (default: src/Map/map.json): ");
        String filename = s.nextLine().trim();

        String finalPath = null;

        if (filename.isEmpty()) {
            // tentar encontrar o mapa default em vários locais possíveis
            String[] defaultPaths = {
                    "src/Map/map.json",
                    "Map/map.json",
                    "ED/src/Map/map.json",
                    "../src/Map/map.json"
            };

            for (String path : defaultPaths) {
                if (new File(path).exists()) {
                    finalPath = path;
                    break;
                }
            }

            if (finalPath == null) {
                finalPath = "src/Map/map.json"; // fallback
            }
        } else {
            // adicionar .json automaticamente se não tiver a extensão
            if (!filename.endsWith(".json")) {
                filename = filename + ".json";
            }

            // tentar vários caminhos possíveis para encontrar o ficheiro
            String[] possiblePaths = {
                    filename, // path exato que o user deu
                    "src/Map/" + filename,
                    "Map/" + filename,
                    "ED/src/Map/" + filename,
                    "../src/Map/" + filename
            };

            // procurar em cada caminho possível
            for (String path : possiblePaths) {
                if (new File(path).exists()) {
                    finalPath = path;
                    break;
                }
            }

            // se não encontrou em nenhum, usar o que o user escreveu
            if (finalPath == null) {
                finalPath = filename;
            }
        }

        // verificar se o ficheiro existe
        File f = new File(finalPath);
        if (!f.exists()) {
            System.out.println("Error: File not found: " + finalPath);
            System.out.println("Tried to find map in multiple locations but couldn't find it.");
            return;
        }

        System.out.println("Loading map from: " + finalPath);
        game.loadMapData(finalPath);

        if (useGui) {
            game.enableGUI(); // ativar interface gráfica se foi escolhida
        }

        // procurar todas as salas de entrada no mapa
        LinkedUnorderedList<Room> entrances = new LinkedUnorderedList<>();
        java.util.Iterator<Room> it = game.getMap().getRooms();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getType().equals("ENTRANCE")) {
                entrances.addToRear(r);
            }
        }

        // converter para array para facilitar o acesso
        Object[] entArray = new Object[entrances.size()];
        int idx = 0;
        for (Room r : entrances)
            entArray[idx++] = r;

        // se não houver entradas, usar a primeira sala
        if (entrances.isEmpty()) {
            System.out.println("Warning: No ENTRANCE rooms found! Using first room as default.");
            entArray = new Object[1];
            entArray[0] = game.getMap().getRooms().next();
        }

        // escolher o modo de jogo (Manual ou Automático)
        System.out.println("\n=== GAME MODE SELECTION ===");
        System.out.println("1. Manual Mode (Human players + Bot)");
        System.out.println("2. Automatic Mode (Only Bots)");

        boolean isAutomaticMode = false;
        while (true) {
            System.out.print("Select mode (1-2): ");
            String modeChoice = s.nextLine().trim();
            if (modeChoice.equals("1")) {
                isAutomaticMode = false;
                System.out.println("\n✓ Manual Mode selected");
                break;
            } else if (modeChoice.equals("2")) {
                isAutomaticMode = true;
                System.out.println("\n✓ Automatic Mode selected");
                break;
            } else {
                System.out.println("Invalid! Please enter 1 or 2.");
            }
        }

        System.out.println("\n=== PLAYER CONFIGURATION ===");

        if (isAutomaticMode) {
            // AUTOMATIC MODE: Only bots (2-5)
            int numBots = 0;
            while (numBots < 2 || numBots > 5) {
                System.out.print("\nHow many bots? (2-5): ");
                try {
                    String input = s.nextLine().trim();
                    numBots = Integer.parseInt(input);
                    if (numBots < 2 || numBots > 5) {
                        System.out.println("Invalid! Please enter a number between 2 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number.");
                }
            }

            System.out.println("\nConfiguring bots...");

            // Add bots with random entrances
            String[] botNames = { "Bot Alpha", "Bot Beta", "Bot Gamma", "Bot Delta", "Bot Epsilon" };
            for (int i = 0; i < numBots; i++) {
                Room botStartRoom = (Room) entArray[0];
                if (entArray.length > 1) {
                    int randomIndex = (int) (Math.random() * entArray.length);
                    botStartRoom = (Room) entArray[randomIndex];
                }
                game.addPlayer(botNames[i], true, botStartRoom);
                System.out.println("✓ " + botNames[i] + " (Bot) added at " + botStartRoom.getId());
            }

            System.out.println("\n=== Starting Automatic Game ===");
            System.out.println("Total players: " + numBots + " bots");
            System.out.println("The game will run automatically. Watch the bots compete!");

        } else {
            // MANUAL MODE: 1 bot + 1-4 human players
            int numHumanPlayers = 0;
            while (numHumanPlayers < 1 || numHumanPlayers > 4) {
                System.out.print("\nHow many human players? (1-4): ");
                try {
                    String input = s.nextLine().trim();
                    numHumanPlayers = Integer.parseInt(input);
                    if (numHumanPlayers < 1 || numHumanPlayers > 4) {
                        System.out.println("Invalid! Please enter a number between 1 and 4.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number.");
                }
            }

            // ALWAYS add 1 bot first in manual mode
            Room botStartRoom = (Room) entArray[0];
            if (entArray.length > 1) {
                int randomIndex = (int) (Math.random() * entArray.length);
                botStartRoom = (Room) entArray[randomIndex];
            }
            game.addPlayer("Bot CPU", true, botStartRoom);
            System.out.println("\n✓ Bot CPU (Bot) automatically added at " + botStartRoom.getId());

            System.out.println("\nNow configure the human players:");

            // Configure each HUMAN player
            for (int i = 1; i <= numHumanPlayers; i++) {
                System.out.println("\n--- Human Player " + i + " ---");

                // Get player name
                System.out.print("Enter player name (default: Player " + i + "): ");
                String playerName = s.nextLine().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player " + i;
                }

                // Choose starting entrance
                Room startRoom = null;
                if (entArray.length == 1) {
                    startRoom = (Room) entArray[0];
                    System.out.println("Starting at: " + startRoom.getId());
                } else {
                    System.out.println("\nSelect starting entrance for " + playerName + ":");
                    for (int j = 0; j < entArray.length; j++) {
                        System.out.println((j + 1) + ". " + ((Room) entArray[j]).getId());
                    }

                    int choice = -1;
                    while (true) {
                        System.out.print("Choice (1-" + entArray.length + "): ");
                        try {
                            String input = s.nextLine().trim();
                            choice = Integer.parseInt(input);
                            if (choice >= 1 && choice <= entArray.length) {
                                startRoom = (Room) entArray[choice - 1];
                                break;
                            }
                            System.out
                                    .println("Invalid choice! Please enter a number between 1 and " + entArray.length);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input! Please enter a number.");
                        }
                    }
                }

                // Add human player to game (isBot = false)
                game.addPlayer(playerName, false, startRoom);
                System.out.println("✓ " + playerName + " (Human) added at " + startRoom.getId());
            }

            System.out.println("\n=== Starting Manual Game ===");
            System.out.println("Total players: " + (numHumanPlayers + 1) + " (1 bot + " + numHumanPlayers + " human)");
        }

        game.start();
    }
}