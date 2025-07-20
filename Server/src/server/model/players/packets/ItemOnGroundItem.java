package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Handles using an inventory item on a ground item (e.g. bones on altar).
 * Currently only logs the action for developers.
 */
public class ItemOnGroundItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int unknown = c.getInStream().readSignedWordBigEndian(); // usually unused in most implementations
        int itemUsedId = c.getInStream().readSignedWordA();
        int groundItemId = c.getInStream().readUnsignedWord();
        int groundItemY = c.getInStream().readSignedWordA();
        int itemUsedSlot = c.getInStream().readSignedWordBigEndianA();
        int groundItemX = c.getInStream().readUnsignedWord();

        // Basic bounds and validity checks
        if (itemUsedSlot < 0 || itemUsedSlot >= c.playerItems.length) {
            return;
        }

        if (c.playerItems[itemUsedSlot] - 1 != itemUsedId) {
            return; // Ensure the item in slot matches what's expected
        }

        // Developer debug logging
        if (c.playerRights == 3) {
            Misc.println("DEBUG: Item used: " + itemUsedId + " on ground item: " + groundItemId +
                         " at [" + groundItemX + ", " + groundItemY + "]");
        }

        // TODO: Add functional behavior for supported combinations
        // e.g., bones on altar, herblore ingredient usage, firemaking, etc.
    }
}
