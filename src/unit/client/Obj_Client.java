package unit.client;

import java.nio.ByteBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import unit.Obj;

public abstract class Obj_Client extends Obj{
	/* ���� : Ŭ���̾�Ʈ���� ����� ����, �޼��常 ����.������ 
	 * ���� : 1.0
	 * ���� : �̿ϼ�
	 * �ۼ��� : ��ȿ��
	 * ������ ���� : 20/05/18
	 * TODO  
	 * */
	
	//�̹���
	private Image sprite;
	
	//canvas�� draw�� ����.
	public abstract void draw(GraphicsContext gc);
	
	//��Ŷ�� �޾� ������ȭ �� ����.
	public void deserialize(ByteBuffer packet) {
		
	}
	
}
