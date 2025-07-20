package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Bank 10 Items
 **/
public class Bank10 implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readUnsignedWordBigEndian();
		int removeId = c.getInStream().readUnsignedWordA();
		int removeSlot = c.getInStream().readUnsignedWordA();

		switch (interfaceId) {
			case 1688 -> // Equip tab operate
				c.getPA().useOperate(removeId);

			case 3900 -> // Shop buy
				c.getShops().buyItem(removeId, removeSlot, 5);

			case 3823 -> // Shop sell
				c.getShops().sellItem(removeId, removeSlot, 5);

			case 5064 -> // Inventory to bank
				c.getItems().bankItem(removeId, removeSlot, 10);

			case 5382 -> // Bank to inventory
				c.getItems().fromBank(removeId, removeSlot, 10);

			case 3322 -> { // Trade or stake
				if (c.duelStatus <= 0) {
					c.getTradeAndDuel().tradeItem(removeId, removeSlot, 10);
				} else {
					c.getTradeAndDuel().stakeItem(removeId, removeSlot, 10);
				}
			}

			case 3415 -> { // From trade window
				if (c.duelStatus <= 0) {
					c.getTradeAndDuel().fromTrade(removeId, removeSlot, 10);
				}
			}

			case 6669 -> // From duel window
				c.getTradeAndDuel().fromDuel(removeId, removeSlot, 10);

			case 1119, 1120, 1121, 1122, 1123 -> // Smithing interfaces
				c.getSmithing().readInput(c.playerLevel[c.playerSmithing], Integer.toString(removeId), c, 5); // still 5 here, unchanged
		}
	}
}
