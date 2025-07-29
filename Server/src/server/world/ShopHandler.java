package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import server.Config;
import server.Server;
import server.util.Misc;

public class ShopHandler {

    public static int MaxShops = 101;
    public static int MaxShopItems = 101;
    public static int MaxInShopItems = 20;
    public static int MaxShowDelay = 10;
    public static int MaxSpecShowDelay = 60;
    public static int TotalShops = 0;
    public static int[][] ShopItems = new int[MaxShops][MaxShopItems];
    public static int[][] ShopItemsN = new int[MaxShops][MaxShopItems];
    public static int[][] ShopItemsDelay = new int[MaxShops][MaxShopItems];
    public static int[][] ShopItemsSN = new int[MaxShops][MaxShopItems];
    public static int[] ShopItemsStandard = new int[MaxShops];
    public static String[] ShopName = new String[MaxShops];
    public static int[] ShopSModifier = new int[MaxShops];
    public static int[] ShopBModifier = new int[MaxShops];

    public ShopHandler() {
        for (int i = 0; i < MaxShops; i++) {
            for (int j = 0; j < MaxShopItems; j++) {
                ResetItem(i, j);
                ShopItemsSN[i][j] = 0;
            }
            ShopItemsStandard[i] = 0;
            ShopSModifier[i] = 0;
            ShopBModifier[i] = 0;
            ShopName[i] = "";
        }
        TotalShops = 0;
        loadShops("shops.cfg");
    }

    public static void shophandler() {
        Misc.println("Shop Handler class successfully loaded");
    }

    public void process() {
        boolean didUpdate = false;
        for (int i = 1; i <= TotalShops; i++) {
            for (int j = 0; j < MaxShopItems; j++) {
                if (ShopItems[i][j] > 0) {
                    if (ShopItemsDelay[i][j] >= MaxShowDelay) {
                        if (j <= ShopItemsStandard[i] && ShopItemsN[i][j] <= ShopItemsSN[i][j]) {
                            if (ShopItemsN[i][j] < ShopItemsSN[i][j]) {
                                ShopItemsN[i][j]++;
                                didUpdate = true;
                                ShopItemsDelay[i][j] = 0;
                            }
                        } else if (ShopItemsDelay[i][j] >= MaxSpecShowDelay) {
                            DiscountItem(i, j);
                            ShopItemsDelay[i][j] = 0;
                            didUpdate = true;
                        }
                    }
                    ShopItemsDelay[i][j]++;
                }
            }
            if (didUpdate) {
                for (int k = 1; k < Config.MAX_PLAYERS; k++) {
                    var player = Server.playerHandler.players[k];
                    if (player != null && player.isShopping && player.myShopId == i) {
                        player.updateShop = true;
                        didUpdate = false;
                        player.updateshop(i);
                    }
                }
                didUpdate = false;
            }
        }
    }

    public void DiscountItem(int ShopID, int ArrayID) {
        ShopItemsN[ShopID][ArrayID]--;
        if (ShopItemsN[ShopID][ArrayID] <= 0) {
            ShopItemsN[ShopID][ArrayID] = 0;
            ResetItem(ShopID, ArrayID);
        }
    }

    public void ResetItem(int ShopID, int ArrayID) {
        ShopItems[ShopID][ArrayID] = 0;
        ShopItemsN[ShopID][ArrayID] = 0;
        ShopItemsDelay[ShopID][ArrayID] = 0;
    }

    public boolean loadShops(String fileName) {
        try (var characterfile = new BufferedReader(new FileReader("./Data/CFG/" + fileName))) {
            String line;
            while ((line = characterfile.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf("=");
                if (spot > -1) {
                    String token = line.substring(0, spot).trim();
                    String token2 = line.substring(spot + 1).trim();
                    String token2_2 = token2.replaceAll("\t+", "\t");
                    String[] token3 = token2_2.split("\t");
                    if (token.equals("shop")) {
                        int ShopID = Integer.parseInt(token3[0]);
                        ShopName[ShopID] = token3[1].replaceAll("_", " ");
                        ShopSModifier[ShopID] = Integer.parseInt(token3[2]);
                        ShopBModifier[ShopID] = Integer.parseInt(token3[3]);
                        for (int i = 0; i < ((token3.length - 4) / 2); i++) {
                            if (token3[(4 + (i * 2))] != null) {
                                ShopItems[ShopID][i] = Integer.parseInt(token3[(4 + (i * 2))]) + 1;
                                ShopItemsN[ShopID][i] = Integer.parseInt(token3[(5 + (i * 2))]);
                                ShopItemsSN[ShopID][i] = Integer.parseInt(token3[(5 + (i * 2))]);
                                ShopItemsStandard[ShopID]++;
                            } else {
                                break;
                            }
                        }
                        TotalShops++;
                    }
                } else if (line.equals("[ENDOFSHOPLIST]")) {
                    return true;
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
