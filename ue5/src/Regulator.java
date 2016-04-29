
public interface Regulator {
	public double calculate(double setpoint, double current, double t, boolean reset);
}
