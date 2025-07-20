package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Handles the second click action on an item in the inventory.
 * Commonly used for alternative interactions like dismantling, inspecting, etc.
 * 
 * Proper stream reading implemented.
 * 
 * @author Ryan / Lmctruck30
 */
public class ItemClick2 implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int itemId = c.getInStream().readSignedWordA();

        // Safety check to ensure player actually has the item
        if (!c.getItems().playerHasItem(itemId, 1)) {
            return;
        }

        // Handle special item interactions
        switch (itemId) {
            case 11694: // Armadyl godsword
            case 11696: // Bandos godsword
            case 11698: // Saradomin godsword
            case 11700: // Zamorak godsword
                c.sendMessage("Dismantling has been disabled due to duping.");
                break;

            default:
                // Developer/Admin debug message
                if (c.playerRights == 3) {
                    Misc.println("DEBUG: " + c.playerName + " - Item2ndOption: " + itemId);
                }
                break;
        }
    }
}
