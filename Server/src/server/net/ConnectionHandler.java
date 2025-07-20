package server.net;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import server.model.players.Client;

public class ConnectionHandler implements IoHandler {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        // Optionally, log exceptions here if needed
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (session.getAttachment() != null) {
            Client plr = (Client) session.getAttachment();
            plr.queueMessage((Packet) message);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // Can be used for logging or message tracking if needed
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
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        session.close();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.setIdleTime(IdleStatus.BOTH_IDLE, 60);
        session.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(new CodecFactory()));
    }
}
