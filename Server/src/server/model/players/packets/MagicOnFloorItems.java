package server.model.players.packets;

import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles magic being used on ground items (e.g., Telekinetic Grab).
 */
public class MagicOnFloorItems implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int itemY = c.getInStream().readSignedWordBigEndian();
        int itemId = c.getInStream().readUnsignedWord();
        int itemX = c.getInStream().readSignedWordBigEndian();
        int spellId = c.getInStream().readUnsignedWordA();

        // Validate item presence
        if (!Server.itemHandler.itemExists(itemId, itemX, itemY)) {
            c.stopMovement();
            return;
        }

        // Begin magic cast
        c.usingMagic = true;

        // Check spell requirements (spell ID 51 = Telekinetic Grab)
        if (!c.getCombat().checkMagicReqs(51)) {
            c.stopMovement();
            return;
        }

        // Execute grab if within range
        if (c.goodDistance(c.getX(), c.getY(), itemX, itemY, 12)) {
            int offsetY = (c.getX() - itemX) * -1;
            int offsetX = (c.getY() - itemY) * -1;

            c.teleGrabX = itemX;
            c.teleGrabY = itemY;
            c.teleGrabItem = itemId;
            c.teleGrabDelay = System.currentTimeMillis();
            c.turnPlayerTo(itemX, itemY);

            c.startAnimation(c.MAGIC_SPELLS[51][2]);
            c.gfx100(c.MAGIC_SPELLS[51][3]);

            c.getPA().createPlayersStillGfx(144, itemX, itemY, 0, 72);
            c.getPA().createPlayersProjectile(
                c.getX(), c.getY(), offsetX, offsetY, 50, 70,
                c.MAGIC_SPELLS[51][4], 50, 10, 0, 50
            );

            c.getPA().addSkillXP(c.MAGIC_SPELLS[51][7], c.playerMagic);
            c.getPA().refreshSkill(c.playerMagic);

            c.stopMovement();
        }
    }
}
