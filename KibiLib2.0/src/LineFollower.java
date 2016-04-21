import lejos.nxt.SensorPort;
import lejos.nxt.Motor;
import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.comm.RConsole;

public class LineFollower {
    private LightSensor lightSensor;
    private DifferentialPilot pilot;
    private static SensorPort scannerPort = SensorPort.S4;

    private int whiteValue = 0;
    private int blackValue = 255;

    private State lifist;
    private State scanst;
    private State adjust;
    private State lifost;

    public void runMain() {
        lifist = new LineFindingState(this);
        scanst = new ScanningState(this);
        //adjust = new AdjustingState(this);
        //lifost = new LineFollowingState(this);
        lifist.enter();
        RConsole.println(whiteValue + "");
        RConsole.println(blackValue + "");
    }

    public static void main(String[] args) {
        RConsole.open();
        LineFollower lf = new LineFollower();
        lf.runMain();
        RConsole.close();
    }
    
    public LineFollower() {
        lightSensor = new LightSensor(scannerPort);
        pilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3, Motor.C, Motor.A);
        pilot.setTravelSpeed(15);
    }

    public void setWhiteValue(int whiteValue) {
        this.whiteValue = whiteValue;
    }

    public int getWhiteValue() {
        return this.whiteValue;
    }

    public void setBlackValue(int blackValue) {
        this.blackValue = blackValue;
    }

    public int getBlackValue() {
        return this.blackValue;
    }

    public DifferentialPilot getPilot() {
        return this.pilot;
    }

    public LightSensor getLightSensor() {
        return this.lightSensor;
    }
}
