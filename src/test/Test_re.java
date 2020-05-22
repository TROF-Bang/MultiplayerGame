package test;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Test_re {

	@SuppressWarnings({ "unused", "null" })
	public static void main(String[] args) throws Exception {
		SocketChannel socketChannel = null;
		ByteBuffer data = null;
		//we open the channel and connect
		socketChannel.read(data);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		Message message = (Message)objectInputStream.readObject();
	}
}
