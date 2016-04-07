public class House {
    public static void main(String args[]) {
        RoboUtil.driveForCentimeters(50);
        RoboUtil.rotateForDegrees(135);
        RoboUtil.driveForCentimeters((int) (50 * Math.sqrt(2)));
        RoboUtil.rotateForDegrees(-135);
        RoboUtil.driveForCentimeters(50);
        RoboUtil.rotateForDegrees(-45);
        RoboUtil.driveForCentimeters((int) (50 / Math.sqrt(2)));
        RoboUtil.rotateForDegrees(-90);
        RoboUtil.driveForCentimeters((int) (50 / Math.sqrt(2)));
        RoboUtil.rotateForDegrees(-135);
        RoboUtil.driveForCentimeters(50);
        RoboUtil.rotateForDegrees(135);
        RoboUtil.driveForCentimeters((int) (50 * Math.sqrt(2)));
        RoboUtil.rotateForDegrees(-135);
        RoboUtil.driveForCentimeters(50);
        RoboUtil.rotateForDegrees(180);
        RoboUtil.driveForCentimeters(50);
    }
}
