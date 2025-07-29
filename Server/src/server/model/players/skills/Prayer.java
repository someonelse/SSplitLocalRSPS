package server.model.players.skills;

import server.Config;
import server.model.players.Client;

/**
 * Handles Prayer skill interactions.
 * Original author: unknown
 * Modernized for clarity, performance, and future maintenance.
 */
public class Prayer {

    private final Client c;

    // Bone ID and base XP value (burying)
    private static final int[][] BONES_EXP = {
        {526, 5},     // Normal bones
        {532, 15},    // Big bones
        {534, 80},    // Baby dragon bones
        {536, 190},   // Dragon bones
        {6729, 230}   // Dagannoth bones
    };

    public Prayer(Client c) {
        this.c = c;
    }

    /**
     * Bury a bone from inventory.
     */
    public void buryBone(int boneId, int slot) {
        if (System.currentTimeMillis() - c.buryDelay > 1500) {
            c.getItems().deleteItem(boneId, slot, 1);
            c.sendMessage("You bury the bones.");
            int xp = getBoneExp(boneId) * Config.PRAYER_EXPERIENCE;
            c.getPA().addSkillXP(xp, 5);
            c.buryDelay = System.currentTimeMillis();
            c.startAnimation(827);
        }
    }

    /**
     * Offer bone on altar.
     */
    public void bonesOnAltar(int boneId) {
        c.getItems().deleteItem(boneId, c.getItems().getItemSlot(boneId), 1);
        c.sendMessage("The gods are pleased with your offering.");
        int xp = getBoneExp(boneId) * 4 * Config.PRAYER_EXPERIENCE;
        c.getPA().addSkillXP(xp, 5);
    }

    /**
     * Check if an item is a bone.
     */
    public boolean isBone(int itemId) {
        for (int[] bone : BONES_EXP) {
            if (bone[0] == itemId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get base experience for a bone.
     */
    private int getBoneExp(int itemId) {
        for (int[] bone : BONES_EXP) {
            if (bone[0] == itemId) {
                return bone[1];
            }
        }
        return 0;
    }
}
