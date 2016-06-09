import javax.microedition.lcdui.Graphics;
import lejos.nxt.LCD;
import lejos.nxt.Button;

public class DisplayTest {
    public static int WIDTH = LCD.SCREEN_WIDTH;
    public static int HEIGHT = LCD.SCREEN_HEIGHT;
    public static void main(String args[]) {
        Graphics context = new Graphics();
        try {Thread.sleep(2000);} catch (Exception e) {}
        context.drawRect(0, 0, WIDTH-1, HEIGHT-1);
        try {Thread.sleep(2000);} catch (Exception e) {}
        context.drawArc((int) 0.2 * WIDTH, (int) 0.2 * HEIGHT, (int) 0.6 * WIDTH, (int) 0.6 * HEIGHT, 0, 360);
        try {Thread.sleep(2000);} catch (Exception e) {}
    }
}
