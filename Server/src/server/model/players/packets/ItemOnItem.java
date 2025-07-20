package server.model.players.packets;

/**
 * Handles when a player uses one item on another in their inventory.
 * 
 * Example: Using a needle on leather, or combining two items.
 * 
 * @author Ryan / Lmctruck30
 */

import server.model.items.UseItem;
import server.model.players.Client;
import server.model.players.PacketType;

public class ItemOnItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int usedWithSlot = c.getInStream().readUnsignedWord();
        int itemUsedSlot = c.getInStream().readUnsignedWordA();

        if (usedWithSlot < 0 || usedWithSlot >= c.playerItems.length || itemUsedSlot < 0 || itemUsedSlot >= c.playerItems.length) {
            return; // Prevent out-of-bounds errors
        }

        int usedWithItemId = c.playerItems[usedWithSlot] - 1;
        int itemUsedId = c.playerItems[itemUsedSlot] - 1;

        if (usedWithItemId < 0 || itemUsedId < 0) {
            return; // Invalid item IDs
        }

        UseItem.ItemonItem(c, itemUsedId, usedWithItemId);
    }
}
