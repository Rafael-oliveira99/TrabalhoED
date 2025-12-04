package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLoader {

    public static void loadMap(String filePath, MazeMap mazeMap) {
        String jsonContent = readFile(filePath);

        if (jsonContent.isEmpty()) {
            System.out.println("Error: Map file is empty or not found.");
            return;
        }

        // --- 1. Parse Rooms (Updated Regex for X and Y) ---
        String roomsBlock = extractBlock(jsonContent, "\"rooms\"");
        if (roomsBlock != null) {
            // Looks for: { "room": "A", "type": "B", "interaction": "C", "x": 100, "y": 200 }
            Pattern roomPattern = Pattern.compile("\\{\\s*\"room\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"type\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"interaction\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"x\"\\s*:\\s*(\\d+)\\s*,\\s*\"y\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher m = roomPattern.matcher(roomsBlock);

            while (m.find()) {
                String id = m.group(1);
                String type = m.group(2);
                String interaction = m.group(3);
                int x = Integer.parseInt(m.group(4));
                int y = Integer.parseInt(m.group(5));

                Room newRoom = new Room(id, type, interaction, x, y);
                mazeMap.addRoom(newRoom);
            }
        }

        // --- 2. Parse Connections (Unchanged) ---
        String connectionsBlock = extractBlock(jsonContent, "\"connections\"");
        if (connectionsBlock != null) {
            Pattern connPattern = Pattern.compile("\\{\\s*\"from\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"to\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"cost\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher m = connPattern.matcher(connectionsBlock);

            while (m.find()) {
                String from = m.group(1);
                String to = m.group(2);
                double cost = Double.parseDouble(m.group(3));

                Room r1 = findRoom(mazeMap, from);
                Room r2 = findRoom(mazeMap, to);

                if (r1 != null && r2 != null) {
                    mazeMap.addCorridor(r1, r2, cost);
                }
            }
        }
    }

    // ... (loadEnigmas, readFile, extractBlock, findRoom methods remain exactly the same) ...

    public static ArrayUnorderedList<Enigma> loadEnigmas(String filePath) {
        ArrayUnorderedList<Enigma> list = new ArrayUnorderedList<>();
        String jsonContent = readFile(filePath);
        Pattern enigmaPattern = Pattern.compile("\\{\\s*\"question\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"answer\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher m = enigmaPattern.matcher(jsonContent);
        while (m.find()) {
            list.addToRear(new Enigma(m.group(1), m.group(2)));
        }
        return list;
    }

    private static String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) content.append(line.trim());
        } catch (IOException e) { e.printStackTrace(); }
        return content.toString();
    }

    private static String extractBlock(String content, String key) {
        int keyIndex = content.indexOf(key);
        if (keyIndex == -1) return null;
        int startBracket = content.indexOf("[", keyIndex);
        int endBracket = content.indexOf("]", startBracket);
        if (startBracket != -1 && endBracket != -1) return content.substring(startBracket, endBracket + 1);
        return null;
    }

    private static Room findRoom(MazeMap map, String id) {
        return map.getRoom(id);
    }
}