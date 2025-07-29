package server.net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.IoSession;

import server.Config;
import server.Connection;

/**
 * Keeps track of per-host connections for session limiting.
 */
public class HostList {

    private static final HostList INSTANCE = new HostList();

    public static HostList getHostList() {
        return INSTANCE;
    }

    private final Map<String, Integer> connections = new HashMap<>();

    /**
     * Adds a session's remote host to the connection count.
     * @param session The IoSession to track
     * @return true if the host is allowed to connect, false if blocked/banned/limited
     */
    public synchronized boolean add(IoSession session) {
        String addr = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
        int amt = connections.getOrDefault(addr, 0) + 1;

        // "1337" check: some sort of 'special' lockout?
        if (amt == 1337) {
            return false;
        }

        if (amt > Config.IPS_ALLOWED || Connection.isIpBanned(addr)) {
            return false;
        }

        connections.put(addr, amt);
        return true;
    }

    /**
     * Removes a session's host from the connection count.
     * @param session The IoSession to remove
     */
    public synchronized void remove(IoSession session) {
        if (session.getAttribute("inList") != Boolean.TRUE) {
            return;
        }
        String addr = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
        Integer amt = connections.get(addr);
        if (amt == null) {
            return;
        }
        amt--;
        if (amt <= 0) {
            connections.remove(addr);
        } else {
            connections.put(addr, amt);
        }
    }
}
