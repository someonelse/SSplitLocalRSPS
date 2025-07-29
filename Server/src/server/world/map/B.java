package server.world.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class B {

    // Holds integer data loaded from file.
    public static final int[] j = new int[100];

    // Loads data from "./Data/Data.txt" into the j[] array.
    public static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("./Data/Data.txt"))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < j.length) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        j[i++] = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid integer on line " + (i + 1) + ": " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load data from ./Data/Data.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
