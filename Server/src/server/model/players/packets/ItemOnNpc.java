package server.model.players.packets;

import server.Server;
import server.model.items.UseItem;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles when an item is used on an NPC.
 * For example: using bones on an altar NPC or giving quest items.
 */
public class ItemOnNpc implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int itemId = c.getInStream().readSignedWordA();
        int npcIndex = c.getInStream().readSignedWordA();
        int itemSlot = c.getInStream().readSignedWordBigEndian();

        if (npcIndex < 0 || npcIndex >= Server.npcHandler.npcs.length || Server.npcHandler.npcs[npcIndex] == null) {
            return;
        }

        int npcId = Server.npcHandler.npcs[npcIndex].npcType;
        UseItem.ItemonNpc(c, itemId, npcId, itemSlot);
    }
}
