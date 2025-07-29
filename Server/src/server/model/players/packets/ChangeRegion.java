package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles logic when a single region is changed (e.g., chunk reload).
 */
public class ChangeRegion implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Removes existing region-specific custom objects
        c.getPA().removeObjects();

        // Optionally reload server-managed objects for this region
        //Server.objectManager.loadObjects(c); // Uncomment if dynamic objects are needed
    }
}
