package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Single extends Application implements Initializable{
	
	static LinkedList<String> commends = new LinkedList<String>(); 
	//static Vector<Obj> objects = new Vector<Obj>();
	static ConcurrentLinkedQueue<Obj> objects = new ConcurrentLinkedQueue<>();
	static GraphicsContext gc;
	static MouseEvent me;
	Socket socket;
	PrintWriter out = null;
	BufferedReader in = null;
	ClientInfoSeirialized info; 
	Vector<Player> Players;
	
	@FXML
	Canvas canvas;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//그래픽스 얻기
		gc = canvas.getGraphicsContext2D();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("single.fxml"));
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		try {
			socket = new Socket("localhost",7777);	
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			info = new ClientInfoSeirialized();
			info.id = "" + (Math.random() * 10000);
			String serializedUser = SerializeDeserialize.toString(info);
			out.println(serializedUser);
			
			
		} catch (Exception e) {
			if (!socket.isClosed()) {
				stopClient();
			}
			return;
		}		
		
		//mouse 클릭
//		scene.setOnMouseClicked(e->{
//			info.me = e;
//			send();
//		});
		
		//키입력
		scene.setOnKeyPressed(e->{
			String key = e.getCode().getName();
			if (info.commends.contains(key)) {
				info.commends.remove(key);
				send();
			}
			if (key.equals("W")) {
				info.commends.addFirst(key);
				send();
			}
			else {
				info.commends.add(key);
				send();
			}
//			System.out.println(commends);
		});
		
		scene.setOnKeyReleased(e->{
			String key = e.getCode().getName();
			if (info.commends.contains(key)) {
				info.commends.remove(key);
				send();
			}
//			System.out.println(commends);
		});
		
		//오브젝트 생성, 배치
		Block block1 = new Block(0, 275, 25, 25);
		Block block2 = new Block(25, 275, 25, 25);
		Block block3 = new Block(50, 275, 25, 25);
		Block block4 = new Block(75, 275, 25, 25);
		Block block5 = new Block(100, 275, 25, 25);
		Block block6 = new Block(125, 275, 25, 25);
		Block block7 = new Block(150, 275, 25, 25);
		Block block8 = new Block(175, 275, 25, 25);
		Block block9 = new Block(200, 275, 25, 25);
		Block block10 = new Block(225, 275, 25, 25);
		Block block11 = new Block(250, 275, 25, 25);
		Block block12 = new Block(275, 275, 25, 25);
		Block block13 = new Block(300, 275, 25, 25);
		Block block14 = new Block(325, 275, 25, 25);
		Block block15 = new Block(350, 275, 25, 25);
		Block block16 = new Block(375, 275, 25, 25);
		Block block17 = new Block(400, 275, 25, 25);
		Block block18 = new Block(425, 275, 25, 25);
		Block block19 = new Block(450, 275, 25, 25);
		Block block20 = new Block(475, 275, 25, 25);
		
		Block block = new Block(100, 200, 25, 25);
		Dummy dummy1 = new Dummy(350, 100, 50, 50);
		Dummy dummy2 = new Dummy(150, 100, 50, 50);
		
		Player player = new Player(50, 50, 25, 25);
		
		//랜더링
		AnimationTimer animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				gc.clearRect(0, 0, 500, 300);
				for (Obj obj : objects) {
					obj.draw(gc);
				}
			}
		};
		animationTimer.start();
		
		//로직(게임엔진)
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Iterator<Obj> iterator = objects.iterator();
				while (iterator.hasNext()) {
					Obj obj = iterator.next();
					obj.step();
				}
			}
		};
		timer.schedule(task, 0, 33);
		
		receive();
	}
	
	private void receive() {
		Thread thread = new Thread(()->{
			try {
				String line;
				while((line = in.readLine()) != null) {
					info = (ClientInfoSeirialized) SerializeDeserialize.fromString(line);
//					roomList = info.roomList;
//					System.out.println(info);
//					byte[] bytes = new byte[100];
//					InputStream is = socket.getInputStream();
//					
//					int readCount = is.read(bytes);
//					
//					if (readCount == -1) {
//						throw new IOException();
//					}
//					String data = new String(bytes, 0, readCount, "UTF-8");
//					String data = line;
					me = info.me;
					commends = info.commends;					
				}
			} catch (Exception e) {
				if (!socket.isClosed()) {
					stopClient();
				}
			}
		});
		thread.start();
		
	}

	public void send() {
		Thread thread = new Thread(()-> {
			try {
//				OutputStream os = socket.getOutputStream();
//				byte[] bytes = data.getBytes("UTF-8");
//				os.write(bytes);
//				os.flush();
//				info.msg = txtInput.getText();
				String serializedUser = SerializeDeserialize.toString(info);				
				out.println(serializedUser);
			} catch (Exception e) {
				if (!socket.isClosed()) {
					stopClient();
				}
			}
		});
		thread.start();
	}
	
	private void stopClient() {
		try {
			if(socket!=null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {}		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	//모든 오브젝트 조상클래스
	class Obj extends Rectangle{
		//생성되며 자신을 등록.
		public Obj(double minX, double minY, double width, double height) {
			super(minX, minY, width, height);
			objects.add(this);
		}
		
		//매 라운드 실행될 메서드
		public void step() {
			
		}
		
		//마우스클릭 이벤트
		public void mouseClicked() {
			
		}
		
		//자신을 그리는 메서드
		public void draw(GraphicsContext gc) {
			gc.fillRect(getX(), getY(), getWidth(), getHeight());
		}
	}
	
	//발판
	class Block extends Obj{

		public Block(double minX, double minY, double width, double height) {
			super(minX, minY, width, height);
		}
		
		@Override
		public void draw(GraphicsContext gc) {
			gc.setFill(Color.BLACK);
			super.draw(gc);
		}
	}

	//총알
	class Bullet extends Obj{

		double speed = 20;
		double xSpeed;
		double ySpeed;
		double angle;
		
		public Bullet(double minX, double minY, double width, double height, double angle) {
			super(minX, minY, width, height);
			this.angle = angle;
			this.xSpeed = Math.cos(angle) * speed; 
			this.ySpeed = Math.sin(angle) * speed;
		}
		
		@Override
		public void step() {
			roomOut();
			setX(getX() + xSpeed);
			setY(getY() + ySpeed);
		}
		
		public void delete() {
			System.out.println("총알 파괴");
			objects.remove(this);
		}
		  
		@Override
		public void draw(GraphicsContext gc) {
			//gc.setFill(Color.YELLOW);
			gc.setFill(Color.RED);
			//gc.rotate(angle);
			super.draw(gc);
		}
		
		public void roomOut() {
			if (getX() < 0 || getX() > 500 || getY() < 0 || getY() > 300) {
				delete();
			}
		}
	}
	
	//플레이어
	   class Player extends Obj{

		      double gravity = 1;
		      double xSpeed = 5;
		      double ySpeed = 0;
		      boolean canJump = false;
		      Image sprite;
		      
		      public Player(double minX, double minY, double width, double height) {
		         super(minX, minY, width, height);
		         //사진 확인
		         sprite = new Image(getClass().getResource("player.png").toString());
		         System.out.println(sprite);
		      }
		      
		      @Override
		      public void mouseClicked() {
		         if (me != null && !me.isConsumed()) {
		            System.out.println("consume");
		            me.consume();
		            double angle = Math.atan2(me.getY()-(getY()+12.5), me.getX()-(getX()+12.5));
		            new Bullet(getX()+12.5, getY()+12.5, 10, 10, angle);
		         }
		      }
		      
		      @Override
		      public void step() {
		         mouseClicked();
		         this.ySpeed += this.gravity;
		         this.setY(getY() + ySpeed);
		         for (Obj obj : objects) {
		            if (obj instanceof Block) {
		               Block block = (Block) obj;
		               if (block.intersects(new Line(getX(), getY()+25, getX()+xSpeed, getY()+25+ySpeed).getBoundsInLocal())) {
		                  this.canJump = true;
		                  this.ySpeed = 0;
		                  setY(block.getY()-25);
		               }
		            }
		         }
		         if (commends.contains("W")) {
		            if (canJump) {
		               canJump = !canJump;
		               this.ySpeed = -15;
		            }
		         }
		         String key = commends.peekLast();
		         if (key != null) {
		            switch (key) {
		            case "A":
		               setX(getX()-xSpeed);
		               break;
		            case "D":
		               setX(getX()+xSpeed);
		               break;
		            default:
		               break;
		            }
		         }
		      }
		      
		      @Override
		      public void draw(GraphicsContext gc) {
		         gc.setFill(Color.BLUE);
		         gc.drawImage(sprite, getX(), getY());
		         //super.draw(gc);
		      }
		   }
	
	//더미
	class Dummy extends Obj{
		int hp = 100;
		
		public Dummy(double minX, double minY, double width, double height) {
			super(minX, minY, width, height);
		}
		
		@Override
		public void draw(GraphicsContext gc) {
			gc.setFill(Color.PURPLE);
			super.draw(gc);
			gc.setFill(Color.RED);
			gc.fillRect(getX(), getY() - 25, getWidth()*hp/100, 15);
			gc.setFill(Color.BLACK);
			gc.strokeRect(getX(), getY()-25, getWidth(), 15);
		}
		
		@Override
		public void step() {
			super.step();
			onCollision();
			if (hp < 0) {
				delete();
			}
		}
		
		public void delete() {
			objects.remove(this);
			System.out.println("더미 파괴");
		}
		
		public void onCollision() {
			for (Obj obj : objects) {
				if (obj instanceof Bullet) {
					Bullet bullet = (Bullet) obj;
					if (bullet.intersects(getBoundsInLocal())) {
						bullet.delete();
						this.hp -= 10;
					}
				}
			}
		}
		
	}
	
}
