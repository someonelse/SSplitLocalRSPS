package server.net;

import java.net.InetSocketAddress;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ByteBuffer;              // MINA 1.x buffer
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import server.Config;                                  // ← don’t forget the semicolon!
import server.model.players.Client;
import server.net.CodecFactory;
import server.net.HostList;
import server.net.Packet;

public class ConnectionHandler implements IoHandler {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        if (session.getAttachment() != null) {
            Client plr = (Client) session.getAttachment();
            plr.queueMessage((Packet) message);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message)
            throws Exception {
        // optional
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (session.getAttachment() != null) {
            Client plr = (Client) session.getAttachment();
            plr.disconnected = true;
        }
        HostList.getHostList().remove(session);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        if (!HostList.getHostList().add(session)) {
            session.close();
        } else {
            session.setAttribute("inList", Boolean.TRUE);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        session.close();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        int localPort = ((InetSocketAddress) session.getLocalAddress()).getPort();

        // keep your existing idle/codec setup
        session.setIdleTime(IdleStatus.BOTH_IDLE, 60);
        session.getFilterChain().addLast(
            "protocolFilter",
            new ProtocolCodecFilter(new CodecFactory())
        );

        // log the connection
        InetSocketAddress remote = (InetSocketAddress) session.getRemoteAddress();
        System.out.println("Accepted connection from "
            + remote.getAddress().getHostAddress() + ":" + remote.getPort()
            + " on local port " + localPort
        );

        if (localPort == Config.SERVER_PORT) {
            // —— LOGIN PORT HANDSHAKE (43594) ——
            // send 1 byte (0 = OK) + 8-byte session key
            ByteBuffer buf = ByteBuffer.allocate(9);
            buf.put((byte) 0);                         // login-success byte
            buf.putLong(System.currentTimeMillis());   // your session key
            buf.flip();
            session.write(buf);
            System.out.println(" → Sent login handshake (0 + session key)");
        } else {
            // —— GAME PORT HANDSHAKE (43595) ——
            // send just 1 byte (0 = OK)
            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put((byte) 0);
            buf.flip();
            session.write(buf);
            System.out.println(" → Sent game handshake (0)");
        }
    }
}