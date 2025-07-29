package server.model.players.skills;

import server.Config;
import server.model.players.Client;
import server.util.Misc;

/**
 * Handles Runecrafting logic.
 * Original author: Sanity
 * Modernized for clarity and maintainability.
 */
public class Runecrafting {

    private final Client c;

    public Runecrafting(Client c) {
        this.c = c;
    }

    private static final int RUNE_ESS = 1436;
    private static final int PURE_ESS = 7936;

    public final int[] runes = {
        556, 558, 555, 557, 554, 559, 564, 562, 561, 563, 560, 565
    };

    public final int[] altarID = {
        2478, 2479, 2480, 2481, 2482, 2483, 2484, 2487, 2486, 2485, 2488, 2489
    };

    public final int[][] craftLevelReq = {
        {556, 1}, {558, 2}, {555, 5}, {557, 9}, {554, 14}, {559, 20},
        {564, 27}, {562, 35}, {561, 44}, {563, 54}, {560, 65}, {565, 77}
    };

    public final int[][] multipleRunes = {
        {11, 22, 33, 44, 55, 66, 77, 88, 99},
        {14, 28, 42, 56, 70, 84, 98},
        {19, 38, 57, 76, 95},
        {26, 52, 78},
        {35, 70},
        {46, 92},
        {59},
        {74},
        {91},
        {100},
        {100},
        {100}
    };

    public final int[] runecraftExp = {
        5, 6, 6, 7, 7, 8, 9, 9, 10, 11, 11, 11
    };

    /**
     * Checks a single or all inventory slots for a specific item ID.
     */
    private boolean itemInInv(int itemId, int slot, boolean checkAllSlots) {
        if (checkAllSlots) {
            for (int i = 0; i < 28; i++) {
                if (c.playerItems[i] == itemId + 1) {
                    return true;
                }
            }
        } else {
            return c.playerItems[slot] == itemId + 1;
        }
        return false;
    }

    /**
     * Replaces all found essence in inventory with runes.
     */
    private void replaceEssence(int essenceId, int runeId, int multiplier, int index) {
        int totalExp = 0;
        for (int i = 0; i < 28; i++) {
            if (itemInInv(essenceId, i, false)) {
                c.getItems().deleteItem(essenceId, i, 1);
                c.getItems().addItem(runeId, multiplier);
                totalExp += runecraftExp[index];
            }
        }
        c.getPA().addSkillXP(totalExp * Config.RUNECRAFTING_EXPERIENCE, c.playerRunecrafting);
    }

    /**
     * Crafts runes based on the altar clicked.
     */
    public void craftRunes(int clickedAltarId) {
        int runeId = -1;
        int index = -1;

        // Determine runeId and index
        for (int i = 0; i < altarID.length; i++) {
            if (altarID[i] == clickedAltarId) {
                runeId = runes[i];
                index = i;
                break;
            }
        }

        if (runeId == -1 || index == -1) {
            c.sendMessage("This altar is not recognized.");
            return;
        }

        // Check level requirement
        int requiredLevel = craftLevelReq[index][1];
        if (c.playerLevel[20] < requiredLevel) {
            c.sendMessage("You need a Runecrafting level of " + requiredLevel + " to craft this rune.");
            return;
        }

        // Check for essence
        if (!c.getItems().playerHasItem(RUNE_ESS) && !c.getItems().playerHasItem(PURE_ESS)) {
            c.sendMessage("You need to have essence to craft runes!");
            return;
        }

        int multiplier = 1;
        for (int lvlReq : multipleRunes[index]) {
            if (c.playerLevel[20] >= lvlReq) {
                multiplier++;
            }
        }

        replaceEssence(RUNE_ESS, runeId, multiplier, index);
        c.startAnimation(791);
        // c.frame174(481, 0, 0); // Optional: sound effect
        c.gfx100(186);
    }
}
