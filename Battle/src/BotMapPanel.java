import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class BotMapPanel extends JPanel {
	private int xpos = 60, ypos = 60, angle = 0;
	private static final long serialVersionUID = 1L;
	
	public void setPosition(int xpos, int ypos, int angle) {
		this.xpos = xpos;
		this.ypos = 400 - ypos;
		this.angle = angle;
	}

	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawRect(15, 15, 400, 400);
        g.setColor(Color.BLUE);
        g.fillRect(16, 355, 60, 60);
        g.fillRect(355, 16, 60, 60);
        g.setColor(Color.RED);
        g.fillRect(16, 16, 60, 60);
        g.fillRect(355, 355, 60, 60);
        g.setColor(Color.GREEN);
        g.fillArc(xpos - 20, ypos - 20, 70, 70, angle - 15, 30);
    }
}
