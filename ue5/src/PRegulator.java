
public class PRegulator implements Regulator {
	private double kp;
	
	public PRegulator() {
		this(1);
	}
	
	public PRegulator(double kp) {
		this.kp = kp;
	}
	
	@Override
	public double calculate(double setpoint, double current, double t, boolean reset) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getKp() {
		return kp;
	}

	public void setKp(double kp) {
		this.kp = kp;
	}
}
