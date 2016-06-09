import lejos.nxt.Button;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;

public class UltrasonicTest {
    private static class UltrasonicThread extends Thread {
        private boolean running;
        private UltrasonicSensor sensor;
        public UltrasonicThread() {
            this.sensor = new UltrasonicSensor(SensorPort.S4);
        }

        public void run() {
            running = true;
            int value;
            while (running) {
                value = sensor.getDistance();
                if (value > 100)
                    RConsole.print("\r" + value);
                else if (value > 10)
                    RConsole.print("\r0" + value);
                else
                    RConsole.print("\r00" + value);
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {}
            }
            RConsole.println("\n");
        }

        public void stop() {
            this.running = false;
        }
    }

    public static void main(String[] args) {
        RConsole.openUSB(0);
        UltrasonicThread t = new UltrasonicThread();
        RConsole.println("Starting measurement...");
        t.start();
        Button.ESCAPE.waitForPress();
        RConsole.println("Finishing...");
        t.stop();
        try {
            t.join();
        }
        catch (Exception e) {}
        RConsole.close();
    }
}
