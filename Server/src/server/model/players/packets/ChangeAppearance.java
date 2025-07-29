package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles changing player's appearance (e.g., gender, body, color customization).
 */
public class ChangeAppearance implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int gender       = c.getInStream().readSignedByte();
        int head         = c.getInStream().readSignedByte();
        int jaw          = c.getInStream().readSignedByte();
        int torso        = c.getInStream().readSignedByte();
        int arms         = c.getInStream().readSignedByte();
        int hands        = c.getInStream().readSignedByte();
        int legs         = c.getInStream().readSignedByte();
        int feet         = c.getInStream().readSignedByte();
        int hairColour   = c.getInStream().readSignedByte();
        int torsoColour  = c.getInStream().readSignedByte();
        int legsColour   = c.getInStream().readSignedByte();
        int feetColour   = c.getInStream().readSignedByte();
        int skinColour   = c.getInStream().readSignedByte();

        if (c.canChangeAppearance) {
            c.playerAppearance[0]  = gender;       // Gender
            c.playerAppearance[1]  = head;         // Head
            c.playerAppearance[2]  = torso;        // Torso
            c.playerAppearance[3]  = arms;         // Arms
            c.playerAppearance[4]  = hands;        // Hands
            c.playerAppearance[5]  = legs;         // Legs
            c.playerAppearance[6]  = feet;         // Feet
            c.playerAppearance[7]  = jaw;          // Beard
            c.playerAppearance[8]  = hairColour;   // Hair color
            c.playerAppearance[9]  = torsoColour;  // Torso color
            c.playerAppearance[10] = legsColour;   // Legs color
            c.playerAppearance[11] = feetColour;   // Feet color
            c.playerAppearance[12] = skinColour;   // Skin color

            c.getPA().removeAllWindows();          // Close appearance change UI
            c.getPA().requestUpdates();            // Request appearance update
            c.canChangeAppearance = false;         // Disallow further changes until reset
        }
    }
}

