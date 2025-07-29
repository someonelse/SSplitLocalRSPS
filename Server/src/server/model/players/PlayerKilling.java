package server.model.players;

public class PlayerKilling {

    private Client c;

    /** 
     * How many people you have to kill before getting points again
     * for killing the same person.
     */
    public static final int NEEDED_KILLS = 10;

    /**
     * Constructor
     */
    public PlayerKilling(Client c) {
        this.c = c;
    }

    /**
     * Attempts to add a player name to the killedPlayers list.
     * @param name The name of the player killed.
     * @return true if the name was not already in the list and was added; false otherwise.
     */
    public boolean addPlayer(String name) {
        if (!c.killedPlayers.contains(name)) {
            c.killedPlayers.add(name);
            return true;
        }
        return false;
    }

    /**
     * Removes a player from the killedPlayers list if they have been in it for more than NEEDED_KILLS.
     * @param name The name of the player to check for.
     */
    public void checkForPlayer(String name) {
        int index = c.killedPlayers.indexOf(name);
        if (index >= NEEDED_KILLS) {
            c.killedPlayers.remove(name);
        }
    }
}
