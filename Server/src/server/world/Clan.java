package server.world;

import server.model.players.Client;

public class Clan {

    private final String owner;
    private String name;
    private final int[] members = new int[50];
    private boolean lootshare;

    public Clan(Client c, String name) {
        this.owner = c.playerName;
        this.name = name;
    }

    public int[] getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public boolean isLootshare() {
        return lootshare;
    }

    public void setLootshare(boolean lootshare) {
        this.lootshare = lootshare;
    }
}
