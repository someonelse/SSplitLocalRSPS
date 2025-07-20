package server.model.players.packets;

import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles the logic for following another player.
 */
public class FollowPlayer implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int followPlayer = c.getInStream().readUnsignedWordBigEndian();

        // Ensure the target player exists
        if (Server.playerHandler.players[followPlayer] == null) {
            return;
        }

        // Reset combat interactions
        c.playerIndex = 0;
        c.npcIndex = 0;

        // Reset ranged/magic status
        c.mageFollow = false;
        c.usingBow = false;
        c.usingRangeWeapon = false;

        // Set follow state
        c.followDistance = 1;
        c.followId = followPlayer;
    }
}
