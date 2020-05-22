package test;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Main {
	public static void main(String[] args) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("dd");
				try {
					Thread.sleep(2000);
					System.out.println("dd");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		//timer.scheduleAtFixedRate(task, 0, 100);
		timer.schedule(task, 0, 100);
	}
}
