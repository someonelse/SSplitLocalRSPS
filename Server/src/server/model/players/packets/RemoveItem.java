package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles removal of an item from various game interfaces.
 * This includes inventory, bank, shops, trading, dueling, and smithing interfaces.
 */
public class RemoveItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int interfaceId = c.getInStream().readUnsignedWordA();
        int removeSlot = c.getInStream().readUnsignedWordA();
        int removeId = c.getInStream().readUnsignedWordA();

        switch (interfaceId) {
            case 1688: // Equipment tab
                c.getItems().removeItem(removeId, removeSlot);
                break;

            case 5064: // Inventory to bank
                c.getItems().bankItem(removeId, removeSlot, 1);
                break;

            case 5382: // Bank to inventory
                c.getItems().fromBank(removeId, removeSlot, 1);
                break;

            case 3900: // Shop value (buy)
                c.getShops().buyFromShopPrice(removeId, removeSlot);
                break;

            case 3823: // Shop value (sell)
                c.getShops().sellToShopPrice(removeId, removeSlot);
                break;

            case 3322: // Trade or Duel stake
                if (c.duelStatus <= 0) {
                    c.getTradeAndDuel().tradeItem(removeId, removeSlot, 1);
                } else {
                    c.getTradeAndDuel().stakeItem(removeId, removeSlot, 1);
                }
                break;

            case 3415: // From trade
                if (c.duelStatus <= 0) {
                    c.getTradeAndDuel().fromTrade(removeId, removeSlot, 1);
                }
                break;

            case 6669: // From duel
                c.getTradeAndDuel().fromDuel(removeId, removeSlot, 1);
                break;

            case 1119: // Smithing interface
            case 1120:
            case 1121:
            case 1122:
            case 1123:
                c.getSmithing().readInput(c.playerLevel[c.playerSmithing], Integer.toString(removeId), c, 1);
                break;
        }
    }
}
