package server.task;

import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.util.ScriptManager;

public class TaskFactory {

    public static Task getDelayedTask(final String callbackFunction,
                                      final Client client,
                                      final int actionX,
                                      final int actionY) {
        return new Task(client, false) {
            @Override
            public void run() {
                synchronized (PlayerHandler.players) {
                    ScriptManager.callFunc(callbackFunction, client, actionX, actionY);
                }
            }
        };
    }

    public static Task getDelayedTask(final String callbackFunction,
                                      final Client client,
                                      final int actionID,
                                      final int actionX,
                                      final int actionY) {
        return new Task(client, false) {
            @Override
            public void run() {
                synchronized (PlayerHandler.players) {
                    ScriptManager.callFunc(callbackFunction, client, actionID, actionX, actionY);
                }
            }
        };
    }

    public static Task getDelayedGlobalTask(final String callbackFunction,
                                            final int actionID,
                                            final int actionX,
                                            final int actionY) {
        return new Task(null, true) {
            @Override
            public void run() {
                synchronized (PlayerHandler.players) {
                    ScriptManager.callFunc(callbackFunction, actionID, actionX, actionY);
                }
            }
        };
    }

    // Abstract task structure for scheduling
    public static class Task implements Runnable {
        private final Client client;
        private final boolean global;

        public Task(Client client, boolean global) {
            this.client = client;
            this.global = global;
        }

        @Override
        public void run() {
            // To be overridden
        }

        public Client getClient() {
            return client;
        }

        public boolean isGlobal() {
            return global;
        }
    }
}
