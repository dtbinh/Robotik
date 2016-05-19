import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.DifferentialPilot;

public class RegFollower {
	private LightSensor ls;
//	private BTConnection conn;
	
	private int darkColor;
	private int lightColor;
	private int targetColor;
	
	private int basespeed;
	private int breakfactor;
	
	private long startTime;
	
	private boolean reset = false;
	
	private RegFollower() {
		ls = new LightSensor(SensorPort.S4);
		darkColor = 1023;
		lightColor = 0;
		basespeed = 360;
		breakfactor = 10;
	}
	
	private void findColors() {
        DifferentialPilot tempPilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3, Motor.C, Motor.A);
        tempPilot.setRotateSpeed(72);
        tempPilot.rotate(360, true);
        RConsole.println("Starting calibration...");
        while (tempPilot.isMoving()) {
        	int val = ls.readNormalizedValue();
        	if (val < darkColor) darkColor = val;
        	if (val > lightColor) lightColor = val;
        }
        targetColor = (darkColor + lightColor) / 2;
        RConsole.println("Min value: " + darkColor + " Max value: " + lightColor + " Average value: " + targetColor);
        RConsole.println("Finding target value...");
        tempPilot.setRotateSpeed(36);
        tempPilot.rotateLeft();
        while (ls.readNormalizedValue() != targetColor);
        tempPilot.stop();
	}
	
//	private boolean waitForBluetooth() {
//		conn = Bluetooth.waitForConnection(30000, NXTConnection.PACKET);
//        return !(conn == null);
//	}
	
//	private class BluetoothThread extends Thread {
//		private boolean running = true;
//		DataInputStream in = conn.openDataInputStream();
//		
//		public void run() {
//			while (running) {
//				try {
//					double kp = in.readDouble();
//					double tn = in.readDouble();
//					double tv = in.readDouble();
//					int speed = in.readInt();
//					int brake = in.readInt();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		public void stop() {
//			running = false;
//		}
//	}
	
	private class FollowerThread extends Thread {
		private boolean running = true;
		
		public void run() {
			PIDRegulator reg = new PIDRegulator();
			reg.setKp(1);
			reg.setTn(5);
			reg.setTv(0.2);
			int regulation;
			long t = System.currentTimeMillis();
			
			long lastPrint = 0;
			
			int err;
			int val;
			// C = left motor, A = right motor
			startTime = System.currentTimeMillis();
			Motor.C.setSpeed(basespeed);
			Motor.A.setSpeed(basespeed);
			Motor.C.forward();
			Motor.A.forward();
			while (running)	{
				val = ls.readNormalizedValue();
				err = targetColor - val;
				regulation = (int) reg.calculate(targetColor, val, System.currentTimeMillis() - t, reset);
				if (System.currentTimeMillis() - lastPrint > 5) {
					lastPrint = System.currentTimeMillis();
					RConsole.println((lastPrint - startTime) + ": val: " + val + " err: " + err + " reg: " + regulation);
				}
				reset = false;
				if (regulation > 0) {
					Motor.C.setSpeed(basespeed - breakfactor * Math.abs(err));
					Motor.A.setSpeed(basespeed - breakfactor * Math.abs(err) - regulation);
				}
				else {
					Motor.C.setSpeed(basespeed - breakfactor * Math.abs(err) + regulation);
					Motor.A.setSpeed(basespeed - breakfactor * Math.abs(err));
				}
				t = System.currentTimeMillis();
			}
			Motor.C.stop();
			Motor.A.stop();
		}
		
		public void stop() {
			this.running = false;
		}
	}
	
	private void followLine() {
		FollowerThread follower = new FollowerThread();
//		BluetoothThread bluetooth = new BluetoothThread();
		follower.start();
//		bluetooth.start();
		Button.ESCAPE.waitForPress();
		follower.stop();
//		bluetooth.stop();
		try {
			follower.join();
//			bluetooth.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void runMain() {
		findColors();
//		if (waitForBluetooth()) {
//			RConsole.println("Bluetooth device found: " + conn.getAddress());
//		}
//		else {
//			RConsole.println("No bluetooth device connected");
//		}
		followLine();
	}
	
	public static void main(String[] args) {
		RConsole.openUSB(0);
		RegFollower rf = new RegFollower();
		rf.runMain();
		RConsole.close();
	}
}
