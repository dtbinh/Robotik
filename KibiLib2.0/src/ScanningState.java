import lejos.robotics.navigation.DifferentialPilot;

public class ScanningState extends State {
	
	private DifferentialPilot pilot;
	
    public ScanningState(LineFollower context) {
        super(context);
        pilot = context.getPilot();
    }

    public void enter() {
    	LineFinder f = new LineFinder();
    	Line l = f.findLine();
    	pilot.travel(8+3);
    	pilot.setRotateSpeed(90);
    	pilot.rotate(-(90 - l.getAngle() * 180 / Math.PI));
    }

    public void leave() {
    }
}
