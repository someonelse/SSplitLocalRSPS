package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles dialogue continuation packets from the player.
 */
public class Dialogue implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        if (c.nextChat > 0) {
            c.getDH().sendDialogues(c.nextChat, c.talkingNpc);
        } else {
            c.getDH().sendDialogues(0, -1); // Default fallback dialogue
        }
    }
}
