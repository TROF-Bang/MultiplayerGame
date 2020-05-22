package unit.server;

import javafx.geometry.Bounds;
import unit.Obj;

public abstract class Obj_Server extends Obj{
	/* 설명 : 서버에서 사용할 정보, 메서드만 모음, 게임로직관련 계산 
	 * 버전 : 1.0
	 * 상태 : 미완성
	 * 작성자 : 신효재
	 * 마지막 작성일 : 20/05/18
	 * TODO  
	 * */
	
	//충돌마스크
	private Bounds bounds;
	
	//한 라운드의 실행을 정의.
	public void round() {
		step();
	}
	
	//매 라운드마다 실행되는 것. 오버라이드하여 작성하자.
	public void step() {
		
	}
	
	//직렬화
	public void serialize() {
		
	}
	
	//충돌 판정 메서드
	public boolean collisionCheck(Obj_Server obj) {
		return obj.bounds.intersects(this.bounds);
	}
	
}
