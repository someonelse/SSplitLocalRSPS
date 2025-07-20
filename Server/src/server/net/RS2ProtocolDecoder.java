package server.net;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import server.model.players.Client;
import server.util.ISAACRandomGen;

public class RS2ProtocolDecoder extends CumulativeProtocolDecoder {

    private final ISAACRandomGen isaac;

    /**
     * Only CodecFactory can initialize us.
     */
    protected RS2ProtocolDecoder(ISAACRandomGen isaac) {
        this.isaac = isaac;
    }

    /**
     * Decodes an incoming message.
     */
    @Override
    protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
        synchronized (session) {
            int opcode = (Integer) session.getAttribute("opcode");
            int size = (Integer) session.getAttribute("size");

            // Read opcode if needed
            if (opcode == -1) {
                if (in.remaining() >= 1) {
                    opcode = in.get() & 0xFF;
                    opcode = (opcode - isaac.getNextKey()) & 0xFF;
                    size = Client.PACKET_SIZES[opcode];
                    session.setAttribute("opcode", opcode);
                    session.setAttribute("size", size);
                } else {
                    // Wait for more data
                    return false;
                }
            }

            // Read size if variable-length packet
            if (size == -1) {
                if (in.remaining() >= 1) {
                    size = in.get() & 0xFF;
                    session.setAttribute("size", size);
                } else {
                    // Wait for more data
                    return false;
                }
            }

            // Read packet payload/data
            if (in.remaining() >= size) {
                byte[] data = new byte[size];
                in.get(data);
                out.write(new Packet(session, opcode, data));
                // Reset opcode and size for the next packet
                session.setAttribute("opcode", -1);
                session.setAttribute("size", -1);
                return true; // Ready to decode another packet
            }

            // Not enough data yet
            return false;
        }
    }

    /**
     * Releases resources used by this decoder.
     */
    @Override
    public void dispose(IoSession session) throws Exception {
        super.dispose(session);
    }
}
