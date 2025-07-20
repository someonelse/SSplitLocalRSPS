package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Use Item
 **/
public class UseItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int itemUsedSlot = c.getInStream().readUnsignedWordA();
        int useWithSlot = c.getInStream().readUnsignedWord();
        int itemUsedId = c.getInStream().readUnsignedWordA();
        int useWithId = c.getInStream().readUnsignedWord();

        if (!isValidSlot(c, itemUsedSlot, itemUsedId) || !isValidSlot(c, useWithSlot, useWithId)) {
            return;
        }

        if (c.getRights().isDeveloper()) {
            Misc.println(String.format("%s - Item Used: %d with Item: %d", c.playerName, itemUsedId, useWithId));
        }

        c.getItemAssistant().resetItems(3214);
        c.getPlayerAssistant().resetAnimation();

        if (c.getHerblore().checkHerblore(itemUsedId, useWithId)) {
            return;
        }

        if (c.getCrafting().handleChisel(itemUsedId, useWithId)) {
            return;
        }

        if (c.getCrafting().handleLeather(itemUsedId, useWithId)) {
            return;
        }

        if (c.getFletching().handleItemOnItem(itemUsedId, useWithId)) {
            return;
        }

        if (c.getFiremaking().grabData(itemUsedId, useWithId)) {
            return;
        }

        if (c.getFiremaking().grabData(useWithId, itemUsedId)) {
            return;
        }

        if (c.getItemAssistant().makePotion(itemUsedId, useWithId)) {
            return;
        }

        if (c.getPlayerAssistant().isSkilling()) {
            return;
        }

        if (c.getSmithing().checkSmithing(itemUsedId, useWithId)) {
            return;
        }

        if (c.getSmithing().checkSmithing(useWithId, itemUsedId)) {
            return;
        }

        if (c.getSmithing().doSmelting(itemUsedId, useWithId)) {
            return;
        }

        if (c.getSmithing().doSmelting(useWithId, itemUsedId)) {
            return;
        }

        if (c.getCooking().handleItemOnItem(itemUsedId, useWithId)) {
            return;
        }

        if (c.getCooking().handleItemOnItem(useWithId, itemUsedId)) {
            return;
        }

        if (c.getItemAssistant().combineItems(itemUsedId, useWithId)) {
            return;
        }

        if (c.getSlayer().handleSlayerItemOnItem(itemUsedId, useWithId)) {
            return;
        }

        if (c.getClueScroll().handleItemOnItem(itemUsedId, useWithId)) {
            return;
        }

        if (c.getClueScroll().handleItemOnItem(useWithId, itemUsedId)) {
            return;
        }

        if (c.getItemAssistant().handleItemOnItem(itemUsedId, useWithId)) {
            return;
        }
    }

    private boolean isValidSlot(Client c, int slot, int itemId) {
        return slot >= 0 && slot < c.playerItems.length && c.playerItems[slot] == itemId + 1;
    }
}
