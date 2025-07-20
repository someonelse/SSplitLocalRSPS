package server.model.players.skills;

import server.model.players.Client;
import server.Config;

public class Fletching {

    private final Client c;

    public Fletching(Client c) {
        this.c = c;
    }

    private final int[][] arrows = {
        {52, 314, 53, 15, 1}, {53, 39, 882, 40, 1}, {53, 40, 884, 58, 15},
        {53, 41, 886, 95, 30}, {53, 42, 888, 132, 45}, {53, 43, 890, 170, 60}, {53, 44, 892, 207, 75}
    };

    private final int[][] buttons = {
        {34185, 1, 1}, {34184, 1, 5}, {34183, 1, 10}, {34182, 1, 27},
        {34189, 2, 1}, {34188, 2, 5}, {34187, 2, 10}, {34186, 2, 27},
        {34193, 3, 1}, {34193, 3, 5}, {34193, 3, 10}, {34193, 3, 27}
    };

    private final int shaft = 52;
    private final int[] logType = {1511, 1521, 1519, 1517, 1515, 1513};
    private final int[] shortbows = {841, 843, 849, 853, 857, 861};
    private final int[] longbows = {839, 845, 847, 851, 855, 859};
    private final int[] exps = {5, 16, 33, 50, 67, 83};
    private final int[] reqs = {5, 20, 35, 50, 65, 80};

    public boolean fletching = false;
    private int fletchType = 0, amount = 0, log = 0;

    public void makeArrows(int item1, int item2) {
        for (int[] arrow : arrows) {
            if ((item1 == arrow[0] && item2 == arrow[1]) || (item2 == arrow[0] && item1 == arrow[1])) {
                if (c.getItems().playerHasItem(item1, 15) && c.getItems().playerHasItem(item2, 15)) {
                    if (c.playerLevel[c.playerFletching] >= arrow[4]) {
                        c.getItems().deleteItem(item1, c.getItems().getItemSlot(item1), 15);
                        c.getItems().deleteItem(item2, c.getItems().getItemSlot(item2), 15);
                        c.getItems().addItem(arrow[2], 15);
                        c.getPA().addSkillXP(arrow[3] * Config.FLETCHING_EXPERIENCE, c.playerFletching);
                    } else {
                        c.sendMessage("You need a fletching level of " + arrow[4] + " to fletch this.");
                    }
                } else {
                    c.sendMessage("You must have 15 of each supply to do this.");
                }
                break;
            }
        }
    }

    public void handleFletchingClick(int clickId) {
        for (int[] btn : buttons) {
            if (btn[0] == clickId) {
                fletchType = btn[1];
                amount = btn[2];
                for (int i = 0; i < logType.length; i++) {
                    if (log == logType[i]) {
                        fletchBow(i);
                        return;
                    }
                }
            }
        }
    }

    private void fletchBow(int index) {
        int toAdd = getItemToAdd(index);
        int amountToAdd = getAmountToAdd(toAdd);

        for (int j = 0; j < amount; j++) {
            if (!c.getItems().playerHasItem(logType[index])) break;

            if (c.playerLevel[c.playerFletching] >= reqs[index] || fletchType == 3) {
                c.getItems().deleteItem(logType[index], c.getItems().getItemSlot(logType[index]), 1);
                c.getItems().addItem(toAdd, amountToAdd);
                c.getPA().addSkillXP(getExp(index) * Config.FLETCHING_EXPERIENCE, c.playerFletching);
            } else {
                c.sendMessage("You need a fletching level of " + reqs[index] + " to fletch this item.");
                break;
            }
        }
    }

    private int getExp(int index) {
        return fletchType == 3 ? 5 : fletchType == 1 ? exps[index] : exps[index] + 8;
    }

    private int getItemToAdd(int index) {
        return switch (fletchType) {
            case 3 -> shaft;
            case 1 -> shortbows[index];
            case 2 -> longbows[index];
            default -> 0;
        };
    }

    private int getAmountToAdd(int id) {
        return id == 52 ? 15 : 1;
    }

    public void handleLog(int item1, int item2) {
        openFletching(item1 == 946 ? item2 : item1);
    }

    public void openFletching(int item) {
        int frame = 8880;
        int title = 8879;
        int leftPic = 8883;
        int midPic = 8884;
        int rightPic = 8885;
        int leftText = 8889;
        int midText = 8893;
        int rightText = 8897;

        switch (item) {
            case 1511 -> {
                c.getPA().sendFrame246(midPic, 250, 839);
                c.getPA().sendFrame246(leftPic, 250, 841);
                c.getPA().sendFrame246(rightPic, 250, 52);
                c.getPA().sendFrame126("Shortbow", leftText);
                c.getPA().sendFrame126("Longbow", midText);
                c.getPA().sendFrame126("Arrow Shafts", rightText);
            }
            case 1521 -> {
                c.getPA().sendFrame246(midPic, 250, 845);
                c.getPA().sendFrame246(leftPic, 250, 843);
                c.getPA().sendFrame246(rightPic, 250, 52);
                c.getPA().sendFrame126("Oak Shortbow", leftText);
                c.getPA().sendFrame126("Oak Longbow", midText);
                c.getPA().sendFrame126("Arrow Shafts", rightText);
            }
            case 1519 -> {
                c.getPA().sendFrame246(midPic, 250, 847);
                c.getPA().sendFrame246(leftPic, 250, 849);
                c.getPA().sendFrame246(rightPic, 250, 52);
                c.getPA().sendFrame126("Willow Shortbow", leftText);
                c.getPA().sendFrame126("Willow Longbow", midText);
                c.getPA().sendFrame126("Arrow Shafts", rightText);
            }
            case 1517 -> {
                c.getPA().sendFrame246(midPic, 250, 851);
                c.getPA().sendFrame246(leftPic, 250, 853);
                c.getPA().sendFrame246(rightPic, 250, 52);
                c.getPA().sendFrame126("Maple Shortbow", leftText);
                c.getPA().sendFrame126("Maple Longbow", midText);
                c.getPA().sendFrame126("Arrow Shafts", rightText);
            }
            case 1515 -> {
                c.getPA().sendFrame246(midPic, 250, 855);
                c.getPA().sendFrame246(leftPic, 250, 857);
                c.getPA().sendFrame246(rightPic, 250, 52);
                c.getPA().sendFrame126("Yew Shortbow", leftText);
                c.getPA().sendFrame126("Yew Longbow", midText);
                c.getPA().sendFrame126("Arrow Shafts", rightText);
            }
            case 1513 -> {
                c.getPA().sendFrame246(midPic, 250, 859);
                c.getPA().sendFrame246(leftPic, 250, 861);
                c.getPA().sendFrame246(rightPic, 250, 52);
                c.getPA().sendFrame126("Magic Shortbow", leftText);
                c.getPA().sendFrame126("Magic Longbow", midText);
                c.getPA().sendFrame126("Arrow Shafts", rightText);
            }
            default -> {
                return;
            }
        }

        c.getPA().sendFrame164(frame);
        c.getPA().sendFrame126("What would you like to make?", title);
        log = item;
        fletching = true;
    }
}
