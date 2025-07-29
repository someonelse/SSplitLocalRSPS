package server.model.players.packets;

import server.Config;
import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles NPC interaction packets.
 */
public class ClickNPC implements PacketType {

    public static final int ATTACK_NPC = 72;
    public static final int MAGE_NPC = 131;
    public static final int FIRST_CLICK = 155;
    public static final int SECOND_CLICK = 17;
    public static final int THIRD_CLICK = 21;

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        c.npcIndex = 0;
        c.npcClickIndex = 0;
        c.playerIndex = 0;
        c.clickNpcType = 0;
        c.getPA().resetFollow();

        switch (packetType) {

            case ATTACK_NPC -> {
                if (!c.mageAllowed) {
                    c.mageAllowed = true;
                    c.sendMessage("I can't reach that.");
                    return;
                }

                c.npcIndex = c.getInStream().readUnsignedWordA();
                var npc = Server.npcHandler.npcs[c.npcIndex];

                if (npc == null || npc.MaxHP == 0) {
                    c.npcIndex = 0;
                    return;
                }

                if (c.autocastId > 0) c.autocasting = true;
                else if (!c.autocasting && c.spellId > 0) c.spellId = 0;

                c.faceUpdate(c.npcIndex);
                c.usingMagic = false;

                boolean usingBow = false;
                boolean usingOtherRangeWeapons = false;
                boolean usingArrows = false;
                boolean usingCross = c.playerEquipment[c.playerWeapon] == 9185;

                if (c.playerEquipment[c.playerWeapon] >= 4214 && c.playerEquipment[c.playerWeapon] <= 4223) {
                    usingBow = true;
                }

                for (int bowId : c.BOWS) {
                    if (c.playerEquipment[c.playerWeapon] == bowId) {
                        usingBow = true;
                        for (int arrowId : c.ARROWS) {
                            if (c.playerEquipment[c.playerArrows] == arrowId) {
                                usingArrows = true;
                                break;
                            }
                        }
                        break;
                    }
                }

                for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
                    if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
                        usingOtherRangeWeapons = true;
                        break;
                    }
                }

                if ((usingBow || c.autocasting) && c.goodDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), 7)) {
                    c.stopMovement();
                } else if (usingOtherRangeWeapons && c.goodDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), 4)) {
                    c.stopMovement();
                }

                if (!usingCross && !usingArrows && usingBow &&
                        (c.playerEquipment[c.playerWeapon] < 4212 || c.playerEquipment[c.playerWeapon] > 4223)) {
                    c.sendMessage("You have run out of arrows!");
                    return;
                }

                if (Config.CORRECT_ARROWS && usingBow && !c.getCombat().usingCrystalBow() &&
                        c.playerEquipment[c.playerWeapon] != 9185 &&
                        c.getCombat().correctBowAndArrows() < c.playerEquipment[c.playerArrows]) {
                    c.sendMessage("You can't use " +
                            c.getItems().getItemName(c.playerEquipment[c.playerArrows]).toLowerCase() +
                            "s with a " +
                            c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase() + ".");
                    c.stopMovement();
                    c.getCombat().resetPlayerAttack();
                    return;
                }

                if (c.playerEquipment[c.playerWeapon] == 9185 && !c.getCombat().properBolts()) {
                    c.sendMessage("You must use bolts with a crossbow.");
                    c.stopMovement();
                    c.getCombat().resetPlayerAttack();
                    return;
                }

                if (c.followId > 0) c.getPA().resetFollow();

                if (c.attackTimer <= 0) {
                    c.getCombat().attackNpc(c.npcIndex);
                    c.attackTimer++;
                }
            }

            case MAGE_NPC -> {
                if (!c.mageAllowed) {
                    c.mageAllowed = true;
                    c.sendMessage("I can't reach that.");
                    return;
                }

                c.npcIndex = c.getInStream().readSignedWordBigEndianA();
                int castingSpellId = c.getInStream().readSignedWordA();
                c.usingMagic = false;

                var npc = Server.npcHandler.npcs[c.npcIndex];
                if (npc == null || npc.MaxHP == 0 || npc.npcType == 944) {
                    c.sendMessage("You can't attack this npc.");
                    return;
                }

                for (int i = 0; i < c.MAGIC_SPELLS.length; i++) {
                    if (castingSpellId == c.MAGIC_SPELLS[i][0]) {
                        c.spellId = i;
                        c.usingMagic = true;
                        break;
                    }
                }

                if (castingSpellId == 1171) { // crumble undead
                    boolean isUndead = false;
                    for (int npcType : Config.UNDEAD_NPCS) {
                        if (npc.npcType == npcType) {
                            isUndead = true;
                            break;
                        }
                    }
                    if (!isUndead) {
                        c.sendMessage("You can only attack undead monsters with this spell.");
                        c.usingMagic = false;
                        c.stopMovement();
                        return;
                    }
                }

                if (c.autocasting) c.autocasting = false;

                if (c.usingMagic) {
                    if (c.goodDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), 6)) {
                        c.stopMovement();
                    }
                    if (c.attackTimer <= 0) {
                        c.getCombat().attackNpc(c.npcIndex);
                        c.attackTimer++;
                    }
                }
            }

            case FIRST_CLICK -> {
                c.npcClickIndex = c.inStream.readSignedWordBigEndian();
                c.npcType = Server.npcHandler.npcs[c.npcClickIndex].npcType;
                var npc = Server.npcHandler.npcs[c.npcClickIndex];
                if (c.goodDistance(npc.getX(), npc.getY(), c.getX(), c.getY(), 1)) {
                    c.turnPlayerTo(npc.getX(), npc.getY());
                    npc.facePlayer(c.playerId);
                    c.getActions().firstClickNpc(c.npcType);
                } else {
                    c.clickNpcType = 1;
                }
            }

            case SECOND_CLICK -> {
                c.npcClickIndex = c.inStream.readUnsignedWordBigEndianA();
                c.npcType = Server.npcHandler.npcs[c.npcClickIndex].npcType;
                var npc = Server.npcHandler.npcs[c.npcClickIndex];
                if (c.goodDistance(npc.getX(), npc.getY(), c.getX(), c.getY(), 1)) {
                    c.turnPlayerTo(npc.getX(), npc.getY());
                    npc.facePlayer(c.playerId);
                    c.getActions().secondClickNpc(c.npcType);
                } else {
                    c.clickNpcType = 2;
                }
            }

            case THIRD_CLICK -> {
                c.npcClickIndex = c.inStream.readSignedWord();
                c.npcType = Server.npcHandler.npcs[c.npcClickIndex].npcType;
                var npc = Server.npcHandler.npcs[c.npcClickIndex];
                if (c.goodDistance(npc.getX(), npc.getY(), c.getX(), c.getY(), 1)) {
                    c.turnPlayerTo(npc.getX(), npc.getY());
                    npc.facePlayer(c.playerId);
                    c.getActions().thirdClickNpc(c.npcType);
                } else {
                    c.clickNpcType = 3;
                }
            }
        }
    }
}
