package Game;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    public static void loadMap(String filePath, MazeMap mazeMap) {
        String jsonContent = readFile(filePath);

        if (jsonContent.isEmpty()) {
            System.out.println("Erro: Ficheiro do mapa vazio ou não encontrado.");
            return;
        }

        // --- 1. Analisar Salas (Parsing Manual) ---
        String roomsBlock = extractBlock(jsonContent, "\"rooms\"");
        if (roomsBlock != null) {
            // Parsing manual sem regex
            int pos = 0;
            while (pos < roomsBlock.length()) {
                int openBrace = roomsBlock.indexOf("{", pos);
                if (openBrace == -1)
                    break;

                int closeBrace = roomsBlock.indexOf("}", openBrace);
                if (closeBrace == -1)
                    break;

                String roomObj = roomsBlock.substring(openBrace, closeBrace + 1);

                // Extrair valores usando funções auxiliares
                String id = extractJsonValue(roomObj, "room");
                String type = extractJsonValue(roomObj, "type");
                String interaction = extractJsonValue(roomObj, "interaction");
                String xStr = extractJsonValue(roomObj, "x");
                String yStr = extractJsonValue(roomObj, "y");

                if (!id.isEmpty() && !xStr.isEmpty() && !yStr.isEmpty()) {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);
                    Room newRoom = new Room(id, type, interaction, x, y);
                    mazeMap.addRoom(newRoom);
                }

                pos = closeBrace + 1;
            }
        }

        // --- 2. Analisar Conexões (Parsing Manual) ---
        String connectionsBlock = extractBlock(jsonContent, "\"connections\"");
        if (connectionsBlock != null) {
            // Parsing manual sem regex
            int pos = 0;
            while (pos < connectionsBlock.length()) {
                int openBrace = connectionsBlock.indexOf("{", pos);
                if (openBrace == -1)
                    break;

                int closeBrace = connectionsBlock.indexOf("}", openBrace);
                if (closeBrace == -1)
                    break;

                String connObj = connectionsBlock.substring(openBrace, closeBrace + 1);

                String from = extractJsonValue(connObj, "from");
                String to = extractJsonValue(connObj, "to");
                String costStr = extractJsonValue(connObj, "cost");

                if (!from.isEmpty() && !to.isEmpty() && !costStr.isEmpty()) {
                    double cost = Double.parseDouble(costStr);
                    Room r1 = findRoom(mazeMap, from);
                    Room r2 = findRoom(mazeMap, to);

                    if (r1 != null && r2 != null) {
                        mazeMap.addCorridor(r1, r2, cost);
                    }
                }

                pos = closeBrace + 1;
            }
        }
    }

    // ... (loadEnigmas, readFile, extractBlock, findRoom métodos permanecem
    // exatamente iguais) ...

    public static ArrayUnorderedList<Enigma> loadEnigmas(String filePath) {
        ArrayUnorderedList<Enigma> list = new ArrayUnorderedList<>();
        String jsonContent = readFile(filePath);

        // Parsing manual sem regex
        int pos = 0;
        while (pos < jsonContent.length()) {
            int openBrace = jsonContent.indexOf("{", pos);
            if (openBrace == -1)
                break;

            int closeBrace = jsonContent.indexOf("}", openBrace);
            if (closeBrace == -1)
                break;

            String enigmaObj = jsonContent.substring(openBrace, closeBrace + 1);

            String question = extractJsonValue(enigmaObj, "question");
            String answer = extractJsonValue(enigmaObj, "answer");

            if (!question.isEmpty() && !answer.isEmpty()) {
                list.addToRear(new Enigma(question, answer));
            }

            pos = closeBrace + 1;
        }

        return list;
    }

    private static String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null)
                content.append(line.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    // Extrator simples de valores JSON (usado para parsing manual)
    private static String extractJsonValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":");
        if (keyIndex == -1)
            return "";

        int valueStart = json.indexOf(":", keyIndex) + 1;
        int valueEnd = json.indexOf(",", valueStart);
        if (valueEnd == -1)
            valueEnd = json.indexOf("}", valueStart);
        if (valueEnd == -1)
            valueEnd = json.length();

        String value = json.substring(valueStart, valueEnd).trim();
        // Remove aspas se presentes
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static String extractBlock(String content, String key) {
        int keyIndex = content.indexOf(key);
        if (keyIndex == -1)
            return null;
        int startBracket = content.indexOf("[", keyIndex);
        int endBracket = content.indexOf("]", startBracket);
        if (startBracket != -1 && endBracket != -1)
            return content.substring(startBracket, endBracket + 1);
        return null;
    }

    private static Room findRoom(MazeMap map, String id) {
        return map.getRoom(id);
    }
}