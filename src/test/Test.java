package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Test {

	@SuppressWarnings("null")
	public static void main(String[] args) throws IOException {
		Message outgoingMessage = null;
		SocketChannel socketChannel = null;
		//we open the channel and connect
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(outgoingMessage);
		objectOutputStream.flush();
		socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));

	}
}

class Message {
	int i;
}