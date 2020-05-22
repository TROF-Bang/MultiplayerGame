package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class UdpServer {
	static DatagramChannel dataChannel;
	static DatagramChannel serverChannel;
	static Vector<Client> clients;
	static final long INTERVAL = 33;
	static final int SERVER_CONNECT_PORT = 7777;
	static final int ClIENT_RECEIVING_PORT = 5001;
	static final String SERVER_ADDRESS = "192.168.56.1";
	static int lastId = 0;

	public static void initialize() {
		try {
			dataChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			dataChannel.bind(new InetSocketAddress(ClIENT_RECEIVING_PORT));
			serverChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			serverChannel.bind(new InetSocketAddress(SERVER_CONNECT_PORT));
			clients = new Vector<Client>();
		} catch (IOException e) {
			System.out.println("ä�� ���� ����");
		}
	}

	public static void main(String[] args) throws Exception {
		initialize();
		startServer();
	}

	public static void startServer() {
		// ��� Client�鿡�� ������������ ������Ʈ �� ������ send
		System.out.println("startServer");
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
					try {
						Iterator<Client> iterator1 = clients.iterator();
						while(iterator1.hasNext()) {
							Client srcClient = iterator1.next();
							srcClient.update();
							ByteBuffer buffer = ByteBuffer.allocate(100);
							buffer = srcClient.serialize();
							for (Client desClient : clients) {
								try {
									desClient.send(buffer);
									buffer.flip();
									System.out.println(clients.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
					}
				}
		};
		timer.schedule(task, 0, INTERVAL);

		//data�� �޾� Client�鿡�� �й��ϴ� ������.
		Runnable runnable1 = new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						ByteBuffer buffer = ByteBuffer.allocate(100);
						dataChannel.receive(buffer);
						buffer.flip();
						int id = buffer.getInt();
						char cmd = buffer.getChar();
						
						Iterator<Client> iterator = clients.iterator();
						while(iterator.hasNext()) {
							Client client = iterator.next();
							if (client.id == id) {
								client.packet.clear();
								client.packet.putChar(cmd);
								client.packet.flip();
							}
						}
					}
				} catch (Exception e) {
				}
			}
		};
		Thread thread1 = new Thread(runnable1);
		thread1.setDaemon(true);
		thread1.start();
		
		// ���� ��Ʈ�θ� ����, ���� ��û ����
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						ByteBuffer buffer = ByteBuffer.allocate(100);
						SocketAddress address = serverChannel.receive(buffer);
						buffer.flip();
						
						StringTokenizer st = new StringTokenizer(address.toString(), "/:");
						String addr= st.nextToken();
						String port = st.nextToken();
						System.out.println(addr);
						System.out.println(port);
						
						//id ���� ������
						int id = ++lastId;
						buffer.clear();
						buffer.putInt(id);
						buffer.flip();
						serverChannel.send(buffer, address);
						//���� �޴´�.
						serverChannel.receive(buffer);
						
						Client client = new Client(new InetSocketAddress(addr,Integer.parseInt(port)),
								id);
						
						clients.add(client);
						System.out.println("�����û ����");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.setName("�������");
		thread.setDaemon(true);
		thread.start();
	}

	static class Client {
		//���� ip
		private InetSocketAddress address;
		//������Ŷ(Ŀ�ǵ�)
		private ByteBuffer packet;
		private int id;
		private int x = 50;
		private int y = 50;

		public Client(InetSocketAddress address, int id) {
			try {
				
				this.id = id;
				this.address = address;
				this.packet = ByteBuffer.allocate(100);
				
				System.out.println("Client����!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public ByteBuffer serialize() {
			ByteBuffer buffer = ByteBuffer.allocate(100);
			buffer.putInt(id);
			buffer.putInt(x);
			buffer.putInt(y);
			buffer.flip();
			return buffer;
		}

		public void send(ByteBuffer buffer) {
			try {
				dataChannel.send(buffer, address);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void update() {
			// ���� ��Ŷ : ������ �Է�Ű
				int key = packet.getChar();
				packet.flip();
				switch (key) {
				case 'W':
					this.y -= 5;
					break;
				case 'A':
					this.x -= 5;
					break;
				case 'S':
					this.y += 5;
					break;
				case 'D':
					this.x += 5;
					break;
				default:
					break;
				}
		}


		// id�� ������ ���� ��ü��.
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Client) {
				Client client = (Client) obj;
				return (client.id == this.id);
			} else {
				return false;
			}
		}

	}

}
