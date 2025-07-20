package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Bank X Items
 **/
public class BankX2 implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int xAmount = c.getInStream().readDWord();

        if (xAmount == 0)
            xAmount = 1;

        switch (c.xInterfaceId) {
            case 5064:
                c.getItems().bankItem(c.playerItems[c.xRemoveSlot], c.xRemoveSlot, xAmount);
                break;

            case 5382:
                c.getItems().fromBank(c.bankItems[c.xRemoveSlot], c.xRemoveSlot, xAmount);
                break;

            case 3322:
                if (c.duelStatus <= 0) {
                    c.getTradeAndDuel().tradeItem(c.xRemoveId, c.xRemoveSlot, xAmount);
                } else {
                    c.getTradeAndDuel().stakeItem(c.xRemoveId, c.xRemoveSlot, xAmount);
                }
                break;

            case 3415:
                if (c.duelStatus <= 0) {
                    c.getTradeAndDuel().fromTrade(c.xRemoveId, c.xRemoveSlot, xAmount);
                }
                break;

            case 6669:
                c.getTradeAndDuel().fromDuel(c.xRemoveId, c.xRemoveSlot, xAmount);
                break;
        }
    }
}
