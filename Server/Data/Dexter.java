import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class Dexter {

    public static void main(String[] args) {
        Dexter dexter = new Dexter();
        // dexter.checkForFlag();
        dexter.checkBanks();
    }

    public void checkBanks() {
        try {
            File dir = new File("characters");
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File loaded : files) {
                        if (loaded.getName().endsWith(".txt")) {
                            List<String> lines = Files.readAllLines(loaded.toPath());
                            int cash = 0;
                            for (String line : lines) {
                                if (line.startsWith("character-item") || line.startsWith("character-bank")) {
                                    String[] temp = line.split("\t");
                                    if (temp.length >= 3) {
                                        int itemId = Integer.parseInt(temp[1]);
                                        int amount = Integer.parseInt(temp[2]);
                                        if (itemId == 996) {
                                            cash += amount;
                                            if (cash > 12_500_000) {
                                                System.out.println("name: " + loaded.getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkForFlag() {
        try {
            File dir = new File("characters");
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File loaded : files) {
                        if (loaded.getName().endsWith(".txt")) {
                            List<String> lines = Files.readAllLines(loaded.toPath());
                            for (String line : lines) {
                                if (line.equalsIgnoreCase("flagged = true")) {
                                    System.out.println(loaded.getName());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logFile(String name) {
        try (FileWriter fw = new FileWriter("dupers.txt", true)) { // append mode
            fw.write(name + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
