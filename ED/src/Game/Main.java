package Game;

import java.util.Scanner;
import java.io.File;
import Collections.ListasIterador.Classes.LinkedUnorderedList;

/**
 * Classe principal do jogo "Labirinto da Glória".
 * Ponto de entrada da aplicação. Gere o menu principal e
 * inicialização dos modos de jogo e editor de mapas.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class Main {
    /**
     * Método principal que inicia a aplicação.
     * 
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== LABIRINTO DA GLORIA ===");
            System.out.println("1. Jogar");
            System.out.println("2. Criar Mapa");
            System.out.println("0. Sair");
            System.out.print("Selecione opção: ");

            String op = s.next();
            s.nextLine();

            if (op.equals("0"))
                break;

            if (op.equals("2")) {
                MapEditor editor = new MapEditor();
                editor.generateAndSaveMap();
            } else if (op.equals("1")) {
                startGame(s);
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private static void startGame(Scanner s) {
        GameEngine game = new GameEngine();

        System.out.print("Ficheiro do mapa (padrão: src/Map/map.json): ");
        String filename = s.nextLine().trim();

        String finalPath = findMapFile(filename);

        File f = new File(finalPath);
        if (!f.exists()) {
            System.out.println("Erro: Ficheiro não encontrado: " + finalPath);
            return;
        }

        System.out.println("A carregar mapa de: " + finalPath);
        game.loadMapData(finalPath);

        LinkedUnorderedList<Room> entrances = new LinkedUnorderedList<>();
        java.util.Iterator<Room> it = game.getMap().getRooms();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getType().equals("ENTRANCE")) {
                entrances.addToRear(r);
            }
        }

        Object[] entArray = new Object[entrances.size()];
        int idx = 0;
        for (Room r : entrances)
            entArray[idx++] = r;

        if (entrances.isEmpty()) {
            System.out.println("Aviso: Nenhuma sala ENTRANCE encontrada! A usar primeira sala.");
            entArray = new Object[] { game.getMap().getRooms().next() };
        }

        System.out.println("\n=== CONFIGURAÇÃO DE JOGADORES ===");
        int numPlayers = getPlayerCount(s);

        int humanCount = 0;
        int botCount = 0;

        for (int i = 1; i <= numPlayers; i++) {
            System.out.println("\n--- Jogador " + i + " ---");

            System.out.print("Nome (padrão: Jogador " + i + "): ");
            String playerName = s.nextLine().trim();
            if (playerName.isEmpty()) {
                playerName = "Jogador " + i;
            }

            boolean isBot = askHumanOrBot(s);
            if (isBot)
                botCount++;
            else
                humanCount++;

            Room startRoom = selectStartRoom(s, entArray, playerName, isBot);

            game.addPlayer(playerName, isBot, startRoom);
            System.out.println("* " + playerName + " (" + (isBot ? "Bot" : "Humano") + ") em " + startRoom.getId());
        }

        System.out.println("\n=== A Iniciar Jogo ===");
        System.out.println("Total: " + numPlayers + " jogador" + (numPlayers > 1 ? "es" : ""));

        if (humanCount > 0 && botCount > 0) {
            System.out.println("(" + humanCount + " humano" + (humanCount > 1 ? "s" : "") + " + " + botCount + " bot"
                    + (botCount > 1 ? "s" : "") + ")");
        } else if (humanCount == 0) {
            System.out.println("(Todos bots - modo automático)");
        }

        game.start();
    }

    private static String findMapFile(String filename) {
        if (filename.isEmpty()) {
            String[] defaultPaths = {
                    "src/Map/map.json",
                    "Map/map.json",
                    "ED/src/Map/map.json",
                    "../src/Map/map.json"
            };
            for (String path : defaultPaths) {
                if (new File(path).exists())
                    return path;
            }
            return "src/Map/map.json";
        }

        if (!filename.endsWith(".json")) {
            filename += ".json";
        }

        String[] possiblePaths = {
                filename,
                "src/Map/" + filename,
                "Map/" + filename,
                "ED/src/Map/" + filename,
                "../src/Map/" + filename
        };

        for (String path : possiblePaths) {
            if (new File(path).exists())
                return path;
        }

        return filename;
    }

    private static int getPlayerCount(Scanner s) {
        int numPlayers = 0;
        while (numPlayers < 1 || numPlayers > 4) {
            System.out.print("Quantos jogadores? (1-4): ");
            try {
                numPlayers = Integer.parseInt(s.nextLine().trim());
                if (numPlayers < 1 || numPlayers > 4) {
                    System.out.println("Por favor, insira um número entre 1 e 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida!");
            }
        }
        return numPlayers;
    }

    private static boolean askHumanOrBot(Scanner s) {
        while (true) {
            System.out.print("Humano ou Bot? (H/B): ");
            String choice = s.nextLine().trim().toUpperCase();
            if (choice.equals("H") || choice.equals("HUMANO")) {
                return false;
            } else if (choice.equals("B") || choice.equals("BOT")) {
                return true;
            }
            System.out.println("Inválido! Digite H ou B.");
        }
    }

    private static Room selectStartRoom(Scanner s, Object[] entArray, String playerName, boolean isBot) {
        if (entArray.length == 1) {
            Room room = (Room) entArray[0];
            System.out.println("A começar em: " + room.getId());
            return room;
        }

        if (isBot) {
            Room room = (Room) entArray[(int) (Math.random() * entArray.length)];
            System.out.println("A começar em: " + room.getId() + " (aleatório)");
            return room;
        }

        System.out.println("\nSelecione entrada para " + playerName + ":");
        for (int j = 0; j < entArray.length; j++) {
            System.out.println((j + 1) + ". " + ((Room) entArray[j]).getId());
        }

        while (true) {
            System.out.print("Escolha (1-" + entArray.length + "): ");
            try {
                int choice = Integer.parseInt(s.nextLine().trim());
                if (choice >= 1 && choice <= entArray.length) {
                    return (Room) entArray[choice - 1];
                }
                System.out.println("Escolha entre 1 e " + entArray.length);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida!");
            }
        }
    }
}