package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Handles silent packets (no operation).
 * These packets are often sent by the client periodically or in response to certain events,
 * but do not require any action on the server.
 */
public class SilentPacket implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        // Intentionally left blank - this packet requires no processing.
    }
}
