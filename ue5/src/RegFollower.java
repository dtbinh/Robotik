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
	
	private boolean reset = false;
	
	private RegFollower() {
		ls = new LightSensor(SensorPort.S4);
		darkColor = 255;
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
        	int val = ls.readValue();
        	if (val < darkColor) darkColor = val;
        	if (val > lightColor) lightColor = val;
        }
        targetColor = (darkColor + lightColor) / 2;
        RConsole.println("Min value: " + darkColor + " Max value: " + lightColor + " Average value: " + targetColor);
        RConsole.println("Finding target value...");
        tempPilot.setRotateSpeed(36);
        tempPilot.rotateLeft();
        while (ls.readValue() != targetColor);
        tempPilot.stop();
	}
	
	private boolean waitForBluetooth() {
		conn = Bluetooth.waitForConnection(30000, NXTConnection.PACKET);
        return !(conn == null);
	}
	
	private class FollowerThread extends Thread {
		private boolean running = true;
		
		public void run() {
			PIDRegulator reg = new PIDRegulator();
			int regulation;
			long t = System.currentTimeMillis();
			int err;
			int val;
			// C = left motor, A = right motor
			Motor.C.setSpeed(basespeed);
			Motor.A.setSpeed(basespeed);
			Motor.C.forward();
			Motor.A.forward();
			while (running)	{
				val = ls.getLightValue();
				err = targetColor - val;
				regulation = (int) reg.calculate(targetColor, val, System.currentTimeMillis() - t, reset);
				reset = false;
				if (regulation > 0) {
					Motor.C.setSpeed(basespeed - breakfactor * err);
					Motor.A.setSpeed(basespeed - breakfactor * err - regulation);
				}
				else {
					Motor.C.setSpeed(basespeed - breakfactor * err + regulation);
					Motor.A.setSpeed(basespeed - breakfactor * err);
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
		follower.start();
		Button.ESCAPE.waitForPress();
		follower.stop();
		try {
			follower.join();
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
