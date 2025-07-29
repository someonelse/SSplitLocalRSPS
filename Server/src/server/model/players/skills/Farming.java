
package server.model.players.skills;

import server.Config;
import server.model.players.Client;
import server.model.objects.Objects;
import server.Server;

/**
 * Firemaking.java
 *
 * Updated and modernized version.
 **/

public class Firemaking {

    private final Client c;
    private static final int[] LOGS = {1511, 1521, 1519, 1517, 1515, 1513};
    private static final int[] EXP = {40, 60, 90, 135, 203, 304};
    private static final int[] LEVELS = {1, 15, 30, 45, 60, 75};
    private static final int DELAY = 1250;
    private long lastLight = 0;
    public boolean resetAnim = false;

    public Firemaking(Client c) {
        this.c = c;
    }

    public void checkLogType(int logType, int otherItem) {
        for (int i = 0; i < LOGS.length; i++) {
            if (LOGS[i] == logType || LOGS[i] == otherItem) {
                lightFire(i);
                return;
            }
        }
    }

    public void lightFire(int slot) {
        if (c.duelStatus >= 5) {
            c.sendMessage("Why am I trying to light a fire in the duel arena?");
            return;
        }

        if (c.playerLevel[c.playerFiremaking] < LEVELS[slot]) {
            c.sendMessage("You need a firemaking level of " + LEVELS[slot] + " to burn this log.");
            return;
        }

        if (!c.getItems().playerHasItem(590) || !c.getItems().playerHasItem(LOGS[slot])) {
            c.sendMessage("You need both a tinderbox and logs to light a fire.");
            return;
        }

        if (System.currentTimeMillis() - lastLight < DELAY) {
            return;
        }

        c.startAnimation(733, 0);
        c.getItems().deleteItem(LOGS[slot], c.getItems().getItemSlot(LOGS[slot]), 1);
        c.getPA().addSkillXP(EXP[slot] * Config.FIREMAKING_EXPERIENCE, c.playerFiremaking);

        Objects fire = new Objects(2732, c.getX(), c.getY(), 0, -1, 10, 3);
        Objects fireReplace = new Objects(-1, c.getX(), c.getY(), 0, -1, 10, 60);
        Server.objectHandler.addObject(fire);
        Server.objectHandler.addObject(fireReplace);

        c.sendMessage("You light the fire.");
        c.getPA().walkTo(-1, 0);
        c.turnPlayerTo(c.getX() + 1, c.getY());
        lastLight = System.currentTimeMillis();
        resetAnim = true;
    }
}
