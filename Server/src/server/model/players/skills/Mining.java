package server.model.players.skills;

import server.model.players.Client;
import server.Config;
import server.util.Misc;

/**
 * Handles Mining skill actions such as starting mining and receiving ores.
 * 
 * @author Sanity
 */
public class Mining {

    private final Client c;

    private static final int[] VALID_PICK = {1265, 1267, 1269, 1273, 1271, 1275};
    private static final int[] PICK_REQS = {1, 1, 6, 6, 21, 31, 41, 61}; // Extra entries may be vestigial
    private static final int[] RANDOM_GEMS = {1623, 1621, 1619, 1617, 1631};

    private static final int EMOTE = 625;

    private int oreType = -1;
    private int exp = -1;
    private int levelReq = -1;
    private int pickType = -1;

    public Mining(Client c) {
        this.c = c;
    }

    /**
     * Starts the mining action.
     */
    public void startMining(int oreType, int levelReq, int exp) {
        c.turnPlayerTo(c.objectX, c.objectY);

        int pick = goodPick();
        if (pick > 0) {
            if (c.playerLevel[c.playerMining] >= levelReq) {
                this.oreType = oreType;
                this.exp = exp;
                this.levelReq = levelReq;
                this.pickType = pick;

                c.sendMessage("You swing your pick at the rock.");
                c.miningTimer = getMiningTimer(oreType);
                c.startAnimation(EMOTE);
            } else {
                resetMining();
                c.sendMessage("You need a mining level of " + levelReq + " to mine this rock.");
                c.startAnimation(65535);
            }
        } else {
            resetMining();
            c.sendMessage("You need a pickaxe to mine this rock.");
            c.startAnimation(65535);
            c.getPA().resetVariables();
        }
    }

    /**
     * Called when a mining action completes and gives ore to player.
     */
    public void mineOre() {
        if (c.getItems().addItem(oreType, 1)) {
            c.startAnimation(EMOTE);
            c.sendMessage("You manage to mine some ore.");
            c.getPA().addSkillXP(exp * Config.MINING_EXPERIENCE, c.playerMining);
            c.getPA().refreshSkill(c.playerMining);
            c.miningTimer = getMiningTimer(oreType);

            if (Misc.random(25) == 10) {
                int gemId = RANDOM_GEMS[Misc.random(RANDOM_GEMS.length - 1)];
                c.getItems().addItem(gemId, 1);
                c.sendMessage("You find a gem!");
            }
        } else {
            c.getPA().resetVariables();
            c.startAnimation(65535);
        }
    }

    /**
     * Resets mining state.
     */
    public void resetMining() {
        this.oreType = -1;
        this.exp = -1;
        this.levelReq = -1;
        this.pickType = -1;
    }

    /**
     * Finds a valid pickaxe either equipped or in inventory.
     */
    public int goodPick() {
        // Check equipped
        for (int j = VALID_PICK.length - 1; j >= 0; j--) {
            if (c.playerEquipment[c.playerWeapon] == VALID_PICK[j]) {
                if (c.playerLevel[c.playerMining] >= PICK_REQS[j]) {
                    return VALID_PICK[j];
                }
            }
        }

        // Check inventory
        for (int item : c.playerItems) {
            for (int j = VALID_PICK.length - 1; j >= 0; j--) {
                if (item == VALID_PICK[j] + 1 && c.playerLevel[c.playerMining] >= PICK_REQS[j]) {
                    return VALID_PICK[j];
                }
            }
        }

        return -1;
    }

    /**
     * Calculates how long mining the ore takes.
     */
    public int getMiningTimer(int ore) {
        int time = Misc.random(5);
        if (ore == 451) { // Rune ore takes longer
            time += 4;
        }
        return time;
    }
}
