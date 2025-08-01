package server.model.shops;

import server.Config;
import server.Server;
import server.model.items.Item;
import server.model.players.Client;

public class ShopAssistant {

    private Client c;
    public long buyDelay;

    public ShopAssistant(Client client) {
        this.c = client;
    }

    // ---- Shop Handling ----

    public void openShop(int ShopID) {
        c.getItems().resetItems(3823);
        resetShop(ShopID);
        c.isShopping = true;
        c.myShopId = ShopID;
        c.getPA().sendFrame248(3824, 3822);
        c.getPA().sendFrame126(Server.shopHandler.ShopName[ShopID], 3901);
    }

    public void updatePlayerShop() {
        for (int i = 1; i < Config.MAX_PLAYERS; i++) {
            if (Server.playerHandler.players[i] != null) {
                if (Server.playerHandler.players[i].isShopping &&
                        Server.playerHandler.players[i].myShopId == c.myShopId &&
                        i != c.playerId) {
                    Server.playerHandler.players[i].updateShop = true;
                }
            }
        }
    }

    public void updateshop(int i) {
        resetShop(i);
    }

    public void resetShop(int ShopID) {
        synchronized (c) {
            int totalItems = 0;
            for (int i = 0; i < Server.shopHandler.MaxShopItems; i++) {
                if (Server.shopHandler.ShopItems[ShopID][i] > 0) {
                    totalItems++;
                }
            }
            if (totalItems > Server.shopHandler.MaxShopItems) {
                totalItems = Server.shopHandler.MaxShopItems;
            }
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(3900);
            c.getOutStream().writeWord(totalItems);
            int totalCount = 0;
            for (int i = 0; i < Server.shopHandler.ShopItems.length; i++) {
                if (Server.shopHandler.ShopItems[ShopID][i] > 0 ||
                        i <= Server.shopHandler.ShopItemsStandard[ShopID]) {
                    if (Server.shopHandler.ShopItemsN[ShopID][i] > 254) {
                        c.getOutStream().writeByte(255);
                        c.getOutStream().writeDWord_v2(Server.shopHandler.ShopItemsN[ShopID][i]);
                    } else {
                        c.getOutStream().writeByte(Server.shopHandler.ShopItemsN[ShopID][i]);
                    }
                    if (Server.shopHandler.ShopItems[ShopID][i] > Config.ITEM_LIMIT ||
                            Server.shopHandler.ShopItems[ShopID][i] < 0) {
                        Server.shopHandler.ShopItems[ShopID][i] = Config.ITEM_LIMIT;
                    }
                    c.getOutStream().writeWordBigEndianA(Server.shopHandler.ShopItems[ShopID][i]);
                    totalCount++;
                }
                if (totalCount > totalItems) break;
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public double getItemShopValue(int itemID, int type, int fromSlot) {
        double shopValue = 1;
        for (int i = 0; i < Config.ITEM_LIMIT; i++) {
            if (Server.itemHandler.ItemList[i] != null) {
                if (Server.itemHandler.ItemList[i].itemId == itemID) {
                    shopValue = Server.itemHandler.ItemList[i].ShopValue;
                }
            }
        }
        double totPrice = shopValue;

        if (Server.shopHandler.ShopBModifier[c.myShopId] == 1) {
            totPrice *= 1;
            totPrice *= 1;
            if (type == 1) {
                totPrice *= 1;
            }
        } else if (type == 1) {
            totPrice *= 1;
        }
        return totPrice;
    }

    public int getItemShopValue(int itemId) {
        for (int i = 0; i < Config.ITEM_LIMIT; i++) {
            if (Server.itemHandler.ItemList[i] != null) {
                if (Server.itemHandler.ItemList[i].itemId == itemId) {
                    return (int) Server.itemHandler.ItemList[i].ShopValue;
                }
            }
        }
        return 0;
    }

    // ---- Buy/Sell Value Display ----

    public void buyFromShopPrice(int removeId, int removeSlot) {
        int shopValue = (int) Math.floor(getItemShopValue(removeId, 0, removeSlot));
        shopValue *= 1.15;
        String shopAdd = "";
        if (c.myShopId >= 17) {
            c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " Soul Split points.");
            return;
        }
        if (c.myShopId == 15) {
            c.sendMessage("This item current costs " + c.getItems().getUntradePrice(removeId) + " coins.");
            return;
        }
        if (shopValue >= 1000 && shopValue < 1000000) {
            shopAdd = " (" + (shopValue / 1000) + "K)";
        } else if (shopValue >= 1000000) {
            shopAdd = " (" + (shopValue / 1000000) + " million)";
        }
        c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + shopValue + " coins" + shopAdd);
    }

    public int getSpecialItemValue(int id) {
        switch (id) {
            case 6889:
            case 6914: return 100;
            case 6916:
            case 6918:
            case 6920:
            case 6922:
            case 6924: return 50;
            case 11663:
            case 11664:
            case 11665:
            case 8842: return 30;
            case 8839:
            case 8840: return 75;
            case 10499: return 20;
            case 8845: return 5;
            case 8846: return 10;
            case 8847: return 15;
            case 8848: return 20;
            case 8849:
            case 8850: return 25;
            case 7462: return 40;
            case 10551: return 50;
            case 6570: return 35;
            case 11730: return 150;
            case 11700: return 230;
            case 11698: return 360;
            case 11696: return 300;
            case 11694: return 600;
            case 6585: return 15;
        }
        return 0;
    }

    public void sellToShopPrice(int removeId, int removeSlot) {
        for (int i : Config.ITEM_SELLABLE) {
            if (!c.playerName.equalsIgnoreCase("Pim")) return;
            if (i == removeId) {
                c.sendMessage("You can't sell " + c.getItems().getItemName(removeId).toLowerCase() + ".");
                return;
            }
        }
        boolean isIn = false;
        if (Server.shopHandler.ShopSModifier[c.myShopId] > 1) {
            for (int j = 0; j <= Server.shopHandler.ShopItemsStandard[c.myShopId]; j++) {
                if (removeId == (Server.shopHandler.ShopItems[c.myShopId][j] - 1)) {
                    isIn = true;
                    break;
                }
            }
        } else {
            isIn = true;
        }
        if (!isIn) {
            c.sendMessage("You can't sell " + c.getItems().getItemName(removeId).toLowerCase() + " to this store.");
        } else {
            int shopValue = (int) Math.floor(getItemShopValue(removeId, 1, removeSlot));
            String shopAdd = "";
            if (shopValue >= 1000 && shopValue < 1000000) {
                shopAdd = " (" + (shopValue / 1000) + "K)";
            } else if (shopValue >= 1000000) {
                shopAdd = " (" + (shopValue / 1000000) + " million)";
            }
            c.sendMessage(c.getItems().getItemName(removeId) + ": shop will buy for " + shopValue + " coins" + shopAdd);
        }
    }

    // ---- Sell Item Logic ----

    public boolean sellItem(int itemID, int fromSlot, int amount) {
        if (!c.playerName.equalsIgnoreCase("Pim")) return false;
        if (c.myShopId == 14) return false;
        for (int i : Config.ITEM_SELLABLE) {
            if (i == itemID) {
                c.sendMessage("You can't sell " + c.getItems().getItemName(itemID).toLowerCase() + ".");
                return false;
            }
        }
        if (c.playerRights >= 3 && !c.playerName.equalsIgnoreCase("Pim")) {
            c.sendMessage("You can't sell as co owner..");
            return false;
        }

        if (amount > 0 && itemID == (c.playerItems[fromSlot] - 1)) {
            if (Server.shopHandler.ShopSModifier[c.myShopId] > 1) {
                boolean isIn = false;
                for (int i = 0; i <= Server.shopHandler.ShopItemsStandard[c.myShopId]; i++) {
                    if (itemID == (Server.shopHandler.ShopItems[c.myShopId][i] - 1)) {
                        isIn = true;
                        break;
                    }
                }
                if (!isIn) {
                    c.sendMessage("You can't sell " + c.getItems().getItemName(itemID).toLowerCase() + " to this store.");
                    return false;
                }
            }
            if (amount > c.playerItemsN[fromSlot] && (Item.itemIsNote[(c.playerItems[fromSlot] - 1)] || Item.itemStackable[(c.playerItems[fromSlot] - 1)])) {
                amount = c.playerItemsN[fromSlot];
            } else if (amount > c.getItems().getItemAmount(itemID) && !Item.itemIsNote[(c.playerItems[fromSlot] - 1)] && !Item.itemStackable[(c.playerItems[fromSlot] - 1)]) {
                amount = c.getItems().getItemAmount(itemID);
            }
            int totPrice2 = 0;
            for (int i = amount; i > 0; i--) {
                totPrice2 = (int) Math.floor(getItemShopValue(itemID, 1, fromSlot));
                if (c.getItems().freeSlots() > 0 || c.getItems().playerHasItem(995)) {
                    if (!Item.itemIsNote[itemID]) {
                        c.getItems().deleteItem(itemID, c.getItems().getItemSlot(itemID), 1);
                    } else {
                        c.getItems().deleteItem(itemID, fromSlot, 1);
                    }
                    c.getItems().addItem(995, totPrice2);
                    addShopItem(itemID, 1);
                } else {
                    c.sendMessage("You don't have enough space in your inventory.");
                    break;
                }
            }
            c.getItems().resetItems(3823);
            resetShop(c.myShopId);
            updatePlayerShop();
            return true;
        }
        return true;
    }

    public boolean addShopItem(int itemID, int amount) {
        boolean added = false;
        if (amount <= 0) return false;
        if (Item.itemIsNote[itemID]) {
            itemID = c.getItems().getUnnotedItem(itemID);
        }
        for (int i = 0; i < Server.shopHandler.ShopItems.length; i++) {
            if ((Server.shopHandler.ShopItems[c.myShopId][i] - 1) == itemID) {
                Server.shopHandler.ShopItemsN[c.myShopId][i] += amount;
                added = true;
            }
        }
        if (!added) {
            for (int i = 0; i < Server.shopHandler.ShopItems.length; i++) {
                if (Server.shopHandler.ShopItems[c.myShopId][i] == 0) {
                    Server.shopHandler.ShopItems[c.myShopId][i] = (itemID + 1);
                    Server.shopHandler.ShopItemsN[c.myShopId][i] = amount;
                    Server.shopHandler.ShopItemsDelay[c.myShopId][i] = 0;
                    break;
                }
            }
        }
        return true;
    }

    // ---- Buy Item Logic ----

    public boolean buyItem(int itemID, int fromSlot, int amount) {
        if (System.currentTimeMillis() - buyDelay < 1500) return false;
        if (c.myShopId == 14) { skillBuy(itemID); return false; }
        if (c.myShopId == 15 || c.myShopId == 1) { buyVoid(itemID); return false; }
        if (itemID != itemID) {
            c.sendMessage("Don't dupe or you will be IP Banned");
            return false;
        }
        if (amount > 0) {
            if (amount > Server.shopHandler.ShopItemsN[c.myShopId][fromSlot]) {
                amount = Server.shopHandler.ShopItemsN[c.myShopId][fromSlot];
            }
            int totPrice2 = 0, slot = 0, slot1 = 0, slot3 = 0;
            if (c.myShopId >= 17) {
                handleOtherShop(itemID);
                return false;
            }
            for (int i = amount; i > 0; i--) {
                totPrice2 = (int) Math.floor(getItemShopValue(itemID, 0, fromSlot));
                slot = c.getItems().getItemSlot(995);
                slot1 = c.getItems().getItemSlot(6529);
                slot3 = c.getItems().getItemSlot(5555);
                if (slot == -1 && c.myShopId != 11 && c.myShopId != 29 && c.myShopId != 30 && c.myShopId != 31 && c.myShopId != 47) {
                    c.sendMessage("You don't have enough coins.");
                    break;
                }
                if (slot1 == -1 && (c.myShopId == 29 || c.myShopId == 30 || c.myShopId == 31)) {
                    c.sendMessage("You don't have enough tokkul.");
                    break;
                }
                if (slot3 == -1 && c.myShopId == 11) {
                    c.sendMessage("You don't have enough donator gold.");
                    break;
                }
                if (totPrice2 <= 1) {
                    totPrice2 = (int) Math.floor(getItemShopValue(itemID, 0, fromSlot));
                    totPrice2 *= 1.66;
                }
                if (c.myShopId == 29 || c.myShopId == 30 || c.myShopId == 31) {
                    if (c.playerItemsN[slot1] >= totPrice2) {
                        if (c.getItems().freeSlots() > 0) {
                            buyDelay = System.currentTimeMillis();
                            c.getItems().deleteItem(6529, slot1, totPrice2);
                            c.getItems().addItem(itemID, 1);
                            Server.shopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
                            Server.shopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > Server.shopHandler.ShopItemsStandard[c.myShopId]) {
                                Server.shopHandler.ShopItems[c.myShopId][fromSlot] = 0;
                            }
                        } else {
                            c.sendMessage("You don't have enough space in your inventory.");
                            break;
                        }
                    } else {
                        c.sendMessage("You don't have enough tokkul.");
                        break;
                    }
                } else if (c.myShopId == 47) {
                    if (c.pkPoints >= totPrice2) {
                        if (c.getItems().freeSlots() > 0) {
                            buyDelay = System.currentTimeMillis();
                            c.pkPoints -= totPrice2;
                            c.getItems().addItem(itemID, 1);
                            Server.shopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
                            Server.shopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > Server.shopHandler.ShopItemsStandard[c.myShopId]) {
                                Server.shopHandler.ShopItems[c.myShopId][fromSlot] = 0;
                            }
                        } else {
                            c.sendMessage("You don't have enough space in your inventory.");
                            break;
                        }
                    } else {
                        c.sendMessage("You don't have enough Pk Points.");
                        break;
                    }
                } else if (c.myShopId == 11) {
                    if (c.playerItemsN[slot3] >= totPrice2) {
                        if (c.getItems().freeSlots() > 0) {
                            buyDelay = System.currentTimeMillis();
                            c.getItems().deleteItem(5555, slot3, totPrice2);
                            c.getItems().addItem(itemID, 1);
                            Server.shopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
                            Server.shopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > Server.shopHandler.ShopItemsStandard[c.myShopId]) {
                                Server.shopHandler.ShopItems[c.myShopId][fromSlot] = 0;
                            }
                        } else {
                            c.sendMessage("You don't have enough space in your inventory.");
                            break;
                        }
                    } else {
                        c.sendMessage("You don't have enough donator gold.");
                        break;
                    }
                } else if (c.myShopId != 11 && c.myShopId != 29 || c.myShopId != 30 || c.myShopId != 31 || c.myShopId != 47) {
                    if (c.playerItemsN[slot] >= totPrice2) {
                        if (c.getItems().freeSlots() > 0) {
                            buyDelay = System.currentTimeMillis();
                            c.getItems().deleteItem(995, slot, totPrice2);
                            c.getItems().addItem(itemID, 1);
                            Server.shopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
                            Server.shopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > Server.shopHandler.ShopItemsStandard[c.myShopId]) {
                                Server.shopHandler.ShopItems[c.myShopId][fromSlot] = 0;
                            }
                        } else {
                            c.sendMessage("You don't have enough space in your inventory.");
                            break;
                        }
                    } else {
                        c.sendMessage("You don't have enough coins.");
                        break;
                    }
                }
            }
            c.getItems().resetItems(3823);
            resetShop(c.myShopId);
            updatePlayerShop();
            return true;
        }
        return false;
    }

    public void handleOtherShop(int itemID) {
        if (c.myShopId == 18) {
            if (c.pcPoints >= getSpecialItemValue(itemID)) {
                if (c.getItems().freeSlots() > 0) {
                    c.pcPoints -= getSpecialItemValue(itemID);
                    c.getItems().addItem(itemID, 1);
                    c.getItems().resetItems(3823);
                }
            } else {
                c.sendMessage("You do not have enough Soul Split points to buy this item.");
            }
        }
    }

    // ---- Skillcapes ----

    /*public int[][] skillCapes = {{0,9747,4319,2679},{1,2683,4329,2685},{2,2680,4359,2682},{3,2701,4341,2703},{4,2686,4351,2688},{5,2689,4347,2691},{6,2692,4343,2691},
    {7,2737,4325,2733},{8,2734,4353,2736},{9,2716,4337,2718},{10,2728,4335,2730},{11,2695,4321,2697},{12,2713,4327,2715},{13,2725,4357,2727},
    {14,2722,4345,2724},{15,2707,4339,2709},{16,2704,4317,2706},{17,2710,4361,2712},{18,2719,4355,2721},{19,2737,4331,2739},{20,2698,4333,2700}};*/
    public int[] skillCapes = {9747,9753,9750,9768,9756,9759,9762,9801,9807,9783,9798,9804,9780,9795,9792,9774,9771,9777,9786,9810,9765};

    public void openSkillCape() {
        int capes = get99Count();
        if (capes > 1)
            capes = 1;
        else
            capes = 0;
        c.myShopId = 14;
        setupSkillCapes(capes, get99Count());
    }

    public int get99Count() {
        int count = 0;
        for (int j = 0; j < c.playerLevel.length; j++) {
            if (c.getLevelForXP(c.playerXP[j]) >= 99) {
                count++;
            }
        }
        return count;
    }

    public void setupSkillCapes(int capes, int capes2) {
        synchronized (c) {
            c.getItems().resetItems(3823);
            c.isShopping = true;
            c.myShopId = 14;
            c.getPA().sendFrame248(3824, 3822);
            c.getPA().sendFrame126("Skillcape Shop", 3901);
            int totalItems = capes2;
            if (totalItems > Server.shopHandler.MaxShopItems) {
                totalItems = Server.shopHandler.MaxShopItems;
            }
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(3900);
            c.getOutStream().writeWord(totalItems);
            int totalCount = 0;
            for (int i = 0; i < 21; i++) {
                if (c.getLevelForXP(c.playerXP[i]) < 99)
                    continue;
                c.getOutStream().writeByte(1);
                c.getOutStream().writeWordBigEndianA(skillCapes[i] + 2);
                totalCount++;
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public void skillBuy(int item) {
        int nn = get99Count();
        if (nn > 1)
            nn = 1;
        else
            nn = 0;
        for (int j = 0; j < skillCapes.length; j++) {
            if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
                if (c.getItems().freeSlots() > 1) {
                    if (c.getItems().playerHasItem(995, 99000)) {
                        if (c.getLevelForXP(c.playerXP[j]) >= 99) {
                            c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 99000);
                            c.getItems().addItem(skillCapes[j] + nn, 1);
                            c.getItems().addItem(skillCapes[j] + 2, 1);
                        } else {
                            c.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
                        }
                    } else {
                        c.sendMessage("You need 99k to buy this item.");
                    }
                } else {
                    c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
                }
            }
            /*
            if (skillCapes[j][1 + nn] == item) {
                if (c.getItems().freeSlots() >= 1) {
                    if (c.getItems().playerHasItem(995,99000)) {
                        if (c.getLevelForXP(c.playerXP[j]) >= 99) {
                            c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 99000);
                            c.getItems().addItem(skillCapes[j] + nn,1);
                            c.getItems().addItem(skillCapes[j] + 2,1);
                        } else {
                            c.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
                        }
                    } else {
                        c.sendMessage("You need 99k to buy this item.");
                    }
                } else {
                    c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
                }
                break;
            }
            */
        }
        c.getItems().resetItems(3823);
    }

    // ---- Void Shop ----

    public void openVoid() {
        /*
        synchronized(c) {
            c.getItems().resetItems(3823);
            c.isShopping = true;
            c.myShopId = 15;
            c.getPA().sendFrame248(3824, 3822);
            c.getPA().sendFrame126("Void Recovery", 3901);

            int TotalItems = 5;
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(3900);
            c.getOutStream().writeWord(TotalItems);
            for (int i = 0; i < c.voidStatus.length; i++) {
                c.getOutStream().writeByte(c.voidStatus[i]);
                c.getOutStream().writeWordBigEndianA(2519 + i * 2);
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
        */
    }

    public void buyVoid(int item) {
        /*
        if (item > 2527 || item < 2518)
            return;
        //c.sendMessage("" + item);
        if (c.voidStatus[(item-2518)/2] > 0) {
            if (c.getItems().freeSlots() >= 1) {
                if (c.getItems().playerHasItem(995,c.getItems().getUntradePrice(item))) {
                    c.voidStatus[(item-2518)/2]--;
                    c.getItems().deleteItem(995,c.getItems().getItemSlot(995), c.getItems().getUntradePrice(item));
                    c.getItems().addItem(item,1);
                    openVoid();
                } else {
                    c.sendMessage("This item costs " + c.getItems().getUntradePrice(item) + " coins to rebuy.");
                }
            } else {
                c.sendMessage("I should have a free inventory space.");
            }
        } else {
            c.sendMessage("I don't need to recover this item from the void knights.");
        }
        */
    }
}
