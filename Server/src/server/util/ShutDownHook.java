package server.util;

import server.model.players.Client;
import server.model.players.PlayerSave;
import server.Server;

public class ShutDownHook extends Thread {

    @Override
    public void run() {
        System.out.println("Shutdown thread run.");
        try {
            for (int j = 0; j < Server.playerHandler.players.length; j++) {
                if (Server.playerHandler.players[j] != null) {
                    Client c = (Client) Server.playerHandler.players[j];
                    try {
                        PlayerSave.saveGame(c);
                    } catch (Exception e) {
                        System.err.println("Failed to save player " + c.playerName);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("All players saved.");
        } catch (Exception e) {
            System.err.println("Error during shutdown: ");
            e.printStackTrace();
        } finally {
            System.out.println("Shutting down...");
        }
    }
}
