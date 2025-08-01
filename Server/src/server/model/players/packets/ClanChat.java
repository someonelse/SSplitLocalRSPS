package server.model.players.packets;

import server.util.Misc;
import server.model.players.Client;
import server.model.players.PacketType;
import server.Server;

/**
 * Handles joining clan chat
 */
public class ClanChat implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        String textSent = Misc.longToPlayerName2(c.getInStream().readQWord());
        textSent = textSent.replace("_", " ");
        
        // Original debug/test line:
        // c.sendMessage(textSent);

        Server.clanChat.handleClanChat(c, textSent);
    }
}
