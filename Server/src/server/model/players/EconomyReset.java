import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EconomyReset {

    private static final File charDir = new File("./characters/");

    public static void main(String[] args) {
        if (charDir.exists() && charDir.isDirectory()) {
            File[] charFiles = charDir.listFiles();
            if (charFiles != null) {
                for (File charFile : charFiles) {
                    resetEcoChar(charFile);
                    System.out.println("Reset player economy levels for... " + charFile);
                }
            }
        }
    }

    private static void resetEcoChar(File charFile) {
        File tempCharFile = new File(charDir, "ECOBOOST$TEMP");
        try (
            DataInputStream fileStream = new DataInputStream(new FileInputStream(charFile));
            BufferedWriter tempOut = new BufferedWriter(new FileWriter(tempCharFile))
        ) {
            String tempData;
            int curEquip = 0;

            while ((tempData = fileStream.readLine()) != null) {
                String trimmed = tempData.trim();
                if (!trimmed.startsWith("character-item =") && !trimmed.startsWith("character-bank =")) {
                    String tempAdd = trimmed;
                    if (trimmed.startsWith("character-equip =")) {
                        tempAdd = "character-equip = " + curEquip + "\t-1\t0";
                        curEquip++;
                    }
                    tempOut.write(tempAdd);
                    tempOut.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Replace old file with new file
        if (!charFile.delete()) {
            System.err.println("Failed to delete original file: " + charFile);
            return;
        }
        if (!tempCharFile.renameTo(charFile)) {
            System.err.println("Failed to rename temp file to original file: " + tempCharFile);
        }
    }
}
