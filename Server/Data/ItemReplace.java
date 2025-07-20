import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class ItemReplace {

    public int toReplace = 9010;
    public int altRemove = 9011;
    public int replaceWith = 995;
    public int altReplace = 996;
    public int replaceAmount = 700;

    public static void main(String[] args) {
        ItemReplace ir = new ItemReplace();
        File dir = new File("characters");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File loaded : files) {
                    if (loaded.getName().endsWith(".txt")) {
                        ir.handleCharacter(loaded);
                    }
                }
            }
        }
    }

    public void handleCharacter(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String temp = lines.get(i).trim();
                if (!temp.isEmpty()) {
                    String[] items = temp.split("\t");
                    if (items.length > 2) {
                        if (temp.contains("item") || temp.contains("bank")) {
                            int itemId = Integer.parseInt(items[1]);
                            if (itemId == altRemove) {
                                items[1] = String.valueOf(altReplace);
                                items[2] = String.valueOf(Integer.parseInt(items[2]) * replaceAmount);
                            }
                            temp = String.join("\t", items);
                        } else if (temp.contains("character-equip = 13")) {
                            int itemId = Integer.parseInt(items[1]);
                            if (itemId == toReplace) {
                                items[1] = String.valueOf(replaceWith);
                                items[2] = String.valueOf(Integer.parseInt(items[2]) * replaceAmount);
                            }
                            temp = String.join("\t", items);
                        }
                    }
                }
                lines.set(i, temp);
            }
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            System.err.println("Error processing file: " + file.getName());
            e.printStackTrace();
        }
    }

    public int getLineCount(Scanner scanner) {
        int count = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            count++;
        }
        return count;
    }
}
