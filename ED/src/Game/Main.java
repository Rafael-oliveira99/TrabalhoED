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
            System.out.println("1. Jogar");
            System.out.println("2. Editor de Mapas (Criar/Gerar)");
            System.out.println("0. Sair");
            System.out.print("Selecione opção: ");

            String op = s.next();
            s.nextLine(); // consumir o newline que sobra

            if (op.equals("0"))
                break; // sair do programa

            if (op.equals("2")) {
                // abrir o editor de mapas
                MapEditor editor = new MapEditor();
                editor.start();
            } else if (op.equals("1")) {
                startGame(s); // iniciar jogo
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private static void startGame(Scanner s) {
        GameEngine game = new GameEngine();

        // pedir o ficheiro do mapa
        System.out.print("Ficheiro do mapa (padrão: src/Map/map.json): ");
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
            System.out.println("Erro: Ficheiro não encontrado: " + finalPath);
            System.out.println("Tentou procurar o mapa em vários locais mas não foi encontrado.");
            return;
        }

        System.out.println("A carregar mapa de: " + finalPath);
        game.loadMapData(finalPath);

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
            System.out.println("Aviso: Nenhuma sala ENTRANCE encontrada! A usar primeira sala como padrão.");
            entArray = new Object[1];
            entArray[0] = game.getMap().getRooms().next();
        }

        // escolher quantos jogadores (1-4)
        System.out.println("\n=== CONFIGURAÇÃO DE JOGADORES ===");

        int numPlayers = 0;
        while (numPlayers < 1 || numPlayers > 4) {
            System.out.print("\nQuantos jogadores? (1-4): ");
            try {
                String input = s.nextLine().trim();
                numPlayers = Integer.parseInt(input);
                if (numPlayers < 1 || numPlayers > 4) {
                    System.out.println("Inválido! Por favor, insira um número entre 1 e 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Por favor, insira um número.");
            }
        }

        // Configurar cada jogador individualmente
        int humanCount = 0;
        int botCount = 0;

        for (int i = 1; i <= numPlayers; i++) {
            System.out.println("\n--- Jogador " + i + " ---");

            // Nome do jogador
            System.out.print("Nome do jogador (padrão: Jogador " + i + "): ");
            String playerName = s.nextLine().trim();
            if (playerName.isEmpty()) {
                playerName = "Jogador " + i;
            }

            // Humano ou Bot
            boolean isBot = false;
            while (true) {
                System.out.print("Humano ou Bot? (H/B): ");
                String choice = s.nextLine().trim().toUpperCase();
                if (choice.equals("H") || choice.equals("HUMANO")) {
                    isBot = false;
                    humanCount++;
                    break;
                } else if (choice.equals("B") || choice.equals("BOT")) {
                    isBot = true;
                    botCount++;
                    break;
                } else {
                    System.out.println("Inválido! Por favor, insira H (Humano) ou B (Bot).");
                }
            }

            // Escolher sala de entrada
            Room startRoom = null;
            if (entArray.length == 1) {
                startRoom = (Room) entArray[0];
                System.out.println("A começar em: " + startRoom.getId());
            } else {
                // Se for bot, escolher aleatoriamente
                if (isBot) {
                    int randomIndex = (int) (Math.random() * entArray.length);
                    startRoom = (Room) entArray[randomIndex];
                    System.out.println("A começar em: " + startRoom.getId() + " (aleatório)");
                } else {
                    // Se for humano, perguntar
                    System.out.println("\nSelecione entrada inicial para " + playerName + ":");
                    for (int j = 0; j < entArray.length; j++) {
                        System.out.println((j + 1) + ". " + ((Room) entArray[j]).getId());
                    }

                    int entranceChoice = -1;
                    while (true) {
                        System.out.print("Escolha (1-" + entArray.length + "): ");
                        try {
                            String input = s.nextLine().trim();
                            entranceChoice = Integer.parseInt(input);
                            if (entranceChoice >= 1 && entranceChoice <= entArray.length) {
                                startRoom = (Room) entArray[entranceChoice - 1];
                                break;
                            }
                            System.out.println(
                                    "Escolha inválida! Por favor, insira um número entre 1 e " + entArray.length);
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida! Por favor, insira um número.");
                        }
                    }
                }
            }

            // Adicionar jogador ao jogo
            game.addPlayer(playerName, isBot, startRoom);
            System.out.println("✓ " + playerName + " (" + (isBot ? "Bot" : "Humano") + ") adicionado em "
                    + startRoom.getId());
        }

        // Mensagem final
        System.out.println("\n=== A Iniciar Jogo ===");
        System.out.println("Total de jogadores: " + numPlayers);
        if (humanCount > 0 && botCount > 0) {
            System.out.println("(" + humanCount + " humano" + (humanCount > 1 ? "s" : "") + " + " + botCount + " bot"
                    + (botCount > 1 ? "s" : "") + ")");
        } else if (humanCount > 0) {
            System.out.println("(Todos jogadores humanos)");
        } else {
            System.out.println("(Todos bots - observe a competição!)");
            System.out.println("\n⚠️  MODO AUTOMÁTICO: Todos os jogadores são bots.");
            System.out.println("    O jogo vai correr automaticamente sem interação humana.");
            System.out.println("    Observe os bots a competir para encontrar o tesouro!\n");
        }

        game.start();
    }
}