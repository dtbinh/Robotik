import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.SensorPort;


public class LineFinder {

	private static double range = 100; // [-50,50]
	private static int measureDegrees = 90;

	private BufferedOutputStream bos;

	private static LightSensor lightSensor = new LightSensor(SensorPort.S4);
	private DifferentialPilot pilot;
	private static int isLeft = 1;

	public LineFinder() {
		pilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3,
				Motor.C, Motor.A);
	}

    public LineFinder(boolean ignore) {
    }

    public Line getLine(int[][] scans) {
		List<List<Point>> normalizedPoints = normalize(scans);
		Point[] maxValues = findMax(normalizedPoints);

		// Gerade senkrecht zu roborter
		if (!valuesGood(scans)) {
			int minInd = 0;
			for (int i = 1; i < maxValues.length; i++) {
				if (maxValues[i].y < maxValues[minInd].y) {
					minInd = i;
				}
			}
			double yAxix = 50 - minInd * 100 / (maxValues.length - 1);
			Line l = new Line(0, yAxix);
			return l;
		}

		maxValues[0].y = 50;
		maxValues[1].y = 0;
		maxValues[2].y = -50;
		checkSameValues(maxValues);
		plot(maxValues);

		List<Point> pointSet = new ArrayList<Point>();
		for (Point p : maxValues)
			pointSet.add(p);

		LeastSquaresCalculator c = new LeastSquaresCalculator(pointSet);
		Line l = c.fitLine();

		return l;
    }

	public static int[] scanLight() {
		Motor.B.rotate(measureDegrees * isLeft, true);
		ArrayList<Integer> values = new ArrayList<Integer>();
		while (Motor.B.isMoving()) {
			values.add(lightSensor.readValue());
			try {
				Thread.sleep(5);
			} catch (Exception e) {
			}
		}
		int temp = 0;
		int i = 0;
		int[] retValues = new int[values.size()];
		i = 0;
		for (Integer v : values) {
			retValues[i++] = v;
		}
		if (isLeft == 1) {
			int n = retValues.length;
			for (i = 0; i < n / 2; ++i) {
				temp = retValues[i];
				retValues[i] = retValues[n - 1 - i];
				retValues[n - 1 - i] = temp;
			}
		}
		isLeft = -isLeft;
		return retValues;
	}

	// bsp for:
	/*
	 * 0000000## 000000##0 00000##00 0000##000 000##0000 00##00000
	 */

	public Line findLine() {
		Motor.B.setSpeed(100);
		Motor.B.rotate(measureDegrees / 2);
		isLeft = -1;
		pilot.setTravelSpeed(5);
		pilot.travel(3);
		int[] scan0 = scanLight();
		pilot.travel(-3);
		int[] scan1 = scanLight();
		pilot.travel(-3);
		int[] scan2 = scanLight();

		int[][] scans = new int[3][];
		scans[0] = scan0;
		scans[1] = scan1;
		scans[2] = scan2;
		return getLine(scans);
	}

	private List<List<Point>> normalize(int[][] scans) {
		List<List<Point>> result = new LinkedList<List<Point>>();

		// for each scan
		for (int[] scan : scans) {
			double step = 1. * range / (scan.length - 1);
			double nextX = -range / 2.;
			List<Point> list = new LinkedList<Point>();
			// for each point of the scan
			for (int i : scan) {
				list.add(new Point(nextX, i));
				nextX += step;
			}
			result.add(list);
		}

		return result;
	}

	private void plot(List<List<Point>> points) {
		for (List<Point> l : points) {
			System.out.println("List:");
			for (Point p : l) {
				System.out.print(p.toString() + " ");
			}
		}
	}

	private void plot(Point[] points) {
		for (Point p : points) {
			System.out.println(p.toString());
		}
	}

	private Point[] findMax(List<List<Point>> points) {
		Point[] maxValues = new Point[points.size()];
		int i = 0;
		for (List<Point> l : points) {
			Point currentMax = new Point(0, Double.MAX_VALUE);
			for (Point p : l) {
				if (p.y < currentMax.y)
					currentMax = p;
			}
			maxValues[i++] = currentMax;
		}
		return maxValues;
	}

	private void checkSameValues(Point[] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[i].x == arr[j].x) {
					arr[i].x += Math.random() / 1000.;
					i--;
					break;
				}
			}
		}
	}

	private boolean valuesGood(int[][] scan) {
		for (int[] arr : scan) {
			double ew = getEW(arr);
			double stdDev = getStdDev(arr, ew);
			if (stdDev < 0.5)
				return false;
		}
		return true;
	}

	private double getEW(int[] scan) {
		int sum = 0;
		for (int i : scan) {
			sum += i;
		}
		return (double) sum / scan.length;
	}

	private double getStdDev(int[] scan, double ew) {
		double sum = 0;
		for (double d : scan) {
			sum += (d - ew) * (d - ew);
		}
		return Math.sqrt(sum) / (scan.length-1);
	}

	public static void main(String[] args) {
		LineFinder f = new LineFinder();
		f.findLine();
	}
}
