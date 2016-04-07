import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.Button;
import lejos.nxt.Motor;

public class Toad {
    public static void main(String[] args) {
        TouchSensor leftSensor = new TouchSensor(SensorPort.S4);
        TouchSensor rightSensor = new TouchSensor(SensorPort.S1);
        Motor.A.setSpeed(360);
        Motor.C.setSpeed(360);
        while (!Button.ESCAPE.isDown()) {
            if (!Motor.A.isMoving() || !Motor.C.isMoving()) {
                Motor.A.forward();
                Motor.C.forward();
            }
            if (leftSensor.isPressed()) {
                Motor.A.stop();
                Motor.C.stop();
                RoboUtil.driveForCentimeters(-4);
                RoboUtil.rotateForDegrees(5);
            }
            if (rightSensor.isPressed()) {
                Motor.A.stop();
                Motor.C.stop();
                RoboUtil.driveForCentimeters(-4);
                RoboUtil.rotateForDegrees(-5);
            }
        }
    }
}
