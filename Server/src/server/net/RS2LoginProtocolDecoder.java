package server.net;

import java.util.Arrays;

import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import server.Config;
import server.Connection;
import server.Server;
import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.model.players.PlayerSave;
import server.util.ISAACRandomGen;
import server.net.GameCodecFactory;

/**
 * RS2 Login Protocol Decoder
 */
public class RS2LoginProtocolDecoder extends CumulativeProtocolDecoder {

    private static final int HANDSHAKE_OPCODE = 14;
    private static final int LOGIN_ENCRYPTED_OPCODE = 10;

    @Override
    protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) {
        synchronized (session) {
            Integer stageObj = (Integer) session.getAttribute("LOGIN_STAGE");
            int stage = (stageObj == null ? 0 : stageObj);

            switch (stage) {
                case 0:
                    return handleInitialHandshake(session, in);
                case 1:
                    return handleEncryptedLogin(session, in);
                default:
                    return false;
            }
        }
    }

    /**
     * Stage 0: Read [opcode, nameHash] and respond
     * with [8 bytes padding][0 marker][8-byte sessionKey].
     */
    private boolean handleInitialHandshake(IoSession session, ByteBuffer in) {
        if (in.remaining() < 2) {
            in.rewind();
            return false;
        }

        int opcode   = in.get() & 0xFF;
        int nameHash = in.get() & 0xFF;

        if (opcode != HANDSHAKE_OPCODE) {
            System.err.println("[HANDSHAKE] Invalid opcode: " + opcode);
            session.close();
            return false;
        }

        // generate session key
        long serverSessionKey = ((long)(Math.random() * 0x7FFFFFFFL) << 32)
                              | ((long)(Math.random() * 0x7FFFFFFFL) & 0xFFFFFFFFL);
        session.setAttribute("SERVER_SESSION_KEY", serverSessionKey);

        // build handshake packet
        StaticPacketBuilder pb = new StaticPacketBuilder()
                .setBare(true)
                .addBytes(new byte[8])         // padding
                .addByte((byte) 0)             // success marker
                .addLong(serverSessionKey);    // session key

        byte[] data = pb.toPacket().getData();
        System.out.println("[HANDSHAKE] Sending (" + data.length + " bytes): " + Arrays.toString(data));

        session.write(pb.toPacket());
        session.setAttribute("LOGIN_STAGE", 1);
        return true;
    }

    /**
     * Stage 1: Read encrypted login block, extract credentials,
     * then call finalizeLogin().
     */
    private boolean handleEncryptedLogin(IoSession session, ByteBuffer in) {
        if (in.remaining() < 2) {
            in.rewind();
            return false;
        }

        int loginType      = in.get() & 0xFF;
        int packetSize     = in.get() & 0xFF;
        int encryptSize    = packetSize - (36 + 1 + 1 + 2);

        if (packetSize <= 0 || encryptSize <= 0 || in.remaining() < packetSize) {
            System.err.println("[LOGIN] Bad packet size: packetSize=" + packetSize +
                               ", encryptSize=" + encryptSize +
                               ", remaining=" + in.remaining());
            session.close();
            return false;
        }

        int magic = in.get() & 0xFF;
        int clientVersion = in.getUnsignedShort();
        if (magic != 255) {
            System.err.println("[LOGIN] Bad magic: " + magic);
            session.close();
            return false;
        }

        in.get(); // lowMem flag
        for (int i = 0; i < 9; i++) {
            in.getInt(); // CRCs
        }

        encryptSize--;
        int actualEncryptSize = in.get() & 0xFF;
        if (actualEncryptSize != encryptSize) {
            System.err.println("[LOGIN] Encrypt size mismatch: expected=" +
                               encryptSize + ", actual=" + actualEncryptSize);
            session.close();
            return false;
        }

        int encOpcode = in.get() & 0xFF;
        if (encOpcode != LOGIN_ENCRYPTED_OPCODE) {
            System.err.println("[LOGIN] Bad encrypted opcode: " + encOpcode);
            session.close();
            return false;
        }

        long clientKey = in.getLong();
        long serverKey = in.getLong();
        int uid        = in.getInt();
        if (uid == 0 || uid == 99735086) {
            System.err.println("[LOGIN] Rejected UID: " + uid);
            session.close();
            return false;
        }

        String username = readRS2String(in).trim().toLowerCase();
        String password = readRS2String(in).toLowerCase();

        System.out.println("[LOGIN] Credentials received → user: " + username +
                           ", pass: " + password +
                           ", uid: " + uid +
                           ", clientKey: " + clientKey +
                           ", serverKey: " + serverKey);

        int[] keys = {
            (int)(clientKey >>> 32), (int)clientKey,
            (int)(serverKey >>> 32), (int)serverKey
        };
        ISAACRandomGen inCipher  = new ISAACRandomGen(keys);
        for (int i = 0; i < 4; i++) {
            keys[i] += 50;
        }
        ISAACRandomGen outCipher = new ISAACRandomGen(keys);

        return finalizeLogin(session, uid, username, password, inCipher, outCipher, clientVersion);
    }

    private boolean finalizeLogin(IoSession session,
                                  int uid, String username, String password,
                                  ISAACRandomGen inCipher, ISAACRandomGen outCipher,
                                  int version) {

        int returnCode = validateLogin(username, version);
        Client player  = new Client(session, -1);
        player.playerName  = username;
        player.playerName2 = username;
        player.playerPass  = password;
        player.properName  = Character.toUpperCase(username.charAt(0)) +
                             username.substring(1);
        player.setInStreamDecryption(inCipher);
        player.setOutStreamDecryption(outCipher);
        player.outStream.packetEncryption = outCipher;

        if (returnCode == 2) {
            int loadResult = PlayerSave.loadGame(player, username, password);
            if (loadResult == 0) {
                player.addStarter = true;
            } else if (loadResult == 3) {
                returnCode = 3; // bad password
            } else {
                player.saveCharacter = true;
                for (int i = 0; i < player.playerEquipment.length; i++) {
                    if (player.playerEquipment[i] == 0) {
                        player.playerEquipment[i]  = -1;
                        player.playerEquipmentN[i] = 0;
                    }
                }
                if (!Server.playerHandler.newPlayerClient(player)) {
                    returnCode = 7; // server full
                }
            }
        }

        session.setAttachment(player);
        player.isActive = true;

        // build 3‐byte response
        StaticPacketBuilder pb = new StaticPacketBuilder().setBare(true);
        pb.addByte((byte)returnCode);
        if (returnCode == 2) {
            pb.addByte((byte)(player.playerRights == 3 ? 2 : player.playerRights));
        } else if (returnCode == 21) {
            pb.addByte((byte)1);
        } else {
            pb.addByte((byte)0);
        }
        pb.addByte((byte)0);

        byte[] resp = pb.toPacket().getData();
        System.out.println("[LOGIN FINAL] Sending " + Arrays.toString(resp) +
                           " → code=" + returnCode + " (" + explainCode(returnCode) + ")");

        session.write(pb.toPacket()).addListener(new IoFutureListener() {
            @Override
            public void operationComplete(IoFuture future) {
                session.getFilterChain().remove("protocolFilter");
                session.getFilterChain().addFirst("protocolFilter",
                        new ProtocolCodecFilter(new GameCodecFactory(inCipher)));
            }
        });

        return true;
    }

    private int validateLogin(String name, int version) {
        if (!name.matches("[A-Za-z0-9 ]+")) return 4;
        if (name.length() > 12)            return 8;
        if (Connection.isNamedBanned(name)) return 4;
        if (PlayerHandler.isPlayerOn(name)) return 5;
        if (PlayerHandler.playerCount >= Config.MAX_PLAYERS) return 7;
        if (Server.UpdateServer)           return 14;
        return 2;
    }

    private String readRS2String(ByteBuffer in) {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = in.get()) != 10) {
            sb.append((char)b);
        }
        return sb.toString();
    }

    private String explainCode(int code) {
        switch (code) {
            case 2:  return "Login successful";
            case 3:  return "Invalid password";
            case 4:  return "Invalid or banned name";
            case 5:  return "Already logged in";
            case 7:  return "Server full";
            case 8:  return "Name too long";
            case 14: return "Server update in progress";
            case 21: return "Retry login after delay";
            default: return "Unknown response code";
        }
    }
}