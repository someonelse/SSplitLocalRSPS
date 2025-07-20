package server.model.npcs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Handles loading NPC drops from configuration files.
 * 
 * @author Sanity
 */
public class NPCDrops {

    public static final Map<Integer, int[][]> normalDrops = new HashMap<>();
    public static final Map<Integer, int[][]> rareDrops = new HashMap<>();
    public static final Map<Integer, int[]> constantDrops = new HashMap<>();
    public static final Map<Integer, Integer> dropRarity = new HashMap<>();

    public NPCDrops() {
        loadDrops();
    }

    public void loadDrops() {
        try (Scanner s = new Scanner(new File("./Data/cfg/NPCDrops.TSM"))) {
            int[][][] npcDrops = new int[3800][][];
            int[][][] rareDrops2 = new int[3800][][];
            int[] itemRarity = new int[3800];

            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("#") || !s.hasNextLine()) continue;

                StringTokenizer normalTok = new StringTokenizer(line, "\t");
                line = s.nextLine();
                if (line.startsWith("#")) continue;

                StringTokenizer rareTok = new StringTokenizer(line, "\t");
                String[] info = normalTok.nextToken().split(":");
                int npcId = Integer.parseInt(info[0]);
                itemRarity[npcId] = Integer.parseInt(info[1]) - 1;

                npcDrops[npcId] = new int[normalTok.countTokens()][2];
                rareDrops2[npcId] = new int[rareTok.countTokens()][2];

                int count = 0;
                while (normalTok.hasMoreTokens()) {
                    String[] temp = normalTok.nextToken().split(":");
                    npcDrops[npcId][count][0] = Integer.parseInt(temp[0]);
                    npcDrops[npcId][count][1] = Integer.parseInt(temp[1]);
                    count++;
                }

                count = 0;
                while (rareTok.hasMoreTokens()) {
                    String[] temp = rareTok.nextToken().split(":");
                    rareDrops2[npcId][count][0] = Integer.parseInt(temp[0]);
                    rareDrops2[npcId][count][1] = Integer.parseInt(temp[1]);
                    System.out.printf("RareDrop [%d] ID: %d Qty: %d%n", count, rareDrops2[npcId][count][0], rareDrops2[npcId][count][1]);
                    count++;
                }

                normalDrops.put(npcId, npcDrops[npcId]);
                rareDrops.put(npcId, rareDrops2[npcId]);
                dropRarity.put(npcId, itemRarity[npcId]);
            }

            loadConstants();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void loadConstants() {
        try (Scanner s = new Scanner(new File("./Data/cfg/NpcConstants.TSM"))) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("#")) continue;

                StringTokenizer constantTok = new StringTokenizer(line, "\t");
                int npcId = Integer.parseInt(constantTok.nextToken());
                int[] temp = new int[constantTok.countTokens()];

                for (int i = 0; constantTok.hasMoreTokens(); i++) {
                    temp[i] = Integer.parseInt(constantTok.nextToken());
                }

                constantDrops.put(npcId, temp);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
