import java.util.ArrayList;
import java.util.List;

public class LeastSquaresCalculator {
	private List<Point> points;
	private Line f;
	
	public LeastSquaresCalculator(List<Point> points, Line f) {
		super();
		this.points = points;
		this.f = f;
	}
	
	public LeastSquaresCalculator(List<Point> points) {
		this(points, new Line(1, 0));
	}
	
	//TODO: THIS IS NOT ALWAYS A LINE!!!
	public Line fitLine(){
		//calc the values
		double sumfX_k = 0;
		double sumfX_k2 = 0;
		double sumY_k = 0;
		double sumfX_ky_k = 0;
		for(Point t : points){
			sumfX_k+=f.f(t.x);
			sumfX_k2+=f.f(t.x)*f.f(t.x);
			sumY_k+=t.y;
			sumfX_ky_k+=f.f(t.x)*t.y;
		}
		//create the matrix
		double[][] m = new double[2][2];
		double[] b = new double[2];
		m[0][0]=points.size();
		m[0][1]=sumfX_k;
		m[1][0]=sumfX_k;
		m[1][1]=sumfX_k2;
		b[0]=sumY_k;
		b[1]=sumfX_ky_k;
		Matrix matrix = new Matrix(m);
		double[] x = matrix.solve(b);
		return new Line(x[1], x[0]);
	}
	
	public static void main(String[] args){
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(0., 2.));
		points.add(new Point(1., 1.));
		points.add(new Point(2., 0.));
		points.add(new Point(3., -1.));
		points.add(new Point(4., 1.));
		LeastSquaresCalculator c = new LeastSquaresCalculator(points);
	}
}
