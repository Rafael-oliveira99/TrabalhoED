package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

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
    private String currentFilename; // Tracks currently loaded file

    public MapEditor() {
        rooms = new ArrayUnorderedList<>();
        connections = new ArrayUnorderedList<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("\n=== EDITOR DE MAPAS ===");
        boolean running = true;
        while (running) {
            System.out.println("0. Carregar Mapa Existente");
            System.out.println("1. Adicionar Sala (Manual)");
            System.out.println("2. Adicionar Conexão (Manual)");
            System.out.println("3. Gerar Mapa Aleatório (Versátil)");
            System.out.println("4. Ver & Editar Mapa Atual");
            System.out.println("5. Guardar & Sair");
            System.out.print("Escolher: ");
            String op = scanner.next();
            scanner.nextLine(); // consume newline

            switch (op) {
                case "0":
                    loadMap();
                    break;
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
                    editMenu();
                    break;
                case "5":
                    saveMap();
                    running = false;
                    break;
                default:
                    System.out.println("Inválido.");
            }
        }
    }

    private void addRoom() {
        System.out.print("ID da Sala: ");
        String id = scanner.nextLine();
        System.out.print("Tipo (ENTRANCE, TREASURE, NORMAL): ");
        String type = scanner.nextLine();
        System.out.print("Interação (none, enigma, lever): ");
        String interact = scanner.nextLine();

        System.out.print("Coordenada X (0-1000): ");
        int x = scanner.nextInt();
        System.out.print("Coordenada Y (0-800): ");
        int y = scanner.nextInt();
        scanner.nextLine();

        rooms.addToRear(new Room(id, type, interact, x, y));
        System.out.println("Sala adicionada.");
    }

    private void addConnection() {
        System.out.print("De: ");
        String from = scanner.nextLine();
        System.out.print("Para: ");
        String to = scanner.nextLine();
        System.out.print("Custo: ");
        int cost = scanner.nextInt();
        scanner.nextLine();
        connections.addToRear(new TempConnection(from, to, cost));
    }

    /**
     * GERADOR AUTOMÁTICO DE MAPAS MELHORADO
     * Cria um mapa não-linear e ramificado com múltiplas entradas e tesouro no
     * centro.
     */
    private void generateRandomMap() {
        System.out.print("Quantas salas? (mín 8, máx 30): ");
        int count = scanner.nextInt();
        scanner.nextLine();
        if (count < 8)
            count = 8;
        if (count > 30)
            count = 30;

        System.out.print("Quantas entradas? (mín 2, máx 5): ");
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
            String interact = interactions[(int) (Math.random() * interactions.length)];

            // Pick a random existing room to attach to (can be treasure, entrance, or other
            // room)
            int parentIndex = (int) (Math.random() * currentRoomIndex);
            Room parent = tempRooms[parentIndex];

            // Calculate Coordinates (Random direction from parent)
            // Distance roughly 100-200 pixels
            double angle = Math.random() * 2 * Math.PI;
            int dist = 100 + (int) (Math.random() * 100);
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

        System.out.println("Estrutura base gerada. A adicionar complexidade...");

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
                int targetIndex = (int) (Math.random() * count);
                if (targetIndex != e) { // Don't connect to itself
                    connections.addToRear(new TempConnection(tempRooms[e].getId(), tempRooms[targetIndex].getId(), 1));
                }
            }
        }

        // 5. Add Extra Edges (Cycles) & Locks
        // Randomly connect some rooms to create loops, making it a maze graph
        int extraEdges = count / 3;
        for (int k = 0; k < extraEdges; k++) {
            int i1 = (int) (Math.random() * count);
            int i2 = (int) (Math.random() * count);

            if (i1 != i2) {
                // IMPORTANTE: Nunca bloquear conexões que envolvem o tesouro!
                // O tesouro é sempre tempRooms[0]
                boolean isTreasureConnection = (i1 == 0 || i2 == 0);

                int cost;
                if (isTreasureConnection) {
                    cost = 1; // conexões ao tesouro SEMPRE abertas
                } else {
                    cost = ((int) (Math.random() * 100) < 30) ? 1000 : 1; // 30% chance de bloquear OUTRAS conexões
                }

                connections.addToRear(new TempConnection(tempRooms[i1].getId(), tempRooms[i2].getId(), cost));
            }
        }

        System.out.println("Mapa gerado com sucesso!");
        System.out.println("Total de Salas: " + count);
        System.out.println("Entradas: " + numEntrances);
        System.out.println("Tesouro está no CENTRO!");
        System.out.println("Não se esqueça de GUARDAR.");
    }

    /**
     * Carregar mapa existente de um ficheiro JSON
     */
    private void loadMap() {
        System.out.print("Nome do ficheiro a carregar (ex: teste.json): ");
        String filename = scanner.nextLine();
        if (!filename.endsWith(".json"))
            filename += ".json";

        try {
            // Read entire file content
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            // Simple JSON parsing (manual)
            String json = jsonContent.toString();

            // Clear current data
            rooms = new ArrayUnorderedList<>();
            connections = new ArrayUnorderedList<>();

            // Parse rooms
            int roomsStart = json.indexOf("\"rooms\":");
            int roomsArrayStart = json.indexOf("[", roomsStart);
            int roomsArrayEnd = json.indexOf("]", roomsArrayStart);
            String roomsSection = json.substring(roomsArrayStart + 1, roomsArrayEnd);

            // Split by objects (simple parsing)
            String[] roomObjects = roomsSection.split("\\},\\s*\\{");
            for (String roomObj : roomObjects) {
                roomObj = roomObj.replace("{", "").replace("}", "").trim();
                if (roomObj.isEmpty())
                    continue;

                String id = extractJsonValue(roomObj, "room");
                String type = extractJsonValue(roomObj, "type");
                String interaction = extractJsonValue(roomObj, "interaction");
                int x = Integer.parseInt(extractJsonValue(roomObj, "x"));
                int y = Integer.parseInt(extractJsonValue(roomObj, "y"));

                rooms.addToRear(new Room(id, type, interaction, x, y));
            }

            // Parse connections
            int connectionsStart = json.indexOf("\"connections\":");
            int connectionsArrayStart = json.indexOf("[", connectionsStart);
            int connectionsArrayEnd = json.indexOf("]", connectionsArrayStart);
            String connectionsSection = json.substring(connectionsArrayStart + 1, connectionsArrayEnd);

            String[] connObjects = connectionsSection.split("\\},\\s*\\{");
            for (String connObj : connObjects) {
                connObj = connObj.replace("{", "").replace("}", "").trim();
                if (connObj.isEmpty())
                    continue;

                String from = extractJsonValue(connObj, "from");
                String to = extractJsonValue(connObj, "to");
                int cost = Integer.parseInt(extractJsonValue(connObj, "cost"));

                connections.addToRear(new TempConnection(from, to, cost));
            }

            currentFilename = filename;
            System.out.println("Carregado com sucesso '" + filename + "'");
            System.out.println("Salas: " + countRooms() + ", Conexões: " + countConnections());

        } catch (Exception e) {
            System.out.println("Erro ao carregar ficheiro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extrator simples de valores JSON
     */
    private String extractJsonValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":");
        if (keyIndex == -1)
            return "";

        int valueStart = json.indexOf(":", keyIndex) + 1;
        int valueEnd = json.indexOf(",", valueStart);
        if (valueEnd == -1)
            valueEnd = json.length();

        String value = json.substring(valueStart, valueEnd).trim();
        // Remove quotes if present
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Menu de edição para mapas carregados/criados
     */
    private void editMenu() {
        if (countRooms() == 0) {
            System.out.println("Nenhum mapa carregado. Por favor, carregue ou crie um mapa primeiro.");
            return;
        }

        boolean editing = true;
        while (editing) {
            System.out.println("\n=== MENU DE EDIÇÃO ===");
            System.out.println("1. Ver Todas as Salas");
            System.out.println("2. Editar Sala");
            System.out.println("3. Remover Sala");
            System.out.println("4. Ver Todas as Conexões");
            System.out.println("5. Editar Conexão");
            System.out.println("6. Remover Conexão");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolher: ");
            String choice = scanner.next();
            scanner.nextLine();

            switch (choice) {
                case "1":
                    viewRooms();
                    break;
                case "2":
                    editRoom();
                    break;
                case "3":
                    removeRoom();
                    break;
                case "4":
                    viewConnections();
                    break;
                case "5":
                    editConnection();
                    break;
                case "6":
                    removeConnection();
                    break;
                case "0":
                    editing = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void viewRooms() {
        System.out.println("\n=== TODAS AS SALAS ===");
        Iterator<Room> it = rooms.iterator();
        int count = 1;
        while (it.hasNext()) {
            Room r = it.next();
            System.out.println(count + ". " + r.getId() + " [" + r.getType() + "] - Interação: "
                    + r.getInteraction() + " - Coords: (" + r.getX() + ", " + r.getY() + ")");
            count++;
        }
        System.out.println("Total: " + (count - 1) + " salas");
    }

    private void editRoom() {
        viewRooms();
        System.out.print("\nInsira o ID da Sala a editar: ");
        String targetId = scanner.nextLine();

        Room targetRoom = findRoom(targetId);
        if (targetRoom == null) {
            System.out.println("Sala não encontrada.");
            return;
        }

        System.out.println("A editar: " + targetRoom.getId());
        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Novo ID [" + targetRoom.getId() + "]: ");
        String newId = scanner.nextLine();
        if (!newId.trim().isEmpty()) {
            // Update connections that reference this room
            updateConnectionsForRoomRename(targetRoom.getId(), newId);
            targetRoom.setId(newId);
        }

        System.out.print("Novo Tipo [" + targetRoom.getType() + "]: ");
        String newType = scanner.nextLine();
        if (!newType.trim().isEmpty())
            targetRoom.setType(newType);

        System.out.print("Nova Interação [" + targetRoom.getInteraction() + "]: ");
        String newInteraction = scanner.nextLine();
        if (!newInteraction.trim().isEmpty())
            targetRoom.setInteraction(newInteraction);

        System.out.print("Novo X [" + targetRoom.getX() + "]: ");
        String xStr = scanner.nextLine();
        if (!xStr.trim().isEmpty())
            targetRoom.setX(Integer.parseInt(xStr));

        System.out.print("Novo Y [" + targetRoom.getY() + "]: ");
        String yStr = scanner.nextLine();
        if (!yStr.trim().isEmpty())
            targetRoom.setY(Integer.parseInt(yStr));

        System.out.println("Sala atualizada com sucesso!");
    }

    private void removeRoom() {
        viewRooms();
        System.out.print("\nInsira o ID da Sala a remover: ");
        String targetId = scanner.nextLine();

        Room targetRoom = findRoom(targetId);
        if (targetRoom == null) {
            System.out.println("Sala não encontrada.");
            return;
        }

        // Remove associated connections
        ArrayUnorderedList<TempConnection> newConnections = new ArrayUnorderedList<>();
        Iterator<TempConnection> it = connections.iterator();
        while (it.hasNext()) {
            TempConnection conn = it.next();
            if (!conn.from.equals(targetId) && !conn.to.equals(targetId)) {
                newConnections.addToRear(conn);
            }
        }
        connections = newConnections;

        // Remove room
        rooms.remove(targetRoom);
        System.out.println("Sala '" + targetId + "' e suas conexões removidas.");
    }

    private void viewConnections() {
        System.out.println("\n=== TODAS AS CONEXÕES ===");
        Iterator<TempConnection> it = connections.iterator();
        int count = 1;
        while (it.hasNext()) {
            TempConnection c = it.next();
            System.out.println(count + ". " + c.from + " <-> " + c.to + " (Custo: " + c.cost + ")");
            count++;
        }
        System.out.println("Total: " + (count - 1) + " conexões");
    }

    private void editConnection() {
        viewConnections();
        System.out.print("\nNúmero da conexão a editar: ");
        int num = scanner.nextInt();
        scanner.nextLine();

        TempConnection conn = getConnectionByIndex(num - 1);
        if (conn == null) {
            System.out.println("Número de conexão inválido.");
            return;
        }

        System.out.println("A editar: " + conn.from + " <-> " + conn.to + " (Custo: " + conn.cost + ")");
        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Novo De [" + conn.from + "]: ");
        String newFrom = scanner.nextLine();
        if (!newFrom.trim().isEmpty())
            conn.from = newFrom;

        System.out.print("Novo Para [" + conn.to + "]: ");
        String newTo = scanner.nextLine();
        if (!newTo.trim().isEmpty())
            conn.to = newTo;

        System.out.print("Novo Custo [" + conn.cost + "]: ");
        String costStr = scanner.nextLine();
        if (!costStr.trim().isEmpty())
            conn.cost = Integer.parseInt(costStr);

        System.out.println("Conexão atualizada com sucesso!");
    }

    private void removeConnection() {
        viewConnections();
        System.out.print("\nNúmero da conexão a remover: ");
        int num = scanner.nextInt();
        scanner.nextLine();

        TempConnection conn = getConnectionByIndex(num - 1);
        if (conn == null) {
            System.out.println("Número de conexão inválido.");
            return;
        }

        connections.remove(conn);
        System.out.println("Conexão removida.");
    }

    // Métodos auxiliares
    private Room findRoom(String id) {
        Iterator<Room> it = rooms.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getId().equals(id))
                return r;
        }
        return null;
    }

    private TempConnection getConnectionByIndex(int index) {
        Iterator<TempConnection> it = connections.iterator();
        int count = 0;
        while (it.hasNext()) {
            TempConnection c = it.next();
            if (count == index)
                return c;
            count++;
        }
        return null;
    }

    private void updateConnectionsForRoomRename(String oldId, String newId) {
        Iterator<TempConnection> it = connections.iterator();
        while (it.hasNext()) {
            TempConnection c = it.next();
            if (c.from.equals(oldId))
                c.from = newId;
            if (c.to.equals(oldId))
                c.to = newId;
        }
    }

    private int countRooms() {
        int count = 0;
        Iterator<Room> it = rooms.iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    private int countConnections() {
        int count = 0;
        Iterator<TempConnection> it = connections.iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    private void saveMap() {
        String filename;

        // If a file was loaded, offer to overwrite
        if (currentFilename != null && !currentFilename.isEmpty()) {
            System.out.print("Sobrescrever '" + currentFilename + "'? (s/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("s") || response.equals("sim") || response.equals("y") || response.equals("yes")) {
                filename = currentFilename;
                System.out.println("A sobrescrever '" + filename + "'...");
            } else {
                System.out.print("Insira novo nome do ficheiro (ex: mapa.json): ");
                filename = scanner.nextLine();
                if (!filename.endsWith(".json"))
                    filename += ".json";
            }
        } else {
            System.out.print("Insira nome do ficheiro (ex: mapa.json): ");
            filename = scanner.nextLine();
            if (!filename.endsWith(".json"))
                filename += ".json";
        }

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
            System.out.println("Guardado em " + filename);
            currentFilename = filename; // Update current filename
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}