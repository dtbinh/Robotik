import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class Toad {
    public static void main(String[] args) {
        byte wallRight = 0; // defines, if the found wall is to the left or the right of the NXT
        byte mode = 0; // mode = 0: find wall; mode = 1: follow wall; mode = 2: check for corner;
        byte stepDegrees = 5;
        byte cmPerSecond = 10;
        byte checkDistance = 20;
        byte leftPressed = 0;

        TouchSensor leftSensor = new TouchSensor(SensorPort.S4);
        TouchSensor rightSensor = new TouchSensor(SensorPort.S1);
        DifferentialPilot pilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3, Motor.C, Motor.A);

        pilot.setTravelSpeed(cmPerSecond);

        while (!Button.ESCAPE.isDown()) {
            if (!pilot.isMoving()) {
                pilot.forward();
            }

            if (leftSensor.isPressed() || rightSensor.isPressed()) {
                leftPressed = leftSensor.isPressed() ? (byte) 1 : (byte) -1;
                if (mode == 0) {
                    wallRight = (byte) -leftPressed;
                    mode = 1;
                }

                if (mode == 1) {
                    pilot.stop();
                    if ((wallRight == 1 && leftPressed == 1) || (wallRight == -1 && leftPressed == -1)) {
                        pilot.travel(-10);
                        //pilot.arc(wallRight * 8, -wallRight * 85);
                        if (wallRight == 1) {
                            pilot.arc(8, 85);
                        }
                        else if (wallRight == -1) {
                            pilot.arc(-8, -85);
                        }
                    }
                    else {
                        pilot.travel(-4);
                        pilot.rotate(wallRight * stepDegrees);
                    }
                }

                else if (mode == 2) {
                    pilot.stop();
                    pilot.travel(-10);
                    //pilot.arc(wallRight * 8, -wallRight * 85);
                    if (wallRight == 1) {
                        pilot.arc(8, 85);
                    }
                    else if (wallRight == -1)  {
                        pilot.arc(-8, -85);
                    }
                    mode = 1;
                }
            }

            if (mode == 2 && pilot.isMoving() && pilot.getMovementIncrement() > 0.8 * checkDistance) {
                mode = 1;
            }

            if (mode == 1 && pilot.isMoving() && pilot.getMovementIncrement() > checkDistance) {
                pilot.stop();
                //pilot.arc(wallRight * 8, -wallRight * 85);
                if (wallRight == 1) {
                    pilot.arc(8, 85);
                }
                else if (wallRight == -1) {
                    pilot.arc(-8, -85);
                }
                pilot.rotate(180);
                mode = 2;
            }
        }
    }
}
