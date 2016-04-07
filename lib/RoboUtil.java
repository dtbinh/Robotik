import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class RoboUtil {
    private static final NXTRegulatedMotor leftMotor = Motor.C;
    private static final NXTRegulatedMotor rightMotor = Motor.A;
    private static final int circumference = 176;
    private static final int axis = 113;

    public static double calcDistanceFromAngle(double degrees) {
        return axis * Math.PI * (degrees / 360.);
    }

    public static double calcRotationsFromDistance(double distance) {
        return (distance / circumference) * 360.;
    }

    public static void rotateForDegrees(double degrees) {
        double rotateDistance = calcDistanceFromAngle(Math.abs(degrees));
        double rotations = calcRotationsFromDistance(rotateDistance);
        if (degrees <= 0) {
            leftMotor.rotate((int) -rotations, true);
            rightMotor.rotate((int) rotations, false);
        }
        else {
            leftMotor.rotate((int) rotations, true);
            rightMotor.rotate((int) -rotations, false);
        }
    }

    public static void driveForCentimeters(int centimeters) {
        int rotations = (int) calcRotationsFromDistance(centimeters * 10);
        leftMotor.rotate(rotations, true);
        rightMotor.rotate(rotations, false);
    }
}
