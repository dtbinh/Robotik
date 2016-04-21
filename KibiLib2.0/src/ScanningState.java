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
    	pilot.travel(13+3);
    	pilot.setRotateSpeed(180);
    	pilot.rotate(l.getAngle());
    }

    public void leave() {
    }
}
