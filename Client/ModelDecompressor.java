import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Loads models from a data file
 *
 * @author Ben
 */
public class ModelDecompressor {

    public static void loadModels() {
        try (
            DataInputStream indexFile = new DataInputStream(new FileInputStream("C:/SoulSplit1/models.idx"));
            DataInputStream dataFile = new DataInputStream(new FileInputStream("C:/SoulSplit1/models.dat"))
        ) {
            int length = indexFile.readInt();
            for (int i = 0; i < length; i++) {
                int id = indexFile.readInt();
                int invlength = indexFile.readInt();
                byte[] data = new byte[invlength];
                dataFile.readFully(data);
                // System.out.println("ID: "+ id +" Length: "+ invlength +" Data: "+ data);
                Model.method460(data, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
