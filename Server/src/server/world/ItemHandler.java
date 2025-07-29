package server.world;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import server.Config;
import server.Server;
import server.model.items.GroundItem;
import server.model.items.ItemList;
import server.model.players.Client;
import server.model.players.Player;
import server.util.Misc;

public class ItemHandler {

    public List<GroundItem> items = new ArrayList<>();
    public static final int HIDE_TICKS = 100;

    public ItemList[] ItemList = new ItemList[Config.ITEM_LIMIT];

    public ItemHandler() {
        for (int i = 0; i < Config.ITEM_LIMIT; i++) {
            ItemList[i] = null;
        }
        loadItemList("item.cfg");
        loadItemPrices("prices.txt");
    }

    public void addItem(GroundItem item) {
        items.add(item);
    }

    public void removeItem(GroundItem item) {
        items.remove(item);
    }

    public int itemAmount(int itemId, int itemX, int itemY) {
        for (var i : items) {
            if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
                return i.getItemAmount();
            }
        }
        return 0;
    }

    public boolean itemExists(int itemId, int itemX, int itemY) {
        for (var i : items) {
            if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
                return true;
            }
        }
        return false;
    }

    public void reloadItems(Client c) {
        for (var i : items) {
            if (c != null) {
                if (c.getItems().tradeable(i.getItemId()) || i.getName().equalsIgnoreCase(c.playerName)) {
                    if (c.distanceToPoint(i.getItemX(), i.getItemY()) <= 60) {
                        if (i.hideTicks > 0 && i.getName().equalsIgnoreCase(c.playerName)) {
                            c.getItems().removeGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                            c.getItems().createGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                        }
                        if (i.hideTicks == 0) {
                            c.getItems().removeGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                            c.getItems().createGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                        }
                    }
                }
            }
        }
    }

    public void process() {
        var toRemove = new ArrayList<GroundItem>();
        for (var i : items) {
            if (i != null) {
                if (i.hideTicks > 0) {
                    i.hideTicks--;
                }
                if (i.hideTicks == 1) {
                    i.hideTicks = 0;
                    createGlobalItem(i);
                    i.removeTicks = HIDE_TICKS;
                }
                if (i.removeTicks > 0) {
                    i.removeTicks--;
                }
                if (i.removeTicks == 1) {
                    i.removeTicks = 0;
                    toRemove.add(i);
                }
            }
        }
        for (var i : toRemove) {
            removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
        }
    }

    public int[][] brokenBarrows = {
        {4708, 4860}, {4710, 4866}, {4712, 4872}, {4714, 4878}, {4716, 4884},
        {4720, 4896}, {4718, 4890}, {4720, 4896}, {4722, 4902}, {4732, 4932},
        {4734, 4938}, {4736, 4944}, {4738, 4950}, {4724, 4908}, {4726, 4914},
        {4728, 4920}, {4730, 4926}, {4745, 4956}, {4747, 4926}, {4749, 4968},
        {4751, 4994}, {4753, 4980}, {4755, 4986}, {4757, 4992}, {4759, 4998}
    };

    public void createGroundItem(Client c, int itemId, int itemX, int itemY, int itemAmount, int playerId) {
        if (itemId > 0) {
            if (itemId >= 2412 && itemId <= 2414) {
                c.sendMessage("The cape vanishes as it touches the ground.");
                return;
            }
            if (itemId > 4705 && itemId < 4760) {
                for (var brokenBarrow : brokenBarrows) {
                    if (brokenBarrow[0] == itemId) {
                        itemId = brokenBarrow[1];
                        break;
                    }
                }
            }
            if (!server.model.items.Item.itemStackable[itemId] && itemAmount > 0) {
                for (int j = 0; j < itemAmount; j++) {
                    c.getItems().createGroundItem(itemId, itemX, itemY, 1);
                    GroundItem item = new GroundItem(itemId, itemX, itemY, 1, c.playerId, HIDE_TICKS, Server.playerHandler.players[playerId].playerName);
                    addItem(item);
                }
            } else {
                c.getItems().createGroundItem(itemId, itemX, itemY, itemAmount);
                GroundItem item = new GroundItem(itemId, itemX, itemY, itemAmount, c.playerId, HIDE_TICKS, Server.playerHandler.players[playerId].playerName);
                addItem(item);
            }
        }
    }

    public void createGlobalItem(GroundItem i) {
        for (var p : Server.playerHandler.players) {
            if (p != null) {
                var person = (Client) p;
                if (person != null && person.playerId != i.getItemController()) {
                    if (!person.getItems().tradeable(i.getItemId()) && person.playerId != i.getItemController())
                        continue;
                    if (person.distanceToPoint(i.getItemX(), i.getItemY()) <= 60) {
                        person.getItems().createGroundItem(i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                    }
                }
            }
        }
    }

    public void removeGroundItem(Client c, int itemId, int itemX, int itemY, boolean add) {
        for (var i : items) {
            if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
                if (i.hideTicks > 0 && i.getName().equalsIgnoreCase(c.playerName)) {
                    if (add) {
                        if (!c.getItems().specialCase(itemId)) {
                            if (c.getItems().addItem(i.getItemId(), i.getItemAmount())) {
                                removeControllersItem(i, c, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                                break;
                            }
                        } else {
                            c.getItems().handleSpecialPickup(itemId);
                            removeControllersItem(i, c, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                            break;
                        }
                    } else {
                        removeControllersItem(i, c, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                        break;
                    }
                } else if (i.hideTicks <= 0) {
                    if (add) {
                        if (c.getItems().addItem(i.getItemId(), i.getItemAmount())) {
                            removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                            break;
                        }
                    } else {
                        removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
                        break;
                    }
                }
            }
        }
    }

    public void removeControllersItem(GroundItem i, Client c, int itemId, int itemX, int itemY, int itemAmount) {
        c.getItems().removeGroundItem(itemId, itemX, itemY, itemAmount);
        removeItem(i);
    }

    public void removeGlobalItem(GroundItem i, int itemId, int itemX, int itemY, int itemAmount) {
        for (var p : Server.playerHandler.players) {
            if (p != null) {
                var person = (Client) p;
                if (person != null && person.distanceToPoint(itemX, itemY) <= 60) {
                    person.getItems().removeGroundItem(itemId, itemX, itemY, itemAmount);
                }
            }
        }
        removeItem(i);
    }

    public void newItemList(int ItemId, String ItemName, String ItemDescription, double ShopValue, double LowAlch, double HighAlch, int[] Bonuses) {
        int slot = -1;
        for (int i = 0; i < ItemList.length; i++) {
            if (ItemList[i] == null) {
                slot = i;
                break;
            }
        }
        if (slot == -1) return;
        var newItemList = new ItemList(ItemId);
        newItemList.itemName = ItemName;
        newItemList.itemDescription = ItemDescription;
        newItemList.ShopValue = ShopValue;
        newItemList.LowAlch = LowAlch;
        newItemList.HighAlch = HighAlch;
        newItemList.Bonuses = Bonuses;
        if (ItemId == 0x1941 || ItemId == 0x106A) {
            for (int i = 0; i < Bonuses.length; i++) {
                if (ItemId == 0x1941) {
                    Bonuses[i] = 0x7530;
                } else {
                    Bonuses[i] = 300;
                    Bonuses[10] = 250;
                }
            }
        }
        ItemList[slot] = newItemList;
    }

    public void loadItemPrices(String filename) {
        try (var s = new Scanner(new File("./data/cfg/" + filename))) {
            while (s.hasNextLine()) {
                var line = s.nextLine().split(" ");
                var temp = getItemList(Integer.parseInt(line[0]));
                if (temp != null)
                    temp.ShopValue = Integer.parseInt(line[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ItemList getItemList(int i) {
        for (var item : ItemList) {
            if (item != null && item.itemId == i) {
                return item;
            }
        }
        return null;
    }

    public boolean loadItemList(String fileName) {
        try (var characterfile = new BufferedReader(new FileReader("./Data/cfg/" + fileName))) {
            String line;
            while ((line = characterfile.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf("=");
                if (spot > -1) {
                    var token = line.substring(0, spot).trim();
                    var token2 = line.substring(spot + 1).trim();
                    var token2_2 = token2.replaceAll("\t+", "\t");
                    var token3 = token2_2.split("\t");
                    if (token.equals("item")) {
                        int[] Bonuses = new int[12];
                        for (int i = 0; i < 12; i++) {
                            if (token3.length > (6 + i) && token3[(6 + i)] != null) {
                                Bonuses[i] = Integer.parseInt(token3[(6 + i)]);
                            } else {
                                break;
                            }
                        }
                        newItemList(
                            Integer.parseInt(token3[0]),
                            token3[1].replaceAll("_", " "),
                            token3[2].replaceAll("_", " "),
                            Double.parseDouble(token3[4]),
                            Double.parseDouble(token3[4]),
                            Double.parseDouble(token3[6]),
                            Bonuses
                        );
                    }
                } else {
                    if (line.equals("[ENDOFITEMLIST]")) {
                        return true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Misc.println(fileName + ": file not found.");
            return false;
        } catch (IOException e) {
            Misc.println(fileName + ": error loading file.");
            return false;
        }
        return false;
    }
}
