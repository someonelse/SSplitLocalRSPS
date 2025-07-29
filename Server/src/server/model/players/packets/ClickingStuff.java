package server.model.players.packets;

import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Clicking stuff (interfaces)
 **/
public class ClickingStuff implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Decline trade if in trade and not yet accepted
        if (c.inTrade) {
            if (!c.acceptedTrade) {
                Misc.println("trade reset");
                c.getTradeAndDuel().declineTrade();
            }
        }

        // Exit banking mode
        if (c.isBanking)
            c.isBanking = false;

        // Duel interaction
        Client o = (Client) Server.playerHandler.players[c.duelingWith];
        if (o != null) {
            if (c.duelStatus >= 1 && c.duelStatus <= 4) {
                c.getTradeAndDuel().declineDuel();
                o.getTradeAndDuel().declineDuel();
            }
        }

        // Claim staked items if duel status is complete
        if (c.duelStatus == 6) {
            c.getTradeAndDuel().claimStakedItems();
        }
    }
}
