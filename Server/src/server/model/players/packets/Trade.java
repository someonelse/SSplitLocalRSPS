package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles trade request packets.
 */
public class Trade implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        if (c == null || c.getInStream() == null) return;

        int tradeId = c.getInStream().readSignedWordBigEndian();
        c.getPA().resetFollow();

        // Restrict trading in certain areas
        if (c.arenas()) {
            c.sendMessage("You can't trade inside the arena!");
            return;
        }

        // Restrict trading for certain admin accounts
        if (c.playerRights == 3 && !isExemptAdmin(c.playerName)) {
            c.sendMessage("Trading as a Co-Owner has been disabled. Use a regular account.");
            return;
        }

        // Prevent trading with yourself
        if (tradeId != c.playerId) {
            c.getTradeAndDuel().requestTrade(tradeId);
        }
    }

    /**
     * Check if the admin is allowed to trade.
     */
    private boolean isExemptAdmin(String playerName) {
        return playerName.equalsIgnoreCase("Zone")
            || playerName.equalsIgnoreCase("AdminName1")
            || playerName.equalsIgnoreCase("AdminName2")
            || playerName.equalsIgnoreCase("AdminName3");
    }
}
