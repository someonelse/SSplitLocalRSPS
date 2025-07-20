
package server.model.players.skills;

import server.Config;
import server.util.Misc;
import server.model.players.Client;

public class Fishing {

    private final Client c;

    private static final int SALMON_ID = 331;
    private static final int SWORD_ID = 371;
    private static final int SALMON_EXP = 70;
    private static final int SWORD_EXP = 100;

    private static final int[] REQS = {1, 20, 40, 35, 62, 76, 81};
    private static final int[] FISH_TYPES = {317, 335, 359, 359, 7944, 383, 389};
    private static final int[] EXP = {10, 50, 80, 90, 120, 110, 46};

    private int fishType = -1;
    private int exp = 0;
    private int req = 0;
    private int equipmentType = -1;

    public boolean fishing = false;

    public Fishing(Client c) {
        this.c = c;
    }

    public void setupFishing(int requestedFishType) {
        int slot = getSlot(requestedFishType);
        if (slot == -1) {
            c.sendMessage("Invalid fishing spot.");
            return;
        }

        int equipment = getEquipment(requestedFishType);
        if (!c.getItems().playerHasItem(equipment)) {
            c.sendMessage("You do not have the correct equipment to use this fishing spot.");
            resetFishing();
            return;
        }

        if (c.playerLevel[c.playerFishing] < REQS[slot]) {
            c.sendMessage("You need a fishing level of " + REQS[slot] + " to fish here.");
            resetFishing();
            return;
        }

        this.req = REQS[slot];
        this.fishType = FISH_TYPES[slot];
        this.equipmentType = equipment;
        this.exp = EXP[slot];
        c.fishing = true;
        c.fishTimer = 3 + Misc.random(2);
    }

    public void catchFish() {
        if (!c.getItems().playerHasItem(equipmentType)) {
            c.sendMessage("You do not have the correct equipment to use this fishing spot.");
            resetFishing();
            return;
        }

        if (c.playerLevel[c.playerFishing] < req) {
            c.sendMessage("You need a fishing level of " + req + " to fish here.");
            resetFishing();
            return;
        }

        if (c.getItems().freeSlots() <= 0) return;

        if (canFishOther(fishType)) {
            c.getItems().addItem(getOtherFishId(fishType), 1);
            c.getPA().addSkillXP(getOtherFishXP(fishType), c.playerFishing);
        } else {
            c.getItems().addItem(fishType, 1);
            c.getPA().addSkillXP(exp * Config.FISHING_EXPERIENCE, c.playerFishing);
        }
        c.sendMessage("You catch a fish.");
        c.fishTimer = 2 + Misc.random(2);
    }

    private int getSlot(int fishType) {
        for (int i = 0; i < FISH_TYPES.length; i++) {
            if (FISH_TYPES[i] == fishType) return i;
        }
        return -1;
    }

    private int getEquipment(int fish) {
        switch (fish) {
            case 317: return 303;
            case 335: return 309;
            case 337: return 301;
            case 361: return 311;
            case 7944: return 303;
            case 383: return 311;
            case 389: return 303;
            default: return -1;
        }
    }

    private boolean canFishOther(int fishType) {
        return (fishType == 335 && c.playerLevel[c.playerFishing] >= 30) ||
               (fishType == 361 && c.playerLevel[c.playerFishing] >= 50);
    }

    private int getOtherFishId(int fishType) {
        return fishType == 335 ? SALMON_ID : fishType == 361 ? SWORD_ID : -1;
    }

    private int getOtherFishXP(int fishType) {
        return fishType == 335 ? SALMON_EXP : fishType == 361 ? SWORD_EXP : 0;
    }

    public void resetFishing() {
        this.exp = 0;
        this.fishType = -1;
        this.equipmentType = -1;
        this.req = 0;
        c.fishTimer = -1;
        c.fishing = false;
    }
}
