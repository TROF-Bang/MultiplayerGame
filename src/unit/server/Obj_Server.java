package unit.server;

import javafx.geometry.Bounds;
import unit.Obj;

public abstract class Obj_Server extends Obj{
	/* ���� : �������� ����� ����, �޼��常 ����, ���ӷ������� ��� 
	 * ���� : 1.0
	 * ���� : �̿ϼ�
	 * �ۼ��� : ��ȿ��
	 * ������ �ۼ��� : 20/05/18
	 * TODO  
	 * */
	
	//�浹����ũ
	private Bounds bounds;
	
	//�� ������ ������ ����.
	public void round() {
		step();
	}
	
	//�� ���帶�� ����Ǵ� ��. �������̵��Ͽ� �ۼ�����.
	public void step() {
		
	}
	
	//����ȭ
	public void serialize() {
		
	}
	
	//�浹 ���� �޼���
	public boolean collisionCheck(Obj_Server obj) {
		return obj.bounds.intersects(this.bounds);
	}
	
}
