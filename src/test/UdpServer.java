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
			System.out.println("채널 오픈 실패");
		}
	}

	public static void main(String[] args) throws Exception {
		initialize();
		startServer();
	}

	public static void startServer() {
		// 모든 Client들에게 일정간격으로 업데이트 후 데이터 send
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

		//data를 받아 Client들에게 분배하는 스레드.
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
		
		// 서버 포트로만 받음, 연결 요청 수락
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
						
						//id 정보 보내기
						int id = ++lastId;
						buffer.clear();
						buffer.putInt(id);
						buffer.flip();
						serverChannel.send(buffer, address);
						//응답 받는다.
						serverChannel.receive(buffer);
						
						Client client = new Client(new InetSocketAddress(addr,Integer.parseInt(port)),
								id);
						
						clients.add(client);
						System.out.println("연결요청 수락");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.setName("연결수락");
		thread.setDaemon(true);
		thread.start();
	}

	static class Client {
		//상대방 ip
		private InetSocketAddress address;
		//수신패킷(커맨드)
		private ByteBuffer packet;
		private int id;
		private int x = 50;
		private int y = 50;

		public Client(InetSocketAddress address, int id) {
			try {
				
				this.id = id;
				this.address = address;
				this.packet = ByteBuffer.allocate(100);
				
				System.out.println("Client생성!");
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
			// 수신 패킷 : 마지막 입력키
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


		// id가 같으면 같은 객체다.
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
