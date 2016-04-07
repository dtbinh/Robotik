import lejos.nxt.Motor;

public class MainRotate {
    public static void main(String[] args) throws Exception {
        /*
        Motor.A.setSpeed(180);
        Motor.C.setSpeed(180);
        Motor.A.forward();
        Motor.C.backward();
        Thread.sleep(1000);
        Motor.A.stop();
        Motor.C.stop();
        */
        RoboUtil.driveForCentimeters(10);
        RoboUtil.rotateForDegrees(360);

    }
}
