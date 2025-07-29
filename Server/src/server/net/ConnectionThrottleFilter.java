package server.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

/**
 * A {@link IoFilter} which blocks connections from connecting
 * at a rate faster than the specified interval.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class ConnectionThrottleFilter extends IoFilterAdapter {

    private long allowedInterval;
    private final Map<InetAddress, Long> clients;
    private final Map<InetAddress, Integer> counts;
    private final Set<InetAddress> connectedAddresses;

    /**
     * Constructor that takes in a specified wait time.
     *
     * @param allowedInterval The number of milliseconds a client must wait
     *                       before making another successful connection.
     */
    public ConnectionThrottleFilter(long allowedInterval) {
        this.allowedInterval = allowedInterval;
        this.clients = Collections.synchronizedMap(new HashMap<>());
        this.counts = Collections.synchronizedMap(new HashMap<>());
        this.connectedAddresses = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Sets the interval between connections from a client, in milliseconds.
     */
    public void setAllowedInterval(long allowedInterval) {
        this.allowedInterval = allowedInterval;
    }

    public void delayClient(IoSession session, int delayMillis) {
        long delayedTime = System.currentTimeMillis() - delayMillis;
        clients.put(getAddress(session), delayedTime);
    }

    private InetAddress getAddress(IoSession session) {
        return ((InetSocketAddress) session.getRemoteAddress()).getAddress();
    }

    /**
     * Determines if a connection is allowed based on the client's address and last connection time.
     */
    public boolean isConnectionOk(IoSession session) {
        InetAddress addr = getAddress(session);
        long now = System.currentTimeMillis();
        long lastConnTime = clients.getOrDefault(addr, 0L);

        if (lastConnTime != 0L && (now - lastConnTime < allowedInterval)) {
            int c = counts.getOrDefault(addr, 0) + 1;
            if (c >= 350) {
                c = 0; // Optional: reset or handle excessive attempts
            }
            counts.put(addr, c);
            // Optionally log: connection attempt dropped
            return false;
        } else {
            clients.put(addr, now);
            return true;
        }
    }

    public void closedSession(IoSession session) {
        connectedAddresses.remove(getAddress(session));
    }

    public void acceptedLogin(IoSession session) {
        connectedAddresses.add(getAddress(session));
    }

    public boolean isConnected(IoSession session) {
        return connectedAddresses.contains(getAddress(session));
    }

    public int[] getSizes() {
        return new int[] { clients.size(), counts.size(), connectedAddresses.size() };
    }

    public void connectionOk(IoSession session) {
        counts.remove(getAddress(session));
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
        if (!isConnectionOk(session)) {
            session.close();
            return;
        }
        nextFilter.sessionCreated(session);
    }
}
