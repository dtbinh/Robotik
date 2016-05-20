import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.DifferentialPilot;

public class RegFollower {
	private LightSensor ls;
	private BTConnection conn;
	
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
		basespeed = 0;
		breakfactor = 0;
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
        while (ls.readNormalizedValue() > targetColor);
        tempPilot.stop();
	}
	
	private boolean waitForBluetooth() {
		conn = Bluetooth.waitForConnection(0, NXTConnection.PACKET);
        return !(conn == null);
	}
	
	private class BluetoothThread extends Thread {
		private boolean running = true;
		byte[] in = new byte[8];
		
		public void run() {
			while (running) {
				conn.read(in, 8);
				double kp = Double.parseDouble(new String(in));
				conn.read(in, 8);
				double tv = Double.parseDouble(new String(in));
				conn.read(in, 8);
				double tn = Double.parseDouble(new String(in));
				conn.read(in, 8);
				int speed = Integer.parseInt(new String(in));
				conn.read(in, 8);
				int brake = Integer.parseInt(new String(in));
				RConsole.println(kp + " " + tv + " " + tn + " " + speed + " " + brake);
			}
		}
		
		public void stop() {
			running = false;
		}
	}
	
	private class FollowerThread extends Thread {
		private boolean running = true;
		
		public void run() {
			PIDRegulator reg = new PIDRegulator();
			reg.setKp(0);
			reg.setTn(0);
			reg.setTv(0);
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
				regulation = (int) reg.calculate(targetColor, val, (System.currentTimeMillis() - t) / 1000., reset);
				try {
					Thread.sleep(2);
				}
				catch (Exception e) {
					
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
//				if (System.currentTimeMillis() - lastPrint > 200) {
//					lastPrint = t;
//					RConsole.println((lastPrint - startTime) + ": val: " + val + " err: " + err + " reg: " + regulation + " A: " + Motor.A.getSpeed() + " B: " + Motor.B.getSpeed());
//				}
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
		BluetoothThread bluetooth = new BluetoothThread();
		follower.start();
		bluetooth.start();
		Button.ESCAPE.waitForPress();
		follower.stop();
		bluetooth.stop();
		try {
			follower.join();
			bluetooth.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void runMain() {
		findColors();
		if (waitForBluetooth()) {
			RConsole.println("Bluetooth device found: " + conn.getAddress());
		}
		else {
			RConsole.println("No bluetooth device connected");
		}
		followLine();
	}
	
	public static void main(String[] args) {
		RConsole.openUSB(0);
		RegFollower rf = new RegFollower();
		rf.runMain();
		RConsole.close();
	}
}
