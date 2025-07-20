package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles using magic spells on items (e.g., High Alchemy, Low Alchemy, Item Enchanting).
 */
public class MagicOnItems implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int slot = c.getInStream().readSignedWord();       // Inventory slot of the item
        int itemId = c.getInStream().readSignedWordA();    // Item ID being used on
        int unused = c.getInStream().readSignedWord();     // Unused data, typically junk
        int spellId = c.getInStream().readSignedWordA();   // Spell interface ID

        c.usingMagic = true;
        c.getPA().magicOnItems(slot, itemId, spellId);
        c.usingMagic = false;
    }
}
