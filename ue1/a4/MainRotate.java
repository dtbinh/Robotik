import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class MainRotate {
    public static void main(String[] args) throws Exception {
        DifferentialPilot pilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3, Motor.C, Motor.A);
        pilot.travel(50);
        Thread.sleep(5000);
        pilot.travel(-50);
        Thread.sleep(5000);
        pilot.rotate(90);
        Thread.sleep(5000);
        pilot.rotate(-90);
        Thread.sleep(5000);
        pilot.rotate(180);
        Thread.sleep(5000);
        pilot.rotate(-180);
        Thread.sleep(5000);
        pilot.rotate(270);
        Thread.sleep(5000);
        pilot.rotate(-270);
        Thread.sleep(5000);
        pilot.rotate(360);
        Thread.sleep(5000);
        pilot.rotate(-360);
        Thread.sleep(5000);
    }
}
