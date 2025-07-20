package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles item movement between slots in a container (e.g., inventory, bank, equipment).
 */
public class MoveItems implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int containerId = c.getInStream().readUnsignedWordA(); // Typically used to determine source container
        int itemFrom = c.getInStream().readUnsignedWordA();    // Original slot index
        int itemTo = c.getInStream().readUnsignedWordA() - 128; // Destination slot index (with offset correction)

        // Prevent item rearrangement while trading
        if (c.inTrade) {
            return;
        }

        c.getItems().moveItems(itemFrom, itemTo, containerId);
    }
}
