package net.sf.juoserver.networking.mina;

import lombok.extern.slf4j.Slf4j;
import net.sf.juoserver.api.Message;
import net.sf.juoserver.api.MessageReader;
import net.sf.juoserver.protocol.UOProtocolMessageReader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UOProtocolDecoder extends ProtocolDecoderAdapter {
	private final Map<Long, MessageReader> messageReaders = new HashMap<Long, MessageReader>();
	
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		if (!messageReaders.containsKey(session.getId())) {
			messageReaders.put(session.getId(), new UOProtocolMessageReader());
		}
		byte[] buffer = consumeEntireIoBuffer(in);
		List<Message> messages = messageReaders.get(session.getId()).readMessages(buffer);
		for (Message message : messages) {
			out.write(message);
		}
	}
	
	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		log.debug("Recreating reader due to closed connection, expecting seed next.");
		messageReaders.remove(session.getId());
	}

	private byte[] consumeEntireIoBuffer(IoBuffer in) {
		byte[] buffer = new byte[in.limit()];
		in.get(buffer);
		return buffer;
	}
}