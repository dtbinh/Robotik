import lejos.nxt.LightSensor;

public class LineFindingState extends State {
    public LineFindingState(LineFollower context) {
        super(context);
    }

    public void enter() {
        LightSensor ls = context.getLightSensor();
        boolean notFound = true;
        context.getPilot().setTravelSpeed(10);
        context.getPilot().forward();
        while (notFound) {
            int value = ls.readValue();
            if (value > context.getWhiteValue()) {
                context.setWhiteValue(ls.readValue());
            }
            else if (value < 0.75 * context.getWhiteValue()) {
                notFound = false;
            }
        }
        context.getPilot().stop();
        try {Thread.sleep(1000);} catch (Exception e) {}
        context.setBlackValue(ls.readValue());
    }

    public void leave() {
        context.getPilot().stop();
    }
}
