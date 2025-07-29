package server.net;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import server.util.ISAACRandomGen;

/**
 * Provides access to the encoders and decoders for the 508 protocol.
 * @author Graham
 */
public class GameCodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder = new RS2ProtocolEncoder();
    private final ProtocolDecoder decoder;

    public GameCodecFactory(ISAACRandomGen inC) {
        this.decoder = new RS2ProtocolDecoder(inC);
    }

    @Override
    public ProtocolEncoder getEncoder() {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder() {
        return decoder;
    }
}
