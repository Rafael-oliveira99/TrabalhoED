package Game;

import java.util.Scanner;
import java.io.File;
import Collections.ListasIterador.Classes.LinkedUnorderedList;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== LABIRINTO DA GLORIA ===");
            System.out.println("1. Jogar (Consola)");
            System.out.println("2. Jogar (Interface Gráfica)");
            System.out.println("3. Editor de Mapas (Criar/Gerar)");
            System.out.println("0. Sair");
            System.out.print("Seleciona uma opção: ");

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
        System.out.print("Introduz ficheiro do mapa (default: src/Map/map.json): ");
        String filename = s.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "src/Map/map.json";
        } else if (!filename.endsWith(".json")) {
            filename += ".json";
        }

        // Check if file exists
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Erro: Ficheiro não encontrado: " + filename);
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
            System.out.println("Erro: Nenhuma sala 'ENTRANCE' encontrada! A usar a primeira sala.");
            startRoom = game.getMap().getRooms().next();
        } else if (entrances.size() == 1) {
            startRoom = entrances.first();
            System.out.println("Apenas uma entrada encontrada: " + startRoom.getId());
        } else {
            // Multiple entrances, ask user to choose
            System.out.println("\nSeleciona a entrada:");
            Object[] entArray = new Object[entrances.size()];
            int idx = 0;
            for (Room r : entrances)
                entArray[idx++] = r;

            for (int i = 0; i < entArray.length; i++) {
                System.out.println((i + 1) + ". " + ((Room) entArray[i]).getId());
            }

            int choice = -1;
            while (true) {
                System.out.print("Opção: ");
                try {
                    String input = s.nextLine().trim();
                    choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= entArray.length) {
                        startRoom = (Room) entArray[choice - 1];
                        break;
                    }
                } catch (NumberFormatException e) {
                }
                System.out.println("Inválido. Insere um número entre 1 e " + entArray.length);
            }
        }

        game.addPlayer("Player 1", false, startRoom);
        game.addPlayer("Bot Alpha", true, startRoom);

        game.start();
    }
}