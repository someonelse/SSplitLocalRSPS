package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles idle logout packets, typically triggered when a player is inactive
 * for an extended period of time.
 */
public class IdleLogout implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Uncomment this logic if you want to allow idle logout for all users except "End Game".
        // if (!c.playerName.equalsIgnoreCase("End Game")) {
        //     c.logout();
        // }
    }
}
