package Game;

import java.util.Scanner;
import java.io.File;

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

            if (op.equals("0")) break;

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

        // Ask for start room name
        System.out.print("Enter Start Room Name (default: Entrada): ");
        String startName = s.nextLine().trim();
        if (startName.isEmpty()) startName = "Entrada";

        // FIX IS HERE: Added "0, 0" coordinates to satisfy the new Room constructor.
        // This is a temporary object; GameEngine will link it to the real map room.
        Room startRoom = new Room(startName, "ENTRANCE", "none", 0, 0);

        game.addPlayer("Player 1", false, startRoom);
        game.addPlayer("Bot Alpha", true, startRoom);

        game.start();
    }
}