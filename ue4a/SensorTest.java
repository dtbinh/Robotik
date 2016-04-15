import lejos.nxt.SensorPort;
import lejos.nxt.Motor;
import lejos.nxt.LightSensor;
import lejos.nxt.Button;

public class SensorTest implements Runnable {
    private LightSensor ls;

    public static void main(String[] args) {
        SensorTest st = new SensorTest();
        st.selfmain();
    }

    public void run() {
        byte dir = 1;
        while (!Button.ESCAPE.isDown()) {
            if (!Motor.B.isMoving()) {
                Motor.B.rotate(dir * 2160, true);
                dir = (byte) -dir;
            }
        }
    }

    public void selfmain() {
        ls = new LightSensor(SensorPort.S1);
        ls.setFloodlight(true);
        Motor.B.setSpeed(900);
        Thread t = new Thread(this);
        Motor.B.rotate(-1080, false);
        t.start();
        while (!Button.ESCAPE.isDown()) {
            System.out.print("\r" + ls.readValue());
        }
        try {
        t.join();
        }
        catch (Exception e) {}
    }
}
