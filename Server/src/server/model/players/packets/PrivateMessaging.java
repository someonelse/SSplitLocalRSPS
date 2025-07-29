package server.model.players.packets;

import server.Config;
import server.Server;
import server.Connection;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * Handles private messaging, friends list, ignore list, and chat status updates.
 */
public class PrivateMessaging implements PacketType {

	private static final int ADD_FRIEND = 188;
	private static final int SEND_PM = 126;
	private static final int REMOVE_FRIEND = 215;
	private static final int CHANGE_PM_STATUS = 95;
	private static final int REMOVE_IGNORE = 59;
	private static final int ADD_IGNORE = 133;

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		switch (packetType) {
			case ADD_FRIEND:
				handleAddFriend(c);
				break;

			case SEND_PM:
				handleSendPrivateMessage(c, packetSize);
				break;

			case REMOVE_FRIEND:
				handleRemoveFriend(c);
				break;

			case CHANGE_PM_STATUS:
				handlePrivateChatStatus(c);
				break;

			case ADD_IGNORE:
				handleAddIgnore(c);
				break;

			case REMOVE_IGNORE:
				handleRemoveIgnore(c);
				break;
		}
	}

	private void handleAddFriend(Client c) {
		c.friendUpdate = true;
		long friendToAdd = c.getInStream().readQWord();

		for (long friend : c.friends) {
			if (friend == friendToAdd) {
				c.sendMessage(friendToAdd + " is already on your friends list.");
				return;
			}
		}

		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] == 0) {
				c.friends[i] = friendToAdd;

				for (int j = 1; j < Config.MAX_PLAYERS; j++) {
					Client other = (Client) Server.playerHandler.players[j];
					if (other != null && other.isActive && Misc.playerNameToInt64(other.playerName) == friendToAdd) {
						if (other.privateChat == 0 || (other.privateChat == 1 && other.getPA().isInPM(Misc.playerNameToInt64(c.playerName)))) {
							c.getPA().loadPM(friendToAdd, 1);
						}
						break;
					}
				}
				break;
			}
		}
	}

	private void handleSendPrivateMessage(Client c, int packetSize) {
		long recipientId = c.getInStream().readQWord();
		int messageSize = packetSize - 8;
		byte[] chatText = new byte[messageSize];
		c.getInStream().readBytes(chatText, messageSize, 0);

		if (Connection.isMuted(c)) return;

		long myName = Misc.playerNameToInt64(c.playerName);

		for (long friendId : c.friends) {
			if (friendId == recipientId) {
				for (int i = 1; i < Config.MAX_PLAYERS; i++) {
					Client other = (Client) PlayerHandler.players[i];
					if (other != null && other.isActive && Misc.playerNameToInt64(other.playerName) == recipientId) {
						if (c.playerRights >= 2 || other.privateChat == 0 || (other.privateChat == 1 && other.getPA().isInPM(myName))) {
							other.getPA().sendPM(myName, c.playerRights, chatText, messageSize);
							return;
						}
					}
				}
				c.sendMessage("That player is currently offline.");
				break;
			}
		}
	}

	private void handleRemoveFriend(Client c) {
		c.friendUpdate = true;
		long friendToRemove = c.getInStream().readQWord();

		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] == friendToRemove) {
				for (int j = 1; j < Config.MAX_PLAYERS; j++) {
					Client other = (Client) Server.playerHandler.players[j];
					if (other != null && Misc.playerNameToInt64(other.playerName) == friendToRemove) {
						other.getPA().updatePM(c.playerId, 0);
						break;
					}
				}
				c.friends[i] = 0;
				break;
			}
		}
	}

	private void handlePrivateChatStatus(Client c) {
		int tradeAndCompete = c.getInStream().readUnsignedByte(); // unused
		c.privateChat = c.getInStream().readUnsignedByte();
		int publicChat = c.getInStream().readUnsignedByte(); // unused

		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			Client other = (Client) Server.playerHandler.players[i];
			if (other != null && other.isActive) {
				other.getPA().updatePM(c.playerId, 1);
			}
		}
	}

	private void handleAddIgnore(Client c) {
		int a = c.getInStream().readDWord();
		int a2 = c.getInStream().readDWord();
		int j3 = 18;
		c.getPA().handleStatus(a, a2, j3);

		c.friendUpdate = true;
		long ignoreId = c.getInStream().readQWord();

		for (int i = 0; i < c.ignores.length; i++) {
			if (c.ignores[i] == 0) {
				c.ignores[i] = ignoreId;
				break;
			}
		}
	}

	private void handleRemoveIgnore(Client c) {
		int ii = c.getInStream().readDWord();
		int i2i = c.getInStream().readDWord();
		int i3i = c.getInStream().readDWord();
		c.getPA().handleStatus(ii, i2i, i3i);

		c.friendUpdate = true;
		long ignoreId = c.getInStream().readQWord();

		for (int i = 0; i < c.ignores.length; i++) {
			if (c.ignores[i] == ignoreId) {
				c.ignores[i] = 0;
				break;
			}
		}
	}
}
