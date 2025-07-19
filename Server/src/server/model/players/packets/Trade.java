package server.model.players.packets;

import server.Config;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int tradeId = c.getInStream().readSignedWordBigEndian();
		c.getPA().resetFollow();
		
		if(c.arenas()) {
			c.sendMessage("You can't trade inside the arena!");
			return;
		}
		
		if(c.playerRights == 3 && !c.playerName.equalsIgnoreCase("Zone") && !c.playerName.equalsIgnoreCase("")&& !c.playerName.equalsIgnoreCase("")&& !c.playerName.equalsIgnoreCase("")) {
			c.sendMessage("Trading as an Co-Owner has been disabled, make a pure account.");
			return;
		}
		if (tradeId != c.playerId)
			c.getTradeAndDuel().requestTrade(tradeId);
	}
		
}

