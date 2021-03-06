
public class LightSensorIntegrator {
	public int getLightValue(int[] sensorValues){
		int leftIntegral = 0;
		int rightIntegral = 0;
		int mid = sensorValues.length/2;
		for (int i = 0; i < mid; i++) {
			leftIntegral+=sensorValues[i];
		}
		for (int i = mid+1; i < sensorValues.length; i++) {
			rightIntegral+=sensorValues[i];
		}
		return leftIntegral - rightIntegral;
	}
}
