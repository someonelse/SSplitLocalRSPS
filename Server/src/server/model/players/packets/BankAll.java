package server.model.players.packets;

import server.model.items.GameItem;
import server.model.items.Item;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Bank All Items
 **/
public class BankAll implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int removeSlot = c.getInStream().readUnsignedWordA();
		int interfaceId = c.getInStream().readUnsignedWord();
		int removeId = c.getInStream().readUnsignedWordA();

		switch (interfaceId) {
			case 3900 -> // Buy from shop
				c.getShops().buyItem(removeId, removeSlot, 10);

			case 3823 -> // Sell to shop
				c.getShops().sellItem(removeId, removeSlot, 10);

			case 5064 -> { // Inventory to bank
				if (Item.itemStackable[removeId]) {
					c.getItems().bankItem(c.playerItems[removeSlot], removeSlot, c.playerItemsN[removeSlot]);
				} else {
					c.getItems().bankItem(c.playerItems[removeSlot], removeSlot, c.getItems().itemAmount(c.playerItems[removeSlot]));
				}
			}

			case 5382 -> // From bank to inventory
				c.getItems().fromBank(c.bankItems[removeSlot], removeSlot, c.bankItemsN[removeSlot]);

			case 3322 -> { // Trade or stake
				if (c.duelStatus <= 0) {
					if (Item.itemStackable[removeId]) {
						c.getTradeAndDuel().tradeItem(removeId, removeSlot, c.playerItemsN[removeSlot]);
					} else {
						c.getTradeAndDuel().tradeItem(removeId, removeSlot, 28);
					}
				} else {
					if (Item.itemStackable[removeId] || Item.itemIsNote[removeId]) {
						c.getTradeAndDuel().stakeItem(removeId, removeSlot, c.playerItemsN[removeSlot]);
					} else {
						c.getTradeAndDuel().stakeItem(removeId, removeSlot, 28);
					}
				}
			}

			case 3415 -> { // From trade window
				if (c.duelStatus <= 0) {
					for (GameItem item : c.getTradeAndDuel().offeredItems) {
						if (item.id == removeId) {
							int amount = Item.itemStackable[removeId]
								? item.amount
								: 28;
							c.getTradeAndDuel().fromTrade(removeId, removeSlot, amount);
						}
					}
				}
			}

			case 6669 -> { // From duel stake
				if (Item.itemStackable[removeId] || Item.itemIsNote[removeId]) {
					for (GameItem item : c.getTradeAndDuel().stakedItems) {
						if (item.id == removeId) {
							c.getTradeAndDuel().fromDuel(removeId, removeSlot, item.amount);
						}
					}
				} else {
					c.getTradeAndDuel().fromDuel(removeId, removeSlot, 28);
				}
			}
		}
	}
}
