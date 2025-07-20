package server.model.players.packets;

import server.model.items.UseItem;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles when an item is used on an object.
 * For example: using a seed on a farming patch, or logs on a fire.
 * 
 * @author Ryan / Lmctruck30
 */
public class ItemOnObject implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int a = c.getInStream().readUnsignedWord(); // unknown/unused
        int objectId = c.getInStream().readSignedWordBigEndian();
        int objectY = c.getInStream().readSignedWordBigEndianA();
        int b = c.getInStream().readUnsignedWord(); // unknown/unused
        int objectX = c.getInStream().readSignedWordBigEndianA();
        int itemId = c.getInStream().readUnsignedWord();

        UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
    }
}
