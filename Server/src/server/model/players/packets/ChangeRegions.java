package server.model.players.packets;

import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles region change for a player (e.g. teleporting, moving between chunks).
 */
public class ChangeRegions implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Reload region-based elements
        //Server.objectHandler.updateObjects(c); // Optional: may be deprecated or unused
        Server.itemHandler.reloadItems(c);
        Server.objectManager.loadObjects(c);
        c.getPA().castleWarsObjects();

        // Flag to indicate player data should be saved
        c.saveFile = true;

        // Reapply skull state if active
        if (c.skullTimer > 0) {
            c.isSkulled = true;
            c.headIconPk = 0;
            c.getPA().requestUpdates();
        }
    }
}
