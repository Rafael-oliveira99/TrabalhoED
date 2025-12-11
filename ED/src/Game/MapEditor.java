package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Random;

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
    private Random rand;

    public MapEditor() {
        rooms = new ArrayUnorderedList<>();
        connections = new ArrayUnorderedList<>();
        scanner = new Scanner(System.in);
        rand = new Random();
    }

    public void start() {
        System.out.println("\n=== MAP EDITOR ===");
        boolean running = true;
        while (running) {
            System.out.println("1. Add Room (Manual)");
            System.out.println("2. Add Connection (Manual)");
            System.out.println("3. Generate Random Map (Versatile)");
            System.out.println("4. Save & Exit");
            System.out.print("Choose: ");
            String op = scanner.next();
            scanner.nextLine(); // consume newline

            switch (op) {
                case "1":
                    addRoom();
                    break;
                case "2":
                    addConnection();
                    break;
                case "3":
                    generateRandomMap();
                    break;
                case "4":
                    saveMap();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private void addRoom() {
        System.out.print("Room ID: ");
        String id = scanner.nextLine();
        System.out.print("Type (ENTRANCE, TREASURE, NORMAL): ");
        String type = scanner.nextLine();
        System.out.print("Interaction (none, enigma, lever): ");
        String interact = scanner.nextLine();

        System.out.print("X Coordinate (0-1000): ");
        int x = scanner.nextInt();
        System.out.print("Y Coordinate (0-800): ");
        int y = scanner.nextInt();
        scanner.nextLine();

        rooms.addToRear(new Room(id, type, interact, x, y));
        System.out.println("Room added.");
    }

    private void addConnection() {
        System.out.print("From: ");
        String from = scanner.nextLine();
        System.out.print("To: ");
        String to = scanner.nextLine();
        System.out.print("Cost: ");
        int cost = scanner.nextInt();
        scanner.nextLine();
        connections.addToRear(new TempConnection(from, to, cost));
    }

    /**
     * IMPROVED AUTOMATIC MAP GENERATOR
     * Creates a non-linear, branching map with multiple entrances and treasure at
     * center.
     */
    private void generateRandomMap() {
        System.out.print("How many rooms? (min 8, max 30): ");
        int count = scanner.nextInt();
        scanner.nextLine();
        if (count < 8)
            count = 8;
        if (count > 30)
            count = 30;

        System.out.print("How many entrances? (min 2, max 5): ");
        int numEntrances = scanner.nextInt();
        scanner.nextLine();
        if (numEntrances < 2)
            numEntrances = 2;
        if (numEntrances > 5)
            numEntrances = 5;

        // Reset lists
        rooms = new ArrayUnorderedList<>();
        connections = new ArrayUnorderedList<>();

        // Temporary array for logic (Allowed as it is standard Java, not a Collection)
        Room[] tempRooms = new Room[count];
        String[] interactions = { "none", "none", "enigma", "lever", "none", "enigma" };

        // 1. Create Treasure Room at Center
        tempRooms[0] = new Room("Tesouro", "TREASURE", "none", 500, 400);
        rooms.addToRear(tempRooms[0]);

        // 2. Create Multiple Entrances around the edges
        String[] directions = { "Norte", "Sul", "Leste", "Oeste", "Nordeste", "Sudeste", "Sudoeste", "Noroeste" };
        int[][] entrancePositions = {
                { 500, 50 }, // Norte (top center)
                { 500, 750 }, // Sul (bottom center)
                { 950, 400 }, // Leste (right center)
                { 50, 400 }, // Oeste (left center)
                { 800, 100 }, // Nordeste
                { 800, 700 }, // Sudeste
                { 200, 700 }, // Sudoeste
                { 200, 100 } // Noroeste
        };

        int entranceIndex = 1;
        for (int e = 0; e < numEntrances && e < directions.length; e++) {
            String entranceName = "Entrada " + directions[e];
            tempRooms[entranceIndex] = new Room(entranceName, "ENTRANCE", "none",
                    entrancePositions[e][0], entrancePositions[e][1]);
            rooms.addToRear(tempRooms[entranceIndex]);
            entranceIndex++;
        }

        // 3. Generate Normal Rooms between entrances and treasure
        int currentRoomIndex = numEntrances + 1;
        int normalRoomsCount = count - numEntrances - 1; // Total - entrances - treasure

        for (int i = 0; i < normalRoomsCount; i++) {
            String name = "Sala " + (i + 1);
            String interact = interactions[rand.nextInt(interactions.length)];

            // Pick a random existing room to attach to (can be treasure, entrance, or other
            // room)
            int parentIndex = rand.nextInt(currentRoomIndex);
            Room parent = tempRooms[parentIndex];

            // Calculate Coordinates (Random direction from parent)
            // Distance roughly 100-200 pixels
            double angle = rand.nextDouble() * 2 * Math.PI;
            int dist = 100 + rand.nextInt(100);
            int newX = parent.getX() + (int) (Math.cos(angle) * dist);
            int newY = parent.getY() + (int) (Math.sin(angle) * dist);

            // Boundary checks
            if (newX < 50)
                newX = 50;
            if (newX > 950)
                newX = 950;
            if (newY < 50)
                newY = 50;
            if (newY > 750)
                newY = 750;

            Room newRoom = new Room(name, "NORMAL", interact, newX, newY);
            tempRooms[currentRoomIndex] = newRoom;
            rooms.addToRear(newRoom);

            // Connect Parent <-> Child (Cost 1 initially)
            connections.addToRear(new TempConnection(parent.getId(), newRoom.getId(), 1));
            currentRoomIndex++;
        }

        System.out.println("Backbone generated. Adding complexity...");

        // 4. Ensure all entrances have a path toward the treasure
        // Connect each entrance to at least one room (possibly the treasure itself)
        for (int e = 1; e <= numEntrances; e++) {
            // Check if entrance already has connections
            boolean hasConnection = false;
            Iterator<TempConnection> it = connections.iterator();
            while (it.hasNext()) {
                TempConnection conn = it.next();
                if (conn.from.equals(tempRooms[e].getId()) || conn.to.equals(tempRooms[e].getId())) {
                    hasConnection = true;
                    break;
                }
            }

            // If no connection, connect to a random room or treasure
            if (!hasConnection) {
                int targetIndex = rand.nextInt(count);
                if (targetIndex != e) { // Don't connect to itself
                    connections.addToRear(new TempConnection(tempRooms[e].getId(), tempRooms[targetIndex].getId(), 1));
                }
            }
        }

        // 5. Add Extra Edges (Cycles) & Locks
        // Randomly connect some rooms to create loops, making it a maze graph
        int extraEdges = count / 3;
        for (int k = 0; k < extraEdges; k++) {
            int i1 = rand.nextInt(count);
            int i2 = rand.nextInt(count);

            if (i1 != i2) {
                // IMPORTANTE: Nunca bloquear conexões que envolvem o tesouro!
                // O tesouro é sempre tempRooms[0]
                boolean isTreasureConnection = (i1 == 0 || i2 == 0);

                int cost;
                if (isTreasureConnection) {
                    cost = 1; // conexões ao tesouro SEMPRE abertas
                } else {
                    cost = (rand.nextInt(100) < 30) ? 1000 : 1; // 30% chance de bloquear OUTRAS conexões
                }

                connections.addToRear(new TempConnection(tempRooms[i1].getId(), tempRooms[i2].getId(), cost));
            }
        }

        System.out.println("Map generated successfully!");
        System.out.println("Total Rooms: " + count);
        System.out.println("Entrances: " + numEntrances);
        System.out.println("Treasure is at the CENTER!");
        System.out.println("Don't forget to SAVE.");
    }

    private void saveMap() {
        System.out.print("Enter filename to save (e.g., random.json): ");
        String filename = scanner.nextLine();
        if (!filename.endsWith(".json"))
            filename += ".json";

        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("{\n  \"rooms\": [\n");
            Iterator<Room> itRooms = rooms.iterator();
            while (itRooms.hasNext()) {
                Room r = itRooms.next();
                fw.write("    { \"room\": \"" + r.getId() + "\", \"type\": \"" + r.getType() + "\", \"interaction\": \""
                        + r.getInteraction() + "\", \"x\": " + r.getX() + ", \"y\": " + r.getY() + " }");
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
            System.out.println("Saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}