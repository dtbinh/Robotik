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
	
	private RegFollower() {
		ls = new LightSensor(SensorPort.S4);
		darkColor = 255;
		lightColor = 0;
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
        tempPilot.rotateLeft();
        tempPilot.setRotateSpeed(36);
        RConsole.println("Finding target value...");
        while (ls.readValue() != targetColor);
        tempPilot.stop();
        tempPilot = null;
	}
	
	private boolean waitForBluetooth() {
		conn = Bluetooth.waitForConnection(30000, NXTConnection.PACKET);
        return !(conn == null);
	}
	
	private void runMain() {
		findColors();
		if (waitForBluetooth()) {
			RConsole.println("Bluetooth device found: " + conn.getAddress());
		}
		else {
			RConsole.println("No bluetooth device connected");
		}
	}
	
	public static void main(String[] args) {
		RConsole.openUSB(0);
		RegFollower rf = new RegFollower();
		rf.runMain();
		RConsole.close();
	}
}
