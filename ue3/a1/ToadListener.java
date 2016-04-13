import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.TouchSensor;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;

public class ToadListener implements SensorPortListener {
    private final SensorPort LEFTTOUCHPORT = SensorPort.S4;
    private final SensorPort RIGHTTOUCHPORT = SensorPort.S1;
    private final SensorPort ULTRASONICPORT = SensorPort.S2;

    private DifferentialPilot pilot;
    private UltrasonicSensorExtended sensor;
    private TouchSensor leftSensor;
    private TouchSensor rightSensor;

    private byte wallRight = 0;
    private byte mode = 0;
    private byte stepDegrees = 15;
    private byte arcRadius = 50;
    private byte cmPerSecond = 10;

    public ToadListener() {
        pilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3, Motor.C, Motor.A);
        pilot.setTravelSpeed(2 * cmPerSecond);

        sensor = new UltrasonicSensorExtended(ULTRASONICPORT);
        sensor.addSensorPortListener(this);

        leftSensor = new TouchSensor(LEFTTOUCHPORT);
        LEFTTOUCHPORT.addSensorPortListener(this);
        rightSensor = new TouchSensor(RIGHTTOUCHPORT);
        RIGHTTOUCHPORT.addSensorPortListener(this);
    }

    public void stateChanged(SensorPort port, int oldValue, int newValue) {
        if (port == ULTRASONICPORT) {
            if (newValue > 20) {
                Sound.beep();
            }
        }
        else if (port == LEFTTOUCHPORT || port == RIGHTTOUCHPORT) {
            if (newValue < 255) {
                if (mode == 0) {
                    wallRight = (port == LEFTTOUCHPORT) ? (byte) -1 : (byte) 1;
                    pilot.setTravelSpeed(cmPerSecond);
                }
                mode = 2; // pause main thread
                try {Thread.sleep(3);} catch (Exception e) {}
                pilot.stop();
                driveBack();
                mode = 1; // main thread moves in an arc
            }
        }
        else {
            System.out.println("UNKNOWN PORT!!!");
        }
    }

    public void driveBack() {
        pilot.travel(-4);
        pilot.rotate(wallRight * stepDegrees);
    }

    public void run() {
        while (!Button.ESCAPE.isDown()) {
             switch (mode) {
                case 0: if (!pilot.isMoving()) {pilot.forward();} break;
                case 1: if (!pilot.isMoving()) {pilot.arcForward(-wallRight * arcRadius);} break;
                case 2: break;
            }
        }
        pilot.stop();
        sensor.stop();
    }

    public static void main(String[] args) {
        ToadListener main = new ToadListener();
        main.run();
    }
}
