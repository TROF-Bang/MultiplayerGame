package unit.client;

import java.nio.ByteBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import unit.Obj;

public abstract class Obj_Client extends Obj{
	/* 설명 : 클라이언트에서 사용할 정보, 메서드만 모음.랜더링 
	 * 버전 : 1.0
	 * 상태 : 미완성
	 * 작성자 : 신효재
	 * 마지막 수정 : 20/05/18
	 * TODO  
	 * */
	
	//이미지
	private Image sprite;
	
	//canvas에 draw할 내용.
	public abstract void draw(GraphicsContext gc);
	
	//패킷을 받아 역직렬화 후 적용.
	public void deserialize(ByteBuffer packet) {
		
	}
	
}
