import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.Button;

public class LineFollowingState extends State {
    private DifferentialPilot pilot;
    private LightSensor lightSensor;

    public LineFollowingState(LineFollower context) {
        super(context);
        this.pilot = context.getPilot();
        this.lightSensor = context.getLightSensor();
    }

    public void enter() {
        pilot.setTravelSpeed(10);
        pilot.setRotateSpeed(30);
        Motor.B.rotateTo(0);
        Motor.B.resetTachoCount();
        pilot.forward();
        int lastTurn = 1;
        int lightValue;
        while (true) {
            lightValue = lightSensor.readValue();
            if (lightValue > 0.75 * context.getWhiteValue()) {
                pilot.stop();
                int def = 10;
                int angle = def;
                double factor = 1.5;
                while (lightSensor.readValue() > 1.4 * context.getBlackValue()) {
                    pilot.rotate(lastTurn * angle);
                    if (lightSensor.readValue() > 1.4 * context.getBlackValue()) {
                        pilot.rotate(-2 * lastTurn * angle);
                        lastTurn = -lastTurn;
                    }
                    if (lightSensor.readValue() > 1.4 * context.getBlackValue()) {
                        lastTurn = -lastTurn;
                        pilot.rotate(lastTurn * angle);
                        angle = 2 * angle / 3;
                        if (angle < 5) {
                            angle = (int) factor * def;
                            factor *= 2;
                        }
                    }
                }
                pilot.forward();
            }
        }
    }

    public void leave() {
    }
}
