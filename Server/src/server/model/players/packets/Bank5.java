package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Bank 5 Items
 **/
public class Bank5 implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readSignedWordBigEndianA();
		int removeId = c.getInStream().readSignedWordBigEndianA();
		int removeSlot = c.getInStream().readSignedWordBigEndian();

		if (c.inTrade) {
			return;
		}

		switch (interfaceId) {
			case 3900 -> // Buy from shop
				c.getShops().buyItem(removeId, removeSlot, 1);

			case 3823 -> // Sell to shop
				c.getShops().sellItem(removeId, removeSlot, 1);

			case 5064 -> // Inventory to bank
				c.getItems().bankItem(removeId, removeSlot, 5);

			case 5382 -> // From bank to inventory
				c.getItems().fromBank(removeId, removeSlot, 5);

			case 3322 -> { // Trade or duel
				if (c.duelStatus <= 0) {
					c.getTradeAndDuel().tradeItem(removeId, removeSlot, 5);
				} else {
					c.getTradeAndDuel().stakeItem(removeId, removeSlot, 5);
				}
			}

			case 3415 -> { // From trade window
				if (c.duelStatus <= 0) {
					c.getTradeAndDuel().fromTrade(removeId, removeSlot, 5);
				}
			}

			case 6669 -> // From duel stake
				c.getTradeAndDuel().fromDuel(removeId, removeSlot, 5);

			case 1119, 1120, 1121, 1122, 1123 -> // Smithing interface
				c.getSmithing().readInput(c.playerLevel[c.playerSmithing], Integer.toString(removeId), c, 5);
		}
	}
}
