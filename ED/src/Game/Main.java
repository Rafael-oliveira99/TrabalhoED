package Game;

import java.util.Scanner;
import java.io.File;
import Collections.ListasIterador.Classes.LinkedUnorderedList;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== LABIRINTO DA GLORIA ===");
            System.out.println("1. Play Game (Console)");
            System.out.println("2. Play Game (Graphical Interface)");
            System.out.println("3. Map Editor (Create/Generate)");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String op = s.next();
            s.nextLine(); // consume newline

            if (op.equals("0"))
                break;

            if (op.equals("3")) {
                MapEditor editor = new MapEditor();
                editor.start();
            } else if (op.equals("1") || op.equals("2")) {
                startGame(op.equals("2"), s);
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void startGame(boolean useGui, Scanner s) {
        GameEngine game = new GameEngine();

        // Ask for map file
        System.out.print("Enter map file (default: src/Map/map.json): ");
        String filename = s.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "src/Map/map.json";
        }

        // Check if file exists
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Error: File not found: " + filename);
            return;
        }

        game.loadMapData(filename);

        if (useGui) {
            game.enableGUI();
        }

        // Find all ENTRANCE rooms
        LinkedUnorderedList<Room> entrances = new LinkedUnorderedList<>();
        java.util.Iterator<Room> it = game.getMap().getRooms();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getType().equals("ENTRANCE")) {
                entrances.addToRear(r);
            }
        }

        Room startRoom = null;

        if (entrances.isEmpty()) {
            System.out.println("Error: No ENTRANCE rooms found in map! Defaulting to first room.");
            startRoom = game.getMap().getRooms().next();
        } else if (entrances.size() == 1) {
            startRoom = entrances.first();
            System.out.println("Only one entrance found: " + startRoom.getId());
        } else {
            // Multiple entrances, ask user to choose
            System.out.println("\nSelect a starting entrance:");
            Object[] entArray = new Object[entrances.size()];
            int idx = 0;
            for (Room r : entrances)
                entArray[idx++] = r;

            for (int i = 0; i < entArray.length; i++) {
                System.out.println((i + 1) + ". " + ((Room) entArray[i]).getId());
            }

            int choice = -1;
            while (true) {
                System.out.print("Choice: ");
                try {
                    String input = s.nextLine().trim();
                    choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= entArray.length) {
                        startRoom = (Room) entArray[choice - 1];
                        break;
                    }
                } catch (NumberFormatException e) {
                }
                System.out.println("Invalid choice. Please enter a number between 1 and " + entArray.length);
            }
        }

        game.addPlayer("Player 1", false, startRoom);
        game.addPlayer("Bot Alpha", true, startRoom);

        game.start();
    }
}