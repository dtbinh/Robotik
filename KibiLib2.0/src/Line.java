

public class Line {
	//f(x)=ax+b
	private double a;
	private double b;
	
	public Line(double a, double b) {
		super();
		this.a = a;
		this.b = b;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double f(double x) {
		return a*x+b;
	}
	
	public String toString(){
		return a+"*x+"+b;
	}
	
	public double getAngle(){
		return Math.atan(a);
	}
	
	public double getXAxisIntersect(){
		//0 = ax+b <=> x=-b/a
		return -b/a;
	}
}
