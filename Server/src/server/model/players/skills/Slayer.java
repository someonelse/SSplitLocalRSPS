
package server.model.players.skills;

import server.model.players.Client;
import server.Server;
import server.util.Misc;

public class Slayer {

    private final Client c;

    private static final int[] LOW_TASKS = {1648, 1612, 117, 1265, 90, 103, 78, 119, 18, 101, 181};
    private static final int[] LOW_REQS = {5, 15, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    private static final int[] MED_TASKS = {1643, 1618, 941, 119, 82, 52, 1612, 117, 1265, 112, 125};
    private static final int[] MED_REQS = {45, 50, 1, 1, 1, 1, 15, 1, 1, 1, 1};

    private static final int[] HIGH_TASKS = {1624, 1610, 1613, 1615, 55, 84, 49, 1618, 941, 82, 2783, 1341};
    private static final int[] HIGH_REQS = {65, 75, 80, 85, 1, 1, 1, 50, 1, 1, 90, 1};

    public Slayer(Client c) {
        this.c = c;
    }

    public void giveTask() {
        if (c.combatLevel < 50) {
            assignTask(LOW_TASKS, LOW_REQS);
        } else if (c.combatLevel <= 90) {
            assignTask(MED_TASKS, MED_REQS);
        } else {
            assignTask(HIGH_TASKS, HIGH_REQS);
        }
    }

    public void giveTask2() {
        for (int task : LOW_TASKS) {
            if (task == c.slayerTask) {
                c.sendMessage("You already have an easy task... to kill " + c.taskAmount + " " +
                        Server.npcHandler.getNpcListName(c.slayerTask) + ".");
                return;
            }
        }
        assignTask(LOW_TASKS, LOW_REQS);
    }

    private void assignTask(int[] tasks, int[] reqs) {
        int index = Misc.random(tasks.length - 1);
        if (c.playerLevel[c.playerSlayer] < reqs[index]) {
            assignTask(tasks, reqs); // try again
            return;
        }
        c.slayerTask = tasks[index];
        c.taskAmount = Misc.random(15) + 15;
        c.sendMessage("You have been assigned to kill " + c.taskAmount + " " +
                Server.npcHandler.getNpcListName(c.slayerTask) + " as a slayer task.");
    }
}
