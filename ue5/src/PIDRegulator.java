
public class PIDRegulator implements Regulator {
	private double kp;
	private double tn;
	private double tv;
	
	public PIDRegulator() {
		this(1, 1, 1);
	}
	
	public PIDRegulator(double kp, double tn, double tv) {
		this.kp = kp;
		this.tn = tn;
		this.tv = tv;
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

	public double getTn() {
		return tn;
	}

	public void setTn(double tn) {
		this.tn = tn;
	}

	public double getTv() {
		return tv;
	}

	public void setTv(double tv) {
		this.tv = tv;
	}
}
