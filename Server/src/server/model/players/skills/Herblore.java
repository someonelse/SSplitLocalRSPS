package server.model.players.skills;

import server.Config;
import server.model.players.Client;

/**
 * Handles Herblore skill actions including identifying herbs and making potions.
 * 
 * @author Sanity
 */
public class Herblore {

    private final Client c;

    // {unidentifiedHerbId, identifiedHerbId, levelReq, exp}
    private static final int[][] HERB_IDENT_DATA = {
        {199, 249, 1, 3}, {201, 251, 5, 4}, {203, 253, 11, 5}, {205, 255, 20, 6},
        {207, 257, 25, 8}, {3049, 2998, 30, 8}, {209, 259, 40, 9}, {211, 261, 48, 10},
        {213, 263, 54, 11}, {3051, 3000, 59, 12}, {215, 265, 65, 13}, {2485, 2481, 67, 13},
        {217, 267, 70, 14}, {219, 269, 75, 15}
    };

    // {identifiedHerbId, potion3dose, potion4dose, levelReq, exp}
    private static final int[][] POTION_DATA = {
        {249, 121, 2428, 1, 25}, {251, 175, 2446, 5, 38}, {253, 115, 113, 12, 50},
        {255, 127, 2430, 22, 63}, {257, 139, 2434, 38, 88}, {261, 2448, 2448, 40, 100},
        {259, 145, 2436, 45, 100}, {263, 157, 2440, 55, 125}, {3000, 3026, 3024, 63, 143},
        {265, 163, 2442, 66, 155}, {2481, 2454, 2452, 69, 158}, {267, 169, 2444, 72, 163},
        {269, 189, 2450, 78, 175}, {2998, 6687, 6685, 81, 180}
    };

    public Herblore(Client c) {
        this.c = c;
    }

    public void handleHerbClick(int herbId) {
        for (int[] herb : HERB_IDENT_DATA) {
            if (herb[0] == herbId) {
                identifyHerb(herb);
                return;
            }
        }
    }

    public void handlePotMaking(int item1, int item2) {
        if (item1 == 227 && isIdentifiedHerb(item2)) {
            makePotion(item2);
        } else if (item2 == 227 && isIdentifiedHerb(item1)) {
            makePotion(item1);
        }
    }

    public boolean isUnidentifiedHerb(int herbId) {
        for (int[] herb : HERB_IDENT_DATA) {
            if (herb[0] == herbId) return true;
        }
        return false;
    }

    public boolean isIdentifiedHerb(int herbId) {
        for (int[] data : POTION_DATA) {
            if (data[0] == herbId) return true;
        }
        return false;
    }

    private void identifyHerb(int[] herbData) {
        int unid = herbData[0], id = herbData[1], levelReq = herbData[2], exp = herbData[3];

        if (c.playerLevel[c.playerHerblore] < levelReq) {
            c.sendMessage("You need a herblore level of " + levelReq + " to identify this herb.");
            return;
        }

        if (c.getItems().playerHasItem(unid)) {
            c.getItems().deleteItem(unid, c.getItems().getItemSlot(unid), 1);
            c.getItems().addItem(id, 1);
            c.getPA().addSkillXP(exp * Config.HERBLORE_EXPERIENCE, c.playerHerblore);
            c.sendMessage("You identify the herb as a " + c.getItems().getItemName(id) + ".");
        }
    }

    private void makePotion(int herbId) {
        for (int[] potion : POTION_DATA) {
            if (potion[0] != herbId) continue;

            int levelReq = potion[3], exp = potion[4], finalPotion = potion[2];

            if (c.playerLevel[c.playerHerblore] < levelReq) {
                c.sendMessage("You need a herblore level of " + levelReq + " to make this potion.");
                return;
            }

            if (c.getItems().playerHasItem(227) && c.getItems().playerHasItem(herbId)) {
                c.getItems().deleteItem(herbId, c.getItems().getItemSlot(herbId), 1);
                c.getItems().deleteItem(227, c.getItems().getItemSlot(227), 1);
                c.getItems().addItem(finalPotion, 1);
                c.getPA().addSkillXP(exp * Config.HERBLORE_EXPERIENCE, c.playerHerblore);
                c.sendMessage("You make a " + c.getItems().getItemName(finalPotion) + ".");
            }
            return;
        }
    }
}
