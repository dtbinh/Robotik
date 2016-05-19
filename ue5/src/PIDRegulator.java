
public class PIDRegulator implements Regulator {
	private double kp;
	private double tn;
	private double tv;
	
	private double last_signal;
	
	private double int_last_signal;
	private double int_integral;
	
	private boolean hasLastSignal;
	
	public PIDRegulator() {
		this(1, 1, 1);
	}
	
	public PIDRegulator(double kp, double tn, double tv) {
		this.kp = kp;
		this.tn = tn;
		this.tv = tv;
		this.last_signal = 0;
		this.hasLastSignal = false;
		this.int_last_signal = 0;
		this.int_integral = 0;
	}
	
	private double integrate(double signal, double t, boolean reset) {
		if (! reset ) {
			this.int_integral += ((signal + this.int_last_signal) * t ) / 2;
			this.int_last_signal = signal;
			return this.int_integral;
		}
		else {
			this.int_last_signal = 0;
			this.int_integral = 0;
			return 0;
		}
	}

	@Override
	public double calculate(double setpoint, double current, double t, boolean reset) {
		double err = setpoint - current;
		
		// I
		
		double integral = integrate(err, t, reset);
		double result = err;
		// Prevent division by zero
		if (this.tn > 0.0) {
			result += integral / this.tn;
		}
		
		// D
		if (hasLastSignal && t > 0.0) {
			double diff = (current - this.last_signal) / t;
			result += this.tv * diff;
		}
		this.last_signal = current;
		hasLastSignal = true;
		
		// P
		return result * this.kp;
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
