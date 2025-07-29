import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EconomyReset {

    private static final File charDir = new File("Data/characters/");

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
        try {
            String tempData, tempAdd = "";
            int curEquip = 0;

            File tempCharFile = new File(charDir + "ECOBOOST$TEMP");
            DataInputStream fileStream = new DataInputStream(new FileInputStream(charFile));
            BufferedWriter tempOut = new BufferedWriter(new FileWriter(tempCharFile));

            while ((tempData = fileStream.readLine()) != null) {
                tempData = tempData.trim();
                if (!tempData.startsWith("character-item =") && !tempData.startsWith("character-bank =")) {
                    tempAdd = tempData;

                    if (tempData.startsWith("character-equip =")) {
                        tempAdd = "character-equip = " + curEquip + "\t-1\t0";
                        curEquip++;
                    }
                    tempOut.write(tempAdd);
                    tempOut.newLine();
                }
            }
            fileStream.close();
            tempOut.close();

            charFile.delete();
            tempCharFile.renameTo(charFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
