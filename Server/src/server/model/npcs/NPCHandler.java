package server.model.npcs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class NPCHandler {

    public static final int maxNPCs = 10000;
    public static final int maxListedNPCs = 10000;
    public NPC[] npcs = new NPC[maxNPCs];
    public NPCList[] npcList = new NPCList[maxListedNPCs];

    public NPCHandler() {
        for (int i = 0; i < maxNPCs; i++) {
            npcs[i] = null;
        }
        for (int i = 0; i < maxListedNPCs; i++) {
            npcList[i] = null;
        }
        loadNPCList("./Data/cfg/npc.cfg");
        loadNPCs("./Data/cfg/spawn-config.cfg");
    }

    // reads npc.cfg
    public void loadNPCList(final String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("npc")) {
                    line = line.trim().replaceAll("\t+", " ");
                    final String[] token = line.split(" +");
                    if (token.length < 5) {
                        continue;
                    }
                    final int id = Integer.parseInt(token[1]);
                    final String name = token[2].replaceAll("_", " ");
                    final int combat = Integer.parseInt(token[3]);
                    final int health = Integer.parseInt(token[4]);
                    npcList[id] = new NPCList(id);
                    npcList[id].npcName = name;
                    npcList[id].npcCombat = combat;
                    npcList[id].npcHealth = health;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading npc list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // reads spawn-config.cfg
    public void loadNPCs(final String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int npcCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("spawn")) {
                    line = line.trim().replaceAll("\t+", " ");
                    final String[] token = line.split(" +");
                    if (token.length < 7) {
                        continue;
                    }
                    final int id = Integer.parseInt(token[1]);
                    final int x = Integer.parseInt(token[2]);
                    final int y = Integer.parseInt(token[3]);
                    final int height = Integer.parseInt(token[4]);
                    final int walk = Integer.parseInt(token[5]);
                    final int face = Integer.parseInt(token[6]);

                    final int slot = getFreeSlot();
                    if (slot == -1) {
                        System.out.println("Max NPC limit reached.");
                        break;
                    }

                    npcs[slot] = new NPC(slot, id);
                    npcs[slot].absX = x;
                    npcs[slot].absY = y;
                    npcs[slot].makeX = x;
                    npcs[slot].makeY = y;
                    npcs[slot].heightLevel = height;
                    npcs[slot].walkingType = walk;
                    npcs[slot].face = face;
                    npcCount++;
                }
            }
            System.out.println("Loaded " + npcCount + " NPC spawns.");
        } catch (Exception e) {
            System.out.println("Error loading NPCs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getFreeSlot() {
        for (int i = 0; i < maxNPCs; i++) {
            if (npcs[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
