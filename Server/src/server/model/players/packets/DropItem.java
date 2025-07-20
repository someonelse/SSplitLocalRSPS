package server.model.players.packets;

import server.Config;
import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles item dropping behavior.
 */
public class DropItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int itemId = c.getInStream().readUnsignedWordA();
        c.getInStream().readUnsignedByte(); // Unused byte
        c.getInStream().readUnsignedByte(); // Unused byte
        int slot = c.getInStream().readUnsignedWordA();

        if (c.arenas()) {
            c.sendMessage("You can't drop items inside the arena!");
            return;
        }

        if (!c.getItems().playerHasItem(itemId, 1, slot)) {
            return; // Prevents cheating
        }

        boolean droppable = true;
        for (int undroppableId : Config.UNDROPPABLE_ITEMS) {
            if (itemId == undroppableId) {
                droppable = false;
                break;
            }
        }

        if (c.playerItemsN[slot] > 0 && itemId != -1 && c.playerItems[slot] == itemId + 1) {
            if (droppable) {
                if (c.underAttackBy > 0 && c.getShops().getItemShopValue(itemId) > 1000) {
                    c.sendMessage("You may not drop items worth more than 1000 while in combat.");
                    return;
                }

                c.sendMessage("Your item disappears when it touches the ground.");
                c.getItems().deleteItem(itemId, slot, c.playerItemsN[slot]);
                Server.playerHandler.saveGame(c);
            } else {
                c.sendMessage("This item cannot be dropped.");
            }
        }
    }
}
