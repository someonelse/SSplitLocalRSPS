package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Handles the third click action on an inventory item,
 * also known as "Item Option 3" or "Alternative Item Option 1".
 * 
 * Example use: glory teleport (rub)
 * 
 * @author Ryan / Lmctruck30
 */
public class ItemClick3 implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Read stream data
        int dummy1 = c.getInStream().readSignedWordBigEndianA();
        int dummy2 = c.getInStream().readSignedWordA();
        int itemId = c.getInStream().readSignedWordA();

        // Handle special item behavior
        switch (itemId) {
            case 1712: // Amulet of Glory
            case 1706: // Glory (5)
            case 1708: // Glory (4)
            case 1710: // Glory (3)
            case 1712: // Glory (2)
                c.getPA().handleGlory(itemId);
                break;

            default:
                if (c.playerRights == 3) {
                    Misc.println("DEBUG: " + c.playerName + " used Item 3rd Option on item ID: " + itemId +
                                 " [Extra: " + dummy1 + ", " + dummy2 + "]");
                }
                break;
        }
    }
}
