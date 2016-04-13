import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPortListener;
import lejos.nxt.SensorPort;
import java.util.List;
import java.util.ArrayList;

public class UltrasonicSensorExtended extends UltrasonicSensor implements Runnable {
    private Thread internalThread;
    private List<SensorPortListener> listeners;
    private SensorPort port;
    private boolean isRunning = true;

    public UltrasonicSensorExtended(SensorPort port) {
        super(port);
        this.port = port;
        listeners = new ArrayList<SensorPortListener>();
        internalThread = new Thread(this);
        internalThread.start();
    }

    public void addSensorPortListener(SensorPortListener listener) {
        synchronized(this) {
            listeners.add(listener);
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void run() {
        int oldValue = 0, newValue = 0;
        while (isRunning) {
            oldValue = newValue;
            newValue = getDistance();
            if (newValue != oldValue) {
                synchronized(this) {
                    for (SensorPortListener l : listeners) {
                        l.stateChanged(port, oldValue, newValue);
                    }
                }
            }
        }
    }
}
