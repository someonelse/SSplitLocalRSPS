package server.model.players.packets;

import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles duel challenge requests from one player to another.
 */
public class ChallengePlayer implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        switch (packetType) {
            case 128:
                int answerPlayer = c.getInStream().readUnsignedWord();
                
                if (Server.playerHandler.players[answerPlayer] == null) {
                    return;
                }

                if (c.arenas() || c.duelStatus == 5) {
                    c.sendMessage("You can't challenge inside the arena!");
                    return;
                }

                c.sendMessage(""); // This may have been a placeholder or cleared output

                c.getTradeAndDuel().requestDuel(answerPlayer);
                break;
        }
    }
}
