package server.model.players.packets;

import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Walking packet handler
 */
public class Walking implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        if (c == null || c.getInStream() == null) return;

        if (c.isBanking) {
            c.isBanking = false;
        }

        // Reset combat targeting on click movement
        if (packetType == 248 || packetType == 164) {
            c.faceUpdate(0);
            c.npcIndex = 0;
            c.playerIndex = 0;
            if (c.followId > 0 || c.followId2 > 0) {
                c.getPA().resetFollow();
            }
        }

        c.getPA().removeAllWindows();

        // Prevent movement in duel when movement is disabled
        if (c.duelRule[1] && c.duelStatus == 5) {
            if (Server.playerHandler.players[c.duelingWith] != null) {
                if (!c.goodDistance(c.getX(), c.getY(), Server.playerHandler.players[c.duelingWith].getX(), Server.playerHandler.players[c.duelingWith].getY(), 1)) {
                    c.sendMessage("Walking has been disabled in this duel!");
                    c.playerIndex = 0;
                    return;
                }
            }
        }

        // Freeze mechanics
        if (c.freezeTimer > 0) {
            if (Server.playerHandler.players[c.playerIndex] != null) {
                if (c.goodDistance(c.getX(), c.getY(), Server.playerHandler.players[c.playerIndex].getX(), Server.playerHandler.players[c.playerIndex].getY(), 1)
                    && packetType != 98) {
                    c.playerIndex = 0;
                    return;
                }
            }
            if (packetType != 98) {
                c.sendMessage("A magical force stops you from moving.");
                c.playerIndex = 0;
            }
            return;
        }

        // Prevent movement while stunned
        if (System.currentTimeMillis() - c.lastSpear < 4000) {
            c.sendMessage("You have been stunned.");
            c.playerIndex = 0;
            return;
        }

        // Toggle mage-allowed flag for special packet
        if (packetType == 98) {
            c.mageAllowed = true;
        }

        // Prevent movement during trade/duel stages
        if ((c.duelStatus >= 1 && c.duelStatus <= 4) || c.duelStatus == 6) {
            if (c.duelStatus == 6) {
                c.getTradeAndDuel().claimStakedItems();
            }
            return;
        }

        if (c.respawnTimer > 3 || c.inTrade) {
            return;
        }

        // Adjust packet size for minimap clicks
        if (packetType == 248) {
            packetSize -= 14;
        }

        c.newWalkCmdSteps = (packetSize - 5) / 2;
        if (++c.newWalkCmdSteps > c.walkingQueueSize) {
            c.newWalkCmdSteps = 0;
            return;
        }

        c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;

        int firstStepX = c.getInStream().readSignedWordBigEndianA() - c.getMapRegionX() * 8;
        for (int i = 1; i < c.newWalkCmdSteps; i++) {
            c.getNewWalkCmdX()[i] = c.getInStream().readSignedByte();
            c.getNewWalkCmdY()[i] = c.getInStream().readSignedByte();
        }

        int firstStepY = c.getInStream().readSignedWordBigEndian() - c.getMapRegionY() * 8;
        c.setNewWalkCmdIsRunning(c.getInStream().readSignedByteC() == 1);

        for (int i = 0; i < c.newWalkCmdSteps; i++) {
            c.getNewWalkCmdX()[i] += firstStepX;
            c.getNewWalkCmdY()[i] += firstStepY;
        }
    }
}
