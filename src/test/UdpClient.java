package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class UdpClient extends Application implements Initializable{
	DatagramChannel channel;
	LinkedList<String> commends;
	Vector<Player> players;
	static final int SERVER_CONNECT_PORT = 7777;
	static final int ClIENT_RECEIVING_PORT = 5001;
	static final String SERVER_ADDRESS = "192.168.56.1";
	static int id;
	
	@FXML Canvas canvas;
	static GraphicsContext gc;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = canvas.getGraphicsContext2D();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//화면 출력
		Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		this.commends = new LinkedList<String>();
		this.players = new Vector<Player>();
		
		//키 입력
		scene.setOnKeyPressed(e->{
			String input = e.getCode().getName();
			if (!commends.contains(input)) {
				commends.push(input);
				System.out.println(commends.peek());
				ByteBuffer buffer = ByteBuffer.allocate(100);
				buffer.putInt(id);
				buffer.putChar(commends.peek().charAt(0));
				buffer.flip();
				if (channel != null && channel.isOpen()) {
					try {
						channel.send(buffer, new InetSocketAddress(SERVER_ADDRESS ,ClIENT_RECEIVING_PORT));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		scene.setOnKeyReleased(e->{
			String input = e.getCode().getName();
			if (commends.contains(input)) {
				commends.remove(input);
			}
			System.out.println(commends.peek());
			ByteBuffer buffer = ByteBuffer.allocate(100);
			buffer.putInt(id);
			String cmd = commends.peek();
			if (cmd != null) {
				buffer.putChar(commends.peek().charAt(0));
			}
			else {
				buffer.putChar('N');
			}
			buffer.flip();
			if (channel != null && channel.isOpen()) {
				try {
					channel.send(buffer, new InetSocketAddress(SERVER_ADDRESS ,ClIENT_RECEIVING_PORT));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		//화면 드로우
		AnimationTimer animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				gc.clearRect(0, 0, 500, 300);
				for (Player player : players) {
					player.draw(gc);
				}
			}
		};
		animationTimer.start();
		
		startClient();
	}
	
	//수신 패킷 : id, x, y
	//발신 패킷 : 마지막 커맨드 
	
	public void startClient() {
		try {
			this.channel = DatagramChannel.open(StandardProtocolFamily.INET);
			ByteBuffer buffer = ByteBuffer.allocate(100);
			buffer.putInt((byte)1);
			buffer.flip();
			//connect 포트로 요청
			//7777로 보내지만, 지금 현재 채널의 포트는 다르다. bind 안했으니 port는 랜덤이다.
			channel.send(buffer, new InetSocketAddress(SERVER_ADDRESS ,SERVER_CONNECT_PORT));
			
			//id 받기
			buffer.clear();
			channel.receive(buffer);
			buffer.flip();
			id = buffer.getInt();
			
			//응답.
			buffer.clear();
			buffer.put((byte)1);
			buffer.flip();
			channel.send(buffer, new InetSocketAddress(SERVER_ADDRESS ,SERVER_CONNECT_PORT));
			
			receive();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void receive() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						ByteBuffer buffer = ByteBuffer.allocate(100);
						//블로킹됨
						channel.receive(buffer);
						buffer.flip();
						System.out.println(buffer.remaining());
						int id = buffer.getInt();
						int x = buffer.getInt();
						int y = buffer.getInt();
						System.out.println("receive" + id + " "+ x +" " + y + " " + players.size());
						
						//id에 해당하는 player 찾고, 없으면 만든다.
						Player trgPlayer = null;
						for (Player player : players) {
							if (id == player.id) {
								trgPlayer = player;
							}
						}
						if (trgPlayer == null) {
							trgPlayer = new Player(id);
							players.add(trgPlayer);
						}
						trgPlayer.x = x;
						trgPlayer.y = y;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	//Player 클래스
	class Player{
		int id;
		int x;
		int y;
		
		public Player(int id) {
			this.id = id;
		}
		
		public void draw(GraphicsContext gc) {
			gc.fillOval(x, y, 10, 10);
			gc.fillText("" + id, x, y - 5);
		}
	}
}
