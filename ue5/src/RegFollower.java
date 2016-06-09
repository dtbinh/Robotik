import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.DifferentialPilot;

public class RegFollower {
	private LightSensor ls;
	private UltrasonicSensor us;
	private BTConnection conn;
	
	private int darkColor;
	private int lightColor;
	private int targetColor;
	
	private int basespeed;
	private int breakfactor;
	
	private long startTime;
	
	private boolean reset = false;
	private PIDRegulator reg = new PIDRegulator(0, 0, 0);
	
	private RegFollower() {
		ls = new LightSensor(SensorPort.S1);
		us = new UltrasonicSensor(SensorPort.S4);
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
		private byte[] in = new byte[10];
		private int len;
		
		public void run() {
			while (running && len >= 0) {
				double val;
				len = conn.read(in, 10);
				String inp = new String(in);
				val = Double.parseDouble(inp.substring(2));
				if (inp.startsWith("kp")) {
					reg.setKp(val);
					RConsole.println("Received Kp=" + val);
				}
				else if (inp.startsWith("tv")) {
					reg.setTv(val);
					RConsole.println("Received Tv=" + val);
				}
				else if (inp.startsWith("tn")) {
					reg.setTn(val);
					RConsole.println("Received Tn=" + val);
				}
				else if (inp.startsWith("sp")) {
					basespeed = (int) val;
					RConsole.println("Received Sp=" + val);
				}
				else if (inp.startsWith("br")) {
					breakfactor = (int) val;
					RConsole.println("Received Br=" + val);
				}
			}
		}
		
		public void stop() {
			running = false;
		}
	}
	
	private class FollowerThread extends Thread {
		private boolean running = true;
		
		public void run() {
			int regulation;
			long t = System.currentTimeMillis();
			
			long lastPrint = 0;
			
			int err;
			int val;
			int distance;
			float distanceFactor;
			// C = left motor, A = right motor
			startTime = System.currentTimeMillis();
			Motor.C.setSpeed(basespeed);
			Motor.A.setSpeed(basespeed);
			Motor.C.forward();
			Motor.A.forward();
			while (running)	{
				val = ls.readNormalizedValue();
				distance = us.getDistance();
				distanceFactor = Math.max(0, Math.min(distance, 40) - 25) / 15.0f;
				err = targetColor - val;
				regulation = (int) reg.calculate(targetColor, val, (System.currentTimeMillis() - t) / 1000., reset);
				reset = false;
				if (regulation > 0) {
					Motor.C.setSpeed(distanceFactor * Math.max(basespeed - breakfactor * Math.abs(err), 0));
					Motor.A.setSpeed(distanceFactor * Math.max(basespeed - breakfactor * Math.abs(err) - regulation, 0));
				}
				else {
					Motor.C.setSpeed(distanceFactor * Math.max(basespeed - breakfactor * Math.abs(err) + regulation, 0));
					Motor.A.setSpeed(distanceFactor * Math.max(basespeed - breakfactor * Math.abs(err), 0));
				}
				Motor.C.forward();
				Motor.A.forward();
				t = System.currentTimeMillis();
				if (System.currentTimeMillis() - lastPrint > 200) {
					lastPrint = t;
					RConsole.println((lastPrint - startTime) + ":\tval: " + val + "\terr: " + err + "\treg: " + regulation +
							"\tA: " + Motor.A.getSpeed() + "\tC: " + Motor.C.getSpeed() + "\tint: " + reg.getIntegral() +
							"\tdistance: " + distance + "\tdistFactor " + distanceFactor);
				}
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
		bluetooth.stop();
		follower.stop();
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
