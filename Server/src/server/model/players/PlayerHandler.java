package server.model.players;

import java.net.InetSocketAddress;
import java.util.Arrays;
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

    // Shutdown hook: save all players on shutdown
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving players...");
            synchronized (PlayerHandler.class) {
                for (var i = 0; i < Config.MAX_PLAYERS; i++) {
                    var player = players[i];
                    if (player != null) {
                        save.saveGame((Client) player);
                    }
                }
            }
        }));
    }

    public boolean newPlayerClient(Client client) {
        int slot = -1;
        for (var i = 1; i < Config.MAX_PLAYERS; i++) {
            if (players[i] == null || players[i].disconnected) {
                slot = i;
                break;
            }
        }
        if (slot == -1) return false;
        client.handler = this;
        client.playerId = slot;
        players[slot] = client;
        players[slot].isActive = true;
        players[slot].connectedFrom = ((InetSocketAddress) client.getSession().getRemoteAddress())
            .getAddress().getHostAddress();
        if (Config.SERVER_DEBUG)
            Misc.println("Player Slot " + slot + " slot 0 " + players[0] + " Player Hit " + players[slot]);
        return true;
    }

    public void destruct() {
        for (var i = 0; i < Config.MAX_PLAYERS; i++) {
            var player = players[i];
            if (player == null) continue;
            player.destruct();
            players[i] = null;
        }
    }

    public static int getPlayerCount() {
        return playerCount;
    }

    public void updatePlayerNames() {
        playerCount = 0;
        for (var i = 0; i < Config.MAX_PLAYERS; i++) {
            var player = players[i];
            if (player != null) {
                playersCurrentlyOn[i] = player.playerName;
                playerCount++;
            } else {
                playersCurrentlyOn[i] = "";
            }
        }
    }

    public static boolean isPlayerOn(String playerName) {
        Objects.requireNonNull(playerName, "playerName");
        synchronized (players) {
            for (var name : playersCurrentlyOn) {
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
                for (var i = 1; i < Config.MAX_PLAYERS; i++) {
                    var player = players[i];
                    if (player != null) {
                        player.disconnected = true;
                    }
                }
            }

            for (var i = 0; i < Config.MAX_PLAYERS; i++) {
                var player = players[i];
                if (player == null || !player.isActive) continue;
                try {
                    if (player.disconnected && (System.currentTimeMillis() - player.logoutDelay > 10_000
                            || player.properLogout || kickAllPlayers)) {
                        handlePlayerLogout(player, i);
                        players[i] = null;
                        continue;
                    }
                    player.preProcessing();
                    while (player.processQueuedPackets()) ;
                    player.process();
                    player.postProcessing();
                    player.getNextPlayerMovement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (var i = 0; i < Config.MAX_PLAYERS; i++) {
                var player = players[i];
                if (player == null || !player.isActive) continue;
                try {
                    if (player.disconnected && (System.currentTimeMillis() - player.logoutDelay > 10_000
                            || player.properLogout || kickAllPlayers)) {
                        handlePlayerLogout(player, i);
                        players[i] = null;
                    } else {
                        var o = (Client) players[i];
                        if (!player.initialized) {
                            player.initialize();
                            player.initialized = true;
                        } else {
                            player.update();
                        }
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

            for (var i = 0; i < Config.MAX_PLAYERS; i++) {
                var player = players[i];
                if (player == null || !player.isActive) continue;
                try {
                    player.clearUpdateFlags();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePlayerLogout(Player player, int index) {
        if (player.inTrade) {
            var o = (Client) Server.playerHandler.players[player.tradeWith];
            if (o != null) {
                o.getTradeAndDuel().declineTrade();
            }
        }
        if (player.duelStatus == 5) {
            var o = (Client) Server.playerHandler.players[player.duelingWith];
            if (o != null) {
                o.getTradeAndDuel().duelVictory();
            }
        } else if (player.duelStatus <= 4 && player.duelStatus >= 1) {
            var o = (Client) Server.playerHandler.players[player.duelingWith];
            if (o != null) {
                o.getTradeAndDuel().declineDuel();
            }
        }
        var o = (Client) Server.playerHandler.players[index];
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
            var size = plr.npcListSize;
            plr.npcListSize = 0;
            for (var i = 0; i < size; i++) {
                if (!plr.RebuildNPCList && plr.withinDistance(plr.npcList[i])) {
                    plr.npcList[i].updateNPCMovement(str);
                    plr.npcList[i].appendNPCUpdateBlock(updateBlock);
                    plr.npcList[plr.npcListSize++] = plr.npcList[i];
                } else {
                    var id = plr.npcList[i].npcId;
                    plr.npcInListBitmap[id >> 3] &= ~(1 << (id & 7));
                    str.writeBits(1, 1);
                    str.writeBits(2, 3);
                }
            }

            for (var i = 0; i < NPCHandler.maxNPCs; i++) {
                if (Server.npcHandler.npcs[i] != null) {
                    var id = Server.npcHandler.npcs[i].npcId;
                    if (!plr.RebuildNPCList && (plr.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
                        // already in list
                    } else if (!plr.withinDistance(Server.npcHandler.npcs[i])) {
                        // too far
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
            var saveChatTextUpdate = plr.isChatTextUpdateRequired();
            plr.setChatTextUpdateRequired(false);
            plr.appendPlayerUpdateBlock(updateBlock);
            plr.setChatTextUpdateRequired(saveChatTextUpdate);
            str.writeBits(8, plr.playerListSize);
            var size = plr.playerListSize;
            if (size > 79) size = 79;
            plr.playerListSize = 0;
            for (var i = 0; i < size; i++) {
                if (!plr.didTeleport && !plr.playerList[i].didTeleport && plr.withinDistance(plr.playerList[i])) {
                    plr.playerList[i].updatePlayerMovement(str);
                    plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
                    plr.playerList[plr.playerListSize++] = plr.playerList[i];
                } else {
                    var id = plr.playerList[i].playerId;
                    plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
                    str.writeBits(1, 1);
                    str.writeBits(2, 3);
                }
            }

            for (var i = 0; i < Config.MAX_PLAYERS; i++) {
                if (players[i] == null || !players[i].isActive || players[i] == plr)
                    continue;
                var id = players[i].playerId;
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
            for (var i = 1; i < Config.MAX_PLAYERS; i++) {
                var player = players[i];
                if (player == null || !player.isActive) continue;
                var o = (Client) Server.playerHandler.players[i];
                if (o != null) {
                    o.getPA().updatePM(plr.playerId, 0);
                }
            }
        }
        plr.destruct();
    }
}
