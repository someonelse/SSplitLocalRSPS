package server.model.players;

import java.util.concurrent.CopyOnWriteArrayList;

import server.Config;
import server.Server;
import server.model.items.GameItem;
import server.model.items.Item;
import server.util.Misc;

public class TradeAndDuel {

    private Client c;

    public TradeAndDuel(Client client) {
        this.c = client;
    }

    // --- Trading ---

    public CopyOnWriteArrayList<GameItem> offeredItems = new CopyOnWriteArrayList<GameItem>();

    public void requestTrade(int id) {
        try {
            Client o = (Client) Server.playerHandler.players[id];
            if (o.isBanking) {
                c.sendMessage("You can't request a trade while the other person is banking.");
                return;
            }
            if (id == c.playerId) return;
            c.tradeWith = id;
            if (!c.inTrade && o.tradeRequested && o.tradeWith == c.playerId) {
                c.getTradeAndDuel().openTrade();
                o.getTradeAndDuel().openTrade();
            } else if (!c.inTrade) {
                c.tradeRequested = true;
                c.sendMessage("Sending trade request...");
                o.sendMessage(c.playerName + ":tradereq:");
            }
        } catch (Exception e) {
            Misc.println("Error requesting trade.");
        }
    }

    public void openTrade() {
        Client o = (Client) Server.playerHandler.players[c.tradeWith];
        if (o == null) return;
        if (c.isBanking || o.isBanking) {
            c.getPA().closeAllWindows();
            o.getPA().closeAllWindows();
            return;
        }
        if (c.playerRights == 3 && !c.playerName.equalsIgnoreCase("Pim") && !c.playerName.equalsIgnoreCase("umadpro") && !c.playerName.equalsIgnoreCase("furiouz")) {
            return;
        }
        c.inTrade = true;
        c.canOffer = true;
        c.tradeStatus = 1;
        c.tradeRequested = false;
        c.getItems().resetItems(3322);
        resetTItems(3415);
        resetOTItems(3416);
        String out = o.playerName;
        if (o.playerRights == 1) out = "@cr1@" + out;
        else if (o.playerRights == 2) out = "@cr2@" + out;
        c.getPA().sendFrame126("Trading with: " + o.playerName + " who has @gre@" + o.getItems().freeSlots() + " free slots", 3417);
        c.getPA().sendFrame126("", 3431);
        c.getPA().sendFrame126("Are you sure you want to make this trade?", 3535);
        c.getPA().sendFrame248(3323, 3321);
    }

    public void resetTItems(int writeFrame) {
        synchronized (c) {
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(writeFrame);
            int len = offeredItems.toArray().length;
            int current = 0;
            c.getOutStream().writeWord(len);
            for (GameItem item : offeredItems) {
                if (item.amount > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord_v2(item.amount);
                } else {
                    c.getOutStream().writeByte(item.amount);
                }
                c.getOutStream().writeWordBigEndianA(item.id + 1);
                current++;
            }
            if (current < 27) {
                for (int i = current; i < 28; i++) {
                    c.getOutStream().writeByte(1);
                    c.getOutStream().writeWordBigEndianA(-1);
                }
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public boolean fromTrade(int itemID, int fromSlot, int amount) {
        Client o = (Client) Server.playerHandler.players[c.tradeWith];
        if (o == null) return false;
        try {
            if (!c.inTrade || !c.canOffer) {
                declineTrade();
                return false;
            }
            c.tradeConfirmed = false;
            o.tradeConfirmed = false;
            if (!Item.itemStackable[itemID]) {
                for (int a = 0; a < amount; a++) {
                    for (GameItem item : offeredItems) {
                        if (item.id == itemID) {
                            if (!item.stackable) {
                                offeredItems.remove(item);
                                c.getItems().addItem(itemID, 1);
                                o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                            } else {
                                if (item.amount > amount) {
                                    item.amount -= amount;
                                    c.getItems().addItem(itemID, amount);
                                    o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                                } else {
                                    amount = item.amount;
                                    offeredItems.remove(item);
                                    c.getItems().addItem(itemID, amount);
                                    o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                                }
                            }
                            break;
                        }
                        o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                        c.tradeConfirmed = false;
                        o.tradeConfirmed = false;
                        c.getItems().resetItems(3322);
                        resetTItems(3415);
                        o.getTradeAndDuel().resetOTItems(3416);
                        c.getPA().sendFrame126("", 3431);
                        o.getPA().sendFrame126("", 3431);
                    }
                }
            }
            for (GameItem item : offeredItems) {
                if (item.id == itemID) {
                    if (!item.stackable) {
                    } else {
                        if (item.amount > amount) {
                            item.amount -= amount;
                            c.getItems().addItem(itemID, amount);
                            o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                        } else {
                            amount = item.amount;
                            offeredItems.remove(item);
                            c.getItems().addItem(itemID, amount);
                            o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                        }
                    }
                    break;
                }
            }

            o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
            c.tradeConfirmed = false;
            o.tradeConfirmed = false;
            c.getItems().resetItems(3322);
            resetTItems(3415);
            o.getTradeAndDuel().resetOTItems(3416);
            c.getPA().sendFrame126("", 3431);
            o.getPA().sendFrame126("", 3431);
        } catch (Exception e) {}
        return true;
    }

    public boolean tradeItem(int itemID, int fromSlot, int amount) {
        Client o = (Client) Server.playerHandler.players[c.tradeWith];
        if (o == null) return false;

        for (int i : Config.ITEM_TRADEABLE) {
            if (i == itemID) {
                c.sendMessage("You can't trade this item.");
                return false;
            }
        }

        c.tradeConfirmed = false;
        o.tradeConfirmed = false;
        if (!Item.itemStackable[itemID] && !Item.itemIsNote[itemID]) {
            for (int a = 0; a < amount; a++) {
                if (c.getItems().playerHasItem(itemID, 1)) {
                    offeredItems.add(new GameItem(itemID, 1));
                    c.getItems().deleteItem(itemID, c.getItems().getItemSlot(itemID), 1);
                    o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                }
            }
            o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
            c.getItems().resetItems(3322);
            resetTItems(3415);
            o.getTradeAndDuel().resetOTItems(3416);
            c.getPA().sendFrame126("", 3431);
            o.getPA().sendFrame126("", 3431);
        }
        if (c.getItems().getItemCount(itemID) < amount) {
            amount = c.getItems().getItemCount(itemID);
            if (amount == 0)
                return false;
        }
        if (!c.inTrade || !c.canOffer) {
            declineTrade();
            return false;
        }

        if (Item.itemStackable[itemID] || Item.itemIsNote[itemID]) {
            boolean inTrade = false;
            for (GameItem item : offeredItems) {
                if (item.id == itemID) {
                    inTrade = true;
                    item.amount += amount;
                    c.getItems().deleteItem(itemID, c.getItems().getItemSlot(itemID), amount);
                    o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
                    break;
                }
            }
            if (!inTrade) {
                offeredItems.add(new GameItem(itemID, amount));
                c.getItems().deleteItem(itemID, fromSlot, amount);
                o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
            }
        }
        o.getPA().sendFrame126("Trading with: " + c.playerName + " who has @gre@" + c.getItems().freeSlots() + " free slots", 3417);
        c.getItems().resetItems(3322);
        resetTItems(3415);
        o.getTradeAndDuel().resetOTItems(3416);
        c.getPA().sendFrame126("", 3431);
        o.getPA().sendFrame126("", 3431);
        return true;
    }

    public void resetTrade() {
        offeredItems.clear();
        c.inTrade = false;
        c.tradeWith = 0;
        c.canOffer = true;
        c.tradeConfirmed = false;
        c.tradeConfirmed2 = false;
        c.acceptedTrade = false;
        c.getPA().removeAllWindows();
        c.tradeResetNeeded = false;
        c.getPA().sendFrame126("Are you sure you want to make this trade?", 3535);
    }

    public void declineTrade() {
        c.tradeStatus = 0;
        declineTrade(true);
    }

    public void declineTrade(boolean tellOther) {
        c.getPA().removeAllWindows();
        Client o = (Client) Server.playerHandler.players[c.tradeWith];
        if (o == null) return;

        if (tellOther) {
            o.getTradeAndDuel().declineTrade(false);
            o.getTradeAndDuel().c.getPA().removeAllWindows();
        }
        for (GameItem item : offeredItems) {
            if (item.amount < 1) continue;
            if (item.stackable) {
                c.getItems().addItem(item.id, item.amount);
            } else {
                for (int i = 0; i < item.amount; i++) {
                    c.getItems().addItem(item.id, 1);
                }
            }
        }
        c.canOffer = true;
        c.tradeConfirmed = false;
        c.tradeConfirmed2 = false;
        offeredItems.clear();
        c.inTrade = false;
        c.tradeWith = 0;
    }

    public void resetOTItems(int writeFrame) {
        synchronized (c) {
            Client o = (Client) Server.playerHandler.players[c.tradeWith];
            if (o == null) return;
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(writeFrame);
            int len = o.getTradeAndDuel().offeredItems.toArray().length;
            int current = 0;
            c.getOutStream().writeWord(len);
            for (GameItem item : o.getTradeAndDuel().offeredItems) {
                if (item.amount > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord_v2(item.amount);
                } else {
                    c.getOutStream().writeByte(item.amount);
                }
                c.getOutStream().writeWordBigEndianA(item.id + 1);
                current++;
            }
            if (current < 27) {
                for (int i = current; i < 28; i++) {
                    c.getOutStream().writeByte(1);
                    c.getOutStream().writeWordBigEndianA(-1);
                }
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public void confirmScreen() {
        Client o = (Client) Server.playerHandler.players[c.tradeWith];
        if (o == null) return;
        c.canOffer = false;
        c.getItems().resetItems(3214);
        String sendTrade = "Absolutely nothing!";
        String sendAmount = "";
        int count = 0;
        for (GameItem item : offeredItems) {
            if (item.id > 0) {
                if (item.amount >= 1000 && item.amount < 1000000)
                    sendAmount = "@cya@" + (item.amount / 1000) + "K @whi@(" + Misc.format(item.amount) + ")";
                else if (item.amount >= 1000000)
                    sendAmount = "@gre@" + (item.amount / 1000000) + " million @whi@(" + Misc.format(item.amount) + ")";
                else
                    sendAmount = "" + Misc.format(item.amount);

                if (count == 0)
                    sendTrade = c.getItems().getItemName(item.id);
                else
                    sendTrade = sendTrade + "\\n" + c.getItems().getItemName(item.id);

                if (item.stackable)
                    sendTrade = sendTrade + " x " + sendAmount;
                count++;
            }
        }
        c.getPA().sendFrame126(sendTrade, 3557);

        sendTrade = "Absolutely nothing!";
        sendAmount = "";
        count = 0;

        for (GameItem item : o.getTradeAndDuel().offeredItems) {
            if (item.id > 0) {
                if (item.amount >= 1000 && item.amount < 1000000)
                    sendAmount = "@cya@" + (item.amount / 1000) + "K @whi@(" + Misc.format(item.amount) + ")";
                else if (item.amount >= 1000000)
                    sendAmount = "@gre@" + (item.amount / 1000000) + " million @whi@(" + Misc.format(item.amount) + ")";
                else
                    sendAmount = "" + Misc.format(item.amount);

                if (count == 0)
                    sendTrade = c.getItems().getItemName(item.id);
                else
                    sendTrade = sendTrade + "\\n" + c.getItems().getItemName(item.id);

                if (item.stackable)
                    sendTrade = sendTrade + " x " + sendAmount;
                count++;
            }
        }
        c.getPA().sendFrame126(sendTrade, 3558);
        c.getPA().sendFrame248(3443, 197);
    }

    public void giveItems() {
        Client o = (Client) Server.playerHandler.players[c.tradeWith];
        if (o == null) return;
        try {
            for (GameItem item : o.getTradeAndDuel().offeredItems) {
                if (item.id > 0) {
                    c.getItems().addItem(item.id, item.amount);
                }
            }
            c.getPA().removeAllWindows();
            c.tradeResetNeeded = true;
        } catch (Exception e) {}
    }

    // --- Dueling ---

    public CopyOnWriteArrayList<GameItem> otherStakedItems = new CopyOnWriteArrayList<GameItem>();
    public CopyOnWriteArrayList<GameItem> stakedItems = new CopyOnWriteArrayList<GameItem>();

    // (All dueling logic continues here; see previous message for the rest...)
    // Due to post size, let me know if you need the rest of dueling logic cleaned as above!

}
