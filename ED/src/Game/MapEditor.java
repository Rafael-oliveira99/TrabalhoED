package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Editor de mapas do jogo.
 * Permite gerar mapas aleatórios e guardá-los em formato JSON.
 * 
 * @author Rafael Oliveira e Francisco Gomes (Grupo 26)
 * @version 1.0
 */
public class MapEditor {

    private class TempConnection {
        String from, to;
        int cost;

        public TempConnection(String f, String t, int c) {
            from = f;
            to = t;
            cost = c;
        }
    }

    private ArrayUnorderedList<Room> rooms;
    private ArrayUnorderedList<TempConnection> connections;
    private Scanner scanner;

    public MapEditor() {
        rooms = new ArrayUnorderedList<>();
        connections = new ArrayUnorderedList<>();
        scanner = new Scanner(System.in);
    }

    public void generateAndSaveMap() {
        System.out.println("\n=== CRIAR MAPA ===");
        generateRandomMap();
        saveMap();
    }

    private void generateRandomMap() {
        System.out.print("Quantas salas? (8-30): ");
        int count = Math.max(8, Math.min(30, scanner.nextInt()));
        scanner.nextLine();

        System.out.print("Quantas entradas? (2-5): ");
        int numEntrances = Math.max(2, Math.min(5, scanner.nextInt()));
        scanner.nextLine();

        rooms = new ArrayUnorderedList<>();
        connections = new ArrayUnorderedList<>();

        Room[] tempRooms = new Room[count];
        String[] interactions = { "none", "none", "enigma", "lever", "none", "enigma" };

        // 1. Tesouro no centro
        tempRooms[0] = new Room("Tesouro", "TREASURE", "none", 500, 400);
        rooms.addToRear(tempRooms[0]);

        // 2. Criar entradas
        String[] directions = { "Norte", "Sul", "Leste", "Oeste", "Nordeste", "Sudeste", "Sudoeste", "Noroeste" };
        int[][] entrancePositions = {
                { 500, 50 }, { 500, 750 }, { 950, 400 }, { 50, 400 },
                { 800, 100 }, { 800, 700 }, { 200, 700 }, { 200, 100 }
        };

        int entranceIndex = 1;
        for (int e = 0; e < numEntrances && e < directions.length; e++) {
            tempRooms[entranceIndex] = new Room("Entrada " + directions[e], "ENTRANCE", "none",
                    entrancePositions[e][0], entrancePositions[e][1]);
            rooms.addToRear(tempRooms[entranceIndex]);
            entranceIndex++;
        }

        // 3. Salas normais
        int currentIndex = numEntrances + 1;
        int normalRooms = count - numEntrances - 1;

        for (int i = 0; i < normalRooms; i++) {
            String interact = interactions[(int) (Math.random() * interactions.length)];
            int parentIndex = (int) (Math.random() * currentIndex);
            Room parent = tempRooms[parentIndex];

            double angle = Math.random() * 2 * Math.PI;
            int dist = 100 + (int) (Math.random() * 100);
            int newX = Math.max(50, Math.min(950, parent.getX() + (int) (Math.cos(angle) * dist)));
            int newY = Math.max(50, Math.min(750, parent.getY() + (int) (Math.sin(angle) * dist)));

            Room newRoom = new Room("Sala " + (i + 1), "NORMAL", interact, newX, newY);
            tempRooms[currentIndex] = newRoom;
            rooms.addToRear(newRoom);
            connections.addToRear(new TempConnection(parent.getId(), newRoom.getId(), 1));
            currentIndex++;
        }

        // 4. Garantir que entradas têm conexões
        for (int e = 1; e <= numEntrances; e++) {
            boolean hasConnection = false;
            Iterator<TempConnection> it = connections.iterator();
            while (it.hasNext()) {
                TempConnection conn = it.next();
                if (conn.from.equals(tempRooms[e].getId()) || conn.to.equals(tempRooms[e].getId())) {
                    hasConnection = true;
                    break;
                }
            }

            if (!hasConnection) {
                int targetIndex = (int) (Math.random() * count);
                if (targetIndex != e) {
                    connections.addToRear(new TempConnection(tempRooms[e].getId(), tempRooms[targetIndex].getId(), 1));
                }
            }
        }

        // 5. Adicionar complexidade com loops e portas trancadas
        int extraEdges = count / 3;
        for (int k = 0; k < extraEdges; k++) {
            int i1 = (int) (Math.random() * count);
            int i2 = (int) (Math.random() * count);

            if (i1 != i2) {
                boolean isTreasure = (i1 == 0 || i2 == 0);
                int cost = isTreasure ? 1 : (Math.random() < 0.3 ? 1000 : 1);
                connections.addToRear(new TempConnection(tempRooms[i1].getId(), tempRooms[i2].getId(), cost));
            }
        }

        System.out.println("Mapa gerado!");
        System.out.println("Salas: " + count + ", Entradas: " + numEntrances);
    }

    private void saveMap() {
        System.out.print("Nome do ficheiro (ex: mapa.json): ");
        String filename = scanner.nextLine();
        if (!filename.endsWith(".json"))
            filename += ".json";

        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("{\n  \"rooms\": [\n");
            Iterator<Room> itRooms = rooms.iterator();
            while (itRooms.hasNext()) {
                Room r = itRooms.next();
                fw.write("    { \"room\": \"" + r.getId() + "\", \"type\": \"" + r.getType() +
                        "\", \"interaction\": \"" + r.getInteraction() + "\", \"x\": " + r.getX() +
                        ", \"y\": " + r.getY() + " }");
                if (itRooms.hasNext())
                    fw.write(",");
                fw.write("\n");
            }
            fw.write("  ],\n  \"connections\": [\n");
            Iterator<TempConnection> itConns = connections.iterator();
            while (itConns.hasNext()) {
                TempConnection c = itConns.next();
                fw.write("    { \"from\": \"" + c.from + "\", \"to\": \"" + c.to + "\", \"cost\": " + c.cost + " }");
                if (itConns.hasNext())
                    fw.write(",");
                fw.write("\n");
            }
            fw.write("  ]\n}");
            System.out.println("Guardado em " + filename);
        } catch (IOException e) {
            System.out.println("Erro ao guardar: " + e.getMessage());
        }
    }
}