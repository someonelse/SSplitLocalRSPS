package server.model.players;

import java.net.InetSocketAddress;
import java.util.Objects;

import server.Config;
import server.Server;
import server.model.npcs.NPCHandler;
import server.util.Misc;
import server.util.Stream;

public class PlayerHandler {

    public static final Player[] players = new Player[Config.MAX_PLAYERS];
    public static String messageToAll = "";
    public static int playerCount = 0;
    public static final String[] playersCurrentlyOn = new String[Config.MAX_PLAYERS];
    public static boolean updateAnnounced;
    public static boolean updateRunning;
    public static int updateSeconds;
    public static long updateStartTime;
    private boolean kickAllPlayers = false;
    public static PlayerSave save;

    // Save all players on shutdown
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving players...");
            synchronized (PlayerHandler.class) {
                for (int i = 0; i < Config.MAX_PLAYERS; i++) {
                    if (players[i] != null) {
                        save.saveGame((Client) players[i]);
                    }
                }
            }
            //System.out.println("Shutting down.");
        }));
    }

    public boolean newPlayerClient(Client client1) {
        int slot = -1;
        for (int i = 1; i < Config.MAX_PLAYERS; i++) {
            if ((players[i] == null) || players[i].disconnected) {
                slot = i;
                break;
            }
        }
        if (slot == -1)
            return false;
        client1.handler = this;
        client1.playerId = slot;
        players[slot] = client1;
        players[slot].isActive = true;
        players[slot].connectedFrom = ((InetSocketAddress) client1.getSession().getRemoteAddress())
                .getAddress().getHostAddress();
        if (Config.SERVER_DEBUG)
            Misc.println("Player Slot " + slot + " slot 0 " + players[0] + " Player Hit " + players[slot]);
        return true;
    }

    public void destruct() {
        for (int i = 0; i < Config.MAX_PLAYERS; i++) {
            if (players[i] == null)
                continue;
            players[i].destruct();
            players[i] = null;
        }
    }

    public static int getPlayerCount() {
        return playerCount;
    }

    public void updatePlayerNames() {
        playerCount = 0;
        for (int i = 0; i < Config.MAX_PLAYERS; i++) {
            if (players[i] != null) {
                playersCurrentlyOn[i] = players[i].playerName;
                playerCount++;
            } else {
                playersCurrentlyOn[i] = "";
            }
        }
    }

    public static boolean isPlayerOn(String playerName) {
        Objects.requireNonNull(playerName, "playerName");
        synchronized (players) {
            for (String name : playersCurrentlyOn) {
                if (name != null && name.equalsIgnoreCase(playerName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void process() {
        synchronized (players) {
            updatePlayerNames();

            if (kickAllPlayers) {
                for (int i = 1; i < Config.MAX_PLAYERS; i++) {
                    if (players[i] != null) {
                        players[i].disconnected = true;
                    }
                }
            }

            for (int i = 0; i < Config.MAX_PLAYERS; i++) {
                if (players[i] == null || !players[i].isActive)
                    continue;
                try {
                    if (players[i].disconnected && (System.currentTimeMillis() - players[i].logoutDelay > 10_000
                            || players[i].properLogout || kickAllPlayers)) {
                        handlePlayerLogout(players[i], i);
                        players[i] = null;
                        continue;
                    }
                    players[i].preProcessing();
                    while (players[i].processQueuedPackets())
                        ;
                    players[i].process();
                    players[i].postProcessing();
                    players[i].getNextPlayerMovement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < Config.MAX_PLAYERS; i++) {
                if (players[i] == null || !players[i].isActive)
                    continue;
                try {
                    if (players[i].disconnected && (System.currentTimeMillis() - players[i].logoutDelay > 10_000
                            || players[i].properLogout || kickAllPlayers)) {
                        handlePlayerLogout(players[i], i);
                        players[i] = null;
                    } else {
                        Client o = (Client) players[i];
                        //if(o.g) {
                        if (!players[i].initialized) {
                            players[i].initialize();
                            players[i].initialized = true;
                        } else {
                            players[i].update();
                        }
                        //}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (updateRunning && !updateAnnounced) {
                updateAnnounced = true;
                Server.UpdateServer = true;
            }
            if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000L))) {
                kickAllPlayers = true;
            }

            for (int i = 0; i < Config.MAX_PLAYERS; i++) {
                if (players[i] == null || !players[i].isActive)
                    continue;
                try {
                    players[i].clearUpdateFlags();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePlayerLogout(Player player, int index) {
        if (player.inTrade) {
            Client o = (Client) Server.playerHandler.players[player.tradeWith];
            if (o != null) {
                o.getTradeAndDuel().declineTrade();
            }
        }
        if (player.duelStatus == 5) {
            Client o = (Client) Server.playerHandler.players[player.duelingWith];
            if (o != null) {
                o.getTradeAndDuel().duelVictory();
            }
        } else if (player.duelStatus <= 4 && player.duelStatus >= 1) {
            Client o = (Client) Server.playerHandler.players[player.duelingWith];
            if (o != null) {
                o.getTradeAndDuel().declineDuel();
            }
        }
        Client o = (Client) Server.playerHandler.players[index];
        if (PlayerSave.saveGame(o)) {
            System.out.println("Game saved for player " + player.playerName);
        } else {
            System.out.println("Could not save for " + player.playerName);
        }
        removePlayer(player);
    }

    private final Stream updateBlock = new Stream(new byte[Config.BUFFER_SIZE]);

    public void updateNPC(Player plr, Stream str) {
        synchronized (plr) {
            updateBlock.currentOffset = 0;
            str.createFrameVarSizeWord(65);
            str.initBitAccess();

            str.writeBits(8, plr.npcListSize);
            int size = plr.npcListSize;
            plr.npcListSize = 0;
            for (int i = 0; i < size; i++) {
                if (!plr.RebuildNPCList && plr.withinDistance(plr.npcList[i])) {
                    plr.npcList[i].updateNPCMovement(str);
                    plr.npcList[i].appendNPCUpdateBlock(updateBlock);
                    plr.npcList[plr.npcListSize++] = plr.npcList[i];
                } else {
                    int id = plr.npcList[i].npcId;
                    plr.npcInListBitmap[id >> 3] &= ~(1 << (id & 7));
                    str.writeBits(1, 1);
                    str.writeBits(2, 3);
                }
            }

            for (int i = 0; i < NPCHandler.maxNPCs; i++) {
                if (Server.npcHandler.npcs[i] != null) {
                    int id = Server.npcHandler.npcs[i].npcId;
                    if (!plr.RebuildNPCList && (plr.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
                        // Already in list
                    } else if (!plr.withinDistance(Server.npcHandler.npcs[i])) {
                        // Too far
                    } else {
                        plr.addNewNPC(Server.npcHandler.npcs[i], str, updateBlock);
                    }
                }
            }

            plr.RebuildNPCList = false;

            if (updateBlock.currentOffset > 0) {
                str.writeBits(14, 16383);
                str.finishBitAccess();
                str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
            } else {
                str.finishBitAccess();
            }
            str.endFrameVarSizeWord();
        }
    }

    public void updatePlayer(Player plr, Stream str) {
        synchronized (plr) {
            updateBlock.currentOffset = 0;
            if (updateRunning && !updateAnnounced) {
                str.createFrame(114);
                str.writeWordBigEndian(updateSeconds * 50 / 30);
            }
            plr.updateThisPlayerMovement(str);
            boolean saveChatTextUpdate = plr.isChatTextUpdateRequired();
            plr.setChatTextUpdateRequired(false);
            plr.appendPlayerUpdateBlock(updateBlock);
            plr.setChatTextUpdateRequired(saveChatTextUpdate);
            str.writeBits(8, plr.playerListSize);
            int size = plr.playerListSize;
            if (size > 79)
                size = 79;
            plr.playerListSize = 0;
            for (int i = 0; i < size; i++) {
                if (!plr.didTeleport && !plr.playerList[i].didTeleport && plr.withinDistance(plr.playerList[i])) {
                    plr.playerList[i].updatePlayerMovement(str);
                    plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
                    plr.playerList[plr.playerListSize++] = plr.playerList[i];
                } else {
                    int id = plr.playerList[i].playerId;
                    plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
                    str.writeBits(1, 1);
                    str.writeBits(2, 3);
                }
            }

            for (int i = 0; i < Config.MAX_PLAYERS; i++) {
                if (players[i] == null || !players[i].isActive || players[i] == plr)
                    continue;
                int id = players[i].playerId;
                if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0)
                    continue;
                if (!plr.withinDistance(players[i]))
                    continue;
                plr.addNewPlayer(players[i], str, updateBlock);
            }

            if (updateBlock.currentOffset > 0) {
                str.writeBits(11, 2047);
                str.finishBitAccess();
                str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
            } else {
                str.finishBitAccess();
            }
            str.endFrameVarSizeWord();
        }
    }

    public void removePlayer(Player plr) {
        if (plr.privateChat != 2) {
            for (int i = 1; i < Config.MAX_PLAYERS; i++) {
                if (players[i] == null || !players[i].isActive)
                    continue;
                Client o = (Client) Server.playerHandler.players[i];
                if (o != null) {
                    o.getPA().updatePM(plr.playerId, 0);
                }
            }
        }
        plr.destruct();
    }

}
