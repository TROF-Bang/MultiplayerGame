package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Single_Server {

	ExecutorService executorService;
	ServerSocket serverSocket;
	private ArrayList<UserManager> connectedClients;
	
	public static void main(String[] args) throws IOException {
		new Single_Server();
		System.out.println(1);
	}
	
	public Single_Server() throws IOException {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress("localhost",7777));
		executorService = Executors.newFixedThreadPool(30);
		startServer();
		System.out.println(3);
	}
	
	public void startServer() throws IOException {
		System.out.println(2);
//			manager = new RoomManager();


//			if (!serverSocket.isClosed()) {
//				stopServer();
//				return;
//			}
			
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while(true) {
							Socket socket = serverSocket.accept();
							System.out.println("[연결 수락: " + socket.getRemoteSocketAddress()+ ": " + Thread.currentThread().getName() + "]");						
							UserManager client = new UserManager(socket);
							connectedClients.add(client);
						}
					} catch (Exception e) {
						if (!serverSocket.isClosed()) {
							stopServer();
							return;
						}
					}
				}
			};
			executorService.submit(runnable);
		}

	public class UserManager {
		
		public Socket curClient;
		
		public PrintWriter out;
		
		public BufferedReader in;
		
		ClientInfoSeirialized info;
		
		public UserManager(Socket socket) {
			this.curClient = socket;
			info = new ClientInfoSeirialized();
			System.out.println("UserManager 생성");
			receive();
		}

		public void receive() {
			System.out.println("executorservice 시작");
			Runnable runnable = new Runnable() {
	
				@SuppressWarnings("unlikely-arg-type")
				@Override
				public void run() {

					try {
						out = new PrintWriter(curClient.getOutputStream(), true);						
						in = new BufferedReader(new InputStreamReader(curClient.getInputStream()));						
						info = (ClientInfoSeirialized) SerializeDeserialize.fromString(in.readLine());
						
						String data = SerializeDeserialize.toString(info);
						out.println(data);
						
						String serializedFromClient;
						while ((serializedFromClient = in.readLine()) != null) {
							System.out.println("while 시작");
							info = (ClientInfoSeirialized) SerializeDeserialize.fromString(serializedFromClient);
							System.out.println("info 정보 : " + info);						
							
							data = SerializeDeserialize.toString(info);
							out.println(data);

							System.out.println("while 끝");
						}	
					} catch (Exception e) {}
					finally {
						synchronized (connectedClients) {
						connectedClients.remove(this);
						}
						
						System.out.println("closeConnection");
						closeConnection();
					}
	
				}

				private void closeConnection() {
					 try {
							if (out != null) {
								out.close();
							}
							if (in != null) {
								in.close();
							}
							if (curClient != null) {
								curClient.close();
							} 
						} catch (Exception e) {}				
				}					
			};
			
				executorService.submit(runnable);
				System.out.println("executorservice 끝");
			}
		
		
	}
	
	private void stopServer() {
		try {
			Iterator<UserManager> iterator = connectedClients.iterator();
			while(iterator.hasNext()) {
				UserManager client = iterator.next();
				client.curClient.close();
				connectedClients.remove(client);
			}
			
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
		} catch (Exception e) {	}		
	}
}