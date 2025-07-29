package server.model.minigames;

import server.model.players.Client;
import server.Server;

/**
 * @author Sanity
 */

public class FightCaves {

    private static final int[][] WAVES = {
        {2743}, {2743, 2627}, {2743, 2627, 2627}, {2743, 2630}, {2743, 2630, 2627}, {2743, 2630, 2627, 2627},
        {2743, 2630, 2630}, {2743, 2631}, {2743, 2631, 2627}, {2743, 2631, 2627, 2627}, {2743, 2631, 2630}, {2743, 2631, 2630, 2627},
        {2743, 2631, 2630, 2627, 2627}, {2743, 2631, 2630, 2630}, {2743, 2631, 2631}, {2743, 2741}, {2743, 2741, 2627},
        {2743, 2741, 2627, 2627}, {2743, 2741, 2630}, {2743, 2741, 2630, 2627}, {2743, 2741, 2630, 2627, 2627},
        {2743, 2741, 2630, 2630}, {2743, 2741, 2631}, {2743, 2741, 2631, 2627}, {2743, 2741, 2631, 2627, 2627},
        {2743, 2741, 2631, 2630}, {2743, 2741, 2631, 2630, 2627}, {2743, 2741, 2631, 2630, 2627, 2627},
        {2743, 2741, 2631, 2630, 2630}, {2743, 2741, 2631, 2631}, {2743, 2741, 2741}, {2743, 2743}, {2745}
    };

    private static final int[][] COORDINATES = {
        {2398, 5086}, {2387, 5095}, {2407, 5098}, {2417, 5082}, {2390, 5076}, {2410, 5090}
    };

    public void spawnNextWave(Client c) {
        if (c == null || c.waveId < 0 || c.waveId >= WAVES.length) {
            c.waveId = 0;
            return;
        }

        int npcAmount = WAVES[c.waveId].length;
        for (int j = 0; j < npcAmount; j++) {
            int npc = WAVES[c.waveId][j];
            int x = COORDINATES[j][0];
            int y = COORDINATES[j][1];
            int height = c.heightLevel;
            int hp = getHp(npc);
            int max = getMax(npc);
            int atk = getAtk(npc);
            int def = getDef(npc);
            Server.npcHandler.spawnNpc(c, npc, x, y, height, 0, hp, max, atk, def, true, false);
        }
        c.tzhaarToKill = npcAmount;
        c.tzhaarKilled = 0;
    }

    public int getHp(int npc) {
        return switch (npc) {
            case 2627 -> 10;
            case 2630 -> 20;
            case 2631 -> 40;
            case 2741 -> 80;
            case 2743 -> 150;
            case 2745 -> 250;
            default -> 100;
        };
    }

    public int getMax(int npc) {
        return switch (npc) {
            case 2627 -> 4;
            case 2630 -> 7;
            case 2631 -> 13;
            case 2741 -> 28;
            case 2743 -> 54;
            case 2745 -> 97;
            default -> 5;
        };
    }

    public int getAtk(int npc) {
        return switch (npc) {
            case 2627 -> 30;
            case 2630 -> 50;
            case 2631 -> 100;
            case 2741 -> 150;
            case 2743 -> 450;
            case 2745 -> 650;
            default -> 100;
        };
    }

    public int getDef(int npc) {
        return switch (npc) {
            case 2627 -> 30;
            case 2630 -> 50;
            case 2631 -> 100;
            case 2741 -> 150;
            case 2743 -> 300;
            case 2745 -> 500;
            default -> 100;
        };
    }
}
