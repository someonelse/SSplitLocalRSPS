package server.world;

import server.model.players.Client;
import server.Server;

public class ClanChatHandler {

    public Clan[] clans = new Clan[100];

    public ClanChatHandler() {}

    public void handleClanChat(Client c, String name) {
        for (int j = 0; j < clans.length; j++) {
            if (clans[j] != null && clans[j].name.equalsIgnoreCase(name)) {
                addToClan(c.playerId, j);
                return;
            }
        }
        makeClan(c, name);
    }

    public void makeClan(Client c, String name) {
        int open = openClan();
        if (open >= 0) {
            if (validName(name)) {
                c.clanId = open;
                clans[c.clanId] = new Clan(c, name);
                addToClan(c.playerId, c.clanId);
            } else {
                c.sendMessage("A clan with this name already exists.");
            }
        } else {
            c.sendMessage("Your clan chat request could not be completed.");
        }
    }

    public void updateClanChat(int clanId) {
        var clan = clans[clanId];
        for (int memberId : clan.members) {
            if (memberId <= 0) continue;
            var member = Server.playerHandler.players[memberId];
            if (member != null) {
                Client c = (Client) member;
                c.getPA().sendFrame126("Talking in: " + clan.name, 18139);
                c.getPA().sendFrame126("Owner: " + clan.owner, 18140);
                int slotToFill = 18144;
                for (int mid : clan.members) {
                    if (mid > 0 && Server.playerHandler.players[mid] != null) {
                        c.getPA().sendFrame126(Server.playerHandler.players[mid].playerName, slotToFill++);
                    }
                }
                for (int k = slotToFill; k < 18244; k++)
                    c.getPA().sendFrame126("", k);
            }
        }
    }

    public int openClan() {
        for (int j = 0; j < clans.length; j++) {
            if (clans[j] == null || clans[j].owner.isEmpty())
                return j;
        }
        return -1;
    }

    public boolean validName(String name) {
        for (var clan : clans) {
            if (clan != null && clan.name.equalsIgnoreCase(name))
                return false;
        }
        return true;
    }

    public void addToClan(int playerId, int clanId) {
        var clan = clans[clanId];
        if (clan != null) {
            for (int j = 0; j < clan.members.length; j++) {
                if (clan.members[j] <= 0) {
                    clan.members[j] = playerId;
                    Server.playerHandler.players[playerId].clanId = clanId;
                    Client c = (Client) Server.playerHandler.players[playerId];
                    messageToClan(Server.playerHandler.players[playerId].playerName + " has joined the channel.", clanId);
                    updateClanChat(clanId);
                    return;
                } else if (clan.members[j] == playerId) {
                    Client c = (Client) Server.playerHandler.players[playerId];
                    Server.playerHandler.players[playerId].clanId = clanId;
                    c.sendMessage("You already joined a clan, Don't try to join twice!");
                    return;
                }
            }
        }
    }

    public void leaveClan(int playerId, int clanId) {
        if (clanId < 0) {
            Client c = (Client) Server.playerHandler.players[playerId];
            c.sendMessage("You are not in a clan.");
            return;
        }
        var clan = clans[clanId];
        if (clan != null) {
            for (int j = 0; j < clan.members.length; j++) {
                if (clan.members[j] == playerId) {
                    clan.members[j] = -1;
                }
            }
            var player = Server.playerHandler.players[playerId];
            if (player != null) {
                Client c = (Client) player;
                Server.playerHandler.players[playerId].clanId = -1;
                c.sendMessage("You have left the clan.");
                c.getPA().clearClanChat();
            }
            updateClanChat(clanId);
        }
    }

    public void destructClan(int clanId) {
        if (clanId < 0) return;
        var clan = clans[clanId];
        for (int memberId : clan.members) {
            if (clanId < 0) continue;
            if (memberId <= 0) continue;
            var player = Server.playerHandler.players[memberId];
            if (player != null) {
                Client c = (Client) player;
                c.clanId = -1;
                c.getPA().clearClanChat();
            }
        }
        clan.members = new int[50];
        clan.owner = "";
        clan.name = "";
    }

    public void messageToClan(String message, int clanId) {
        if (clanId < 0) return;
        var clan = clans[clanId];
        for (int memberId : clan.members) {
            if (memberId < 0) continue;
            var player = Server.playerHandler.players[memberId];
            if (player != null) {
                Client c = (Client) player;
                c.sendMessage("@red@" + message);
            }
        }
    }

    public void playerMessageToClan(int playerId, String message, int clanId) {
        Client z = (Client) Server.playerHandler.players[playerId];
        if (clanId < 0) return;
        var clan = clans[clanId];
        for (int memberId : clan.members) {
            if (memberId <= 0) continue;
            var player = Server.playerHandler.players[memberId];
            if (player != null) {
                Client c = (Client) player;
                c.sendClan(Server.playerHandler.players[playerId].playerName, message, clan.name, Server.playerHandler.players[playerId].playerRights);
            } else {
                if (clanId > 0)
                    clanId = -1;
                z.sendMessage("You are not in a clan.");
            }
        }
    }

    public void sendLootShareMessage(int clanId, String message) {
        if (clanId >= 0) {
            var clan = clans[clanId];
            for (int memberId : clan.members) {
                if (memberId <= 0) continue;
                var player = Server.playerHandler.players[memberId];
                if (player != null) {
                    Client c = (Client) player;
                    c.sendClan("Lootshare", message, clan.name, 2);
                }
            }
        }
    }

    public void handleLootShare(Client c, int item, int amount) {
        sendLootShareMessage(c.clanId, c.playerName + " has received " + amount + "x " + server.model.items.Item.getItemName(item) + ".");
    }
}
