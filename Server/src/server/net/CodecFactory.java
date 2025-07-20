package server.net;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Provides access to the encoders and decoders for the 508 protocol.
 * @author Graham
 */
public class CodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder = new RS2ProtocolEncoder();
    private final ProtocolDecoder decoder = new RS2LoginProtocolDecoder();

    @Override
    public ProtocolEncoder getEncoder() {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder() {
        return decoder;
    }
}
