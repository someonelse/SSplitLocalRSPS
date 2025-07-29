package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Wear Item
 */
public class WearItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        c.wearId = c.getInStream().readUnsignedWord();
        c.wearSlot = c.getInStream().readUnsignedWordA();
        c.interfaceId = c.getInStream().readUnsignedWordA();

        // Prevent equipment during combat
        if (c.playerIndex > 0 || c.npcIndex > 0) {
            c.getCombat().resetPlayerAttack();
        }

        // Handle rune pouch usage
        if (c.wearId >= 5509 && c.wearId <= 5515) {
            int pouch = getPouchIndex(c.wearId);
            if (pouch != -1) {
                c.getPA().emptyPouch(pouch);
            }
            return;
        }

        // Basic validation (optional but good practice)
        if (c.wearSlot < 0 || c.wearSlot >= c.playerEquipment.length) {
            return;
        }

        if (c.wearId <= 0) {
            return;
        }

        c.getItems().wearItem(c.wearId, c.wearSlot);
    }

    private int getPouchIndex(int itemId) {
        switch (itemId) {
            case 5509:
                return 0;
            case 5510:
                return 1;
            case 5512:
                return 2;
            case 5514:
                return 3;
            default:
                return -1;
        }
    }
}
