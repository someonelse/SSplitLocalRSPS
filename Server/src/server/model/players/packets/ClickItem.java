package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Clicking an item, bury bone, eat food etc.
 */
public class ClickItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int junk = c.getInStream().readSignedWordBigEndianA();
        int itemSlot = c.getInStream().readUnsignedWordA();
        int itemId = c.getInStream().readUnsignedWordBigEndian();

        if (itemId != c.playerItems[itemSlot] - 1) {
            return;
        }

        // Handle pouch usage
        if (itemId >= 5509 && itemId <= 5514) {
            int pouch = switch (itemId) {
                case 5509 -> 0;
                case 5510 -> 1;
                case 5512 -> 2;
                case 5514 -> 3;
                default -> -1;
            };
            if (pouch != -1) {
                c.getPA().fillPouch(pouch);
            }
            return;
        }

        // Handle unidentified herbs
        if (c.getHerblore().isUnidHerb(itemId)) {
            c.getHerblore().handleHerbClick(itemId);
        }

        // Handle food
        if (c.getFood().isFood(itemId)) {
            c.getFood().eat(itemId, itemSlot);
        }

        // Handle potion
        if (c.getPotions().isPotion(itemId)) {
            c.getPotions().handlePotion(itemId, itemSlot);
        }

        // Handle bone
        if (c.getPrayer().isBone(itemId)) {
            c.getPrayer().buryBone(itemId, itemSlot);
        }

        // Handle spade digging and area teleport logic
        if (itemId == 952) {
            if (c.inArea(3553, 3301, 3561, 3294)) {
                c.teleTimer = 3;
                c.newLocation = 1;
            } else if (c.inArea(3550, 3287, 3557, 3278)) {
                c.teleTimer = 3;
                c.newLocation = 2;
            } else if (c.inArea(3561, 3292, 3568, 3285)) {
                c.teleTimer = 3;
                c.newLocation = 3;
            } else if (c.inArea(3570, 3302, 3579, 3293)) {
                c.teleTimer = 3;
                c.newLocation = 4;
            } else if (c.inArea(3571, 3285, 3582, 3278)) {
                c.teleTimer = 3;
                c.newLocation = 5;
            } else if (c.inArea(3562, 3279, 3569, 3273)) {
                c.teleTimer = 3;
                c.newLocation = 6;
            }
        }

        // If needed later:
        // ScriptManager.callFunc("itemClick_" + itemId, c, itemId, itemSlot);
    }
}
