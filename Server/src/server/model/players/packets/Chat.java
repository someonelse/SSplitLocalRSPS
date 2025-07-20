package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.Client;
import server.model.players.PacketType;
import server.Connection;
import server.Config;

/**
 * Handles player chat input
 */
public class Chat implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Read chat text effects and color
        c.setChatTextEffects(c.getInStream().readUnsignedByteS());
        c.setChatTextColor(c.getInStream().readUnsignedByteS());

        // Determine chat message size
        c.setChatTextSize((byte) (c.packetSize - 2));

        // Read the actual chat message into buffer
        c.inStream.readBytes_reverseA(c.getChatText(), c.getChatTextSize(), 0);

        // If player is not muted, mark chat update as needed
        if (!Connection.isMuted(c)) {
            c.setChatTextUpdateRequired(true);
        }
    }
}
