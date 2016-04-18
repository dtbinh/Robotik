import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class LineFinder {
	
	private static double range = 100; //[-50,50]
	
	//bsp for:
	/*
	 * 0000000##
	 * 000000##0
	 * 00000##00
	 * 0000##000
	 * 000##0000
	 * 00##00000
	 * */
	
	public double findLineAngle(){
		//drive forward
		//scan
		//int[] scan0 = {25,24,21,24,25,21,28,40,45,58,51};
		int[] scan0 = {26,23,28,40,46,59, 52,44,38,29,21,24};
		//drive back
		//scan
		int[] scan1 = {26,23,28,40,46,59, 52,44,38,29,21,24};
		//drive back
		//scan
		//int[] scan2 = {51,60,46,37,28,23,21,23,21,24};
		int[] scan2 = {26,23,28,40,46,59, 52,44,38,29,21,24};
		
		int[][] scans = new int[3][];
		scans[0] = scan0;
		scans[1] = scan1;
		scans[2] = scan2;
		List<List<Point>> normalizedPoints = normalize(scans);
		Point[] maxValues = findMax(normalizedPoints);
		maxValues[0].y = 50;
		maxValues[1].y = 0;
		maxValues[2].y = -50;
		checkSameValues(maxValues);
		plot(maxValues);
		
		Set<Point> pointSet = new HashSet<>();
		for(Point p : maxValues)pointSet.add(p); 
		
		LeastSquaresCalculator c = new LeastSquaresCalculator(pointSet);
		Line l = c.fitLine();
		System.out.println(l.toString());
		
		//plot(normalizedPoints);
		return 0;
	}
	
	private List<List<Point>> normalize(int[][] scans){
		List<List<Point>> result = new LinkedList<List<Point>>();
		
		
		//for each scan
		for(int[] scan : scans){
			double step = 1.*range/(scan.length-1);
			double nextX = -range/2.;
			List<Point> list = new LinkedList<>();
			//for each point of the scan
			for(int i : scan){
				list.add(new Point(nextX, i));
				nextX+=step;
			}
			result.add(list);
		}
		
		return result;
	}
	
	
	private void plot(List<List<Point>> points){
		for(List<Point> l : points){
			System.out.println("List:");
			for(Point p : l){
				System.out.print(p.toString()+" ");
			}
		}
	}
	
	private void plot(Point[] points){
		for(Point p : points){
			System.out.println(p.toString());
		}
	}
	
	private Point[] findMax(List<List<Point>> points){
		Point[] maxValues = new Point[points.size()];
		int i = 0;
		for(List<Point> l : points){
			Point currentMax = new Point(0, Double.MIN_VALUE);
			for(Point p : l){
				if(p.y > currentMax.y) currentMax= p;
			}
			maxValues[i++] = currentMax;
		}
		return maxValues;
	}
	
	private void checkSameValues(Point[] arr){
		for (int i = 0; i < arr.length; i++) {
			for (int j = i+1; j < arr.length; j++) {
				if(arr[i].x==arr[j].x){
					arr[i].x+=Math.random()/1000.;
					i--;
					break;
				}
			}
		}
	}
	
	public static void main(String[] args){
		LineFinder f  = new LineFinder();
		f.findLineAngle();
	}
}
