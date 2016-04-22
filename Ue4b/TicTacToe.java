import lejos.nxt.comm.RConsole;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;

import lejos.nxt.LCD;
import javax.microedition.lcdui.Graphics;

public class TicTacToe implements ButtonListener {
    private static int WIDTH = LCD.SCREEN_WIDTH;
    private static int HEIGHT = LCD.SCREEN_HEIGHT;

    //private BtNXT btConn;
    private Graphics graphicsContext;

    private int mode; // 0: Select Master-Slave --- 1: Select Other Device --- 2: Play Game
    private int selectedIndex;
    private int numberOfDevices;
    private boolean ownTurn;
    private boolean isMaster;

    private TicTacToe() {
        //btConn = new BtNXT();
        graphicsContext = new Graphics();
    }

    public void buttonPressed(Button b) {
        if (b == Button.LEFT) {
            switch (mode) {
                case 0: selectedIndex = (selectedIndex + 1) % 2; drawMasterSlave(); break;
                case 1: selectedIndex = (selectedIndex - 1 + numberOfDevices) % numberOfDevices; drawSelectDevice(); break;
                case 2: selectedIndex = (selectedIndex + 8) % 9; drawField(); break;
            }
        }
        else if (b == Button.RIGHT) {
            switch (mode) {
                case 0: selectedIndex = (selectedIndex + 1) % 2; drawMasterSlave(); break;
                case 1: selectedIndex = (selectedIndex + 1) % numberOfDevices; drawSelectDevice(); break;
                case 2: selectedIndex = (selectedIndex + 1) % 9; drawField(); break;
            }
        }
        else if (b == Button.ENTER) {
            switch (mode) {
                case 0: selectMasterSlave();
                case 1: selectDevice();
                case 2: submitTurn();
            }
        }
        else if (b == Button.ESCAPE) {

        }
        else {
        }
    }

    public void buttonReleased(Button b) {
    }

    private void selectMasterSlave() {
    }

    private void drawMasterSlave() {
        int fontHeight = graphicsContext.getFont().getHeight();
        graphicsContext.clear();
        graphicsContext.drawString("Master", 10, 10, graphicsContext.LEFT);
        graphicsContext.drawString("Slave", 10, 30, graphicsContext.LEFT);
        graphicsContext.drawArc(3, 10 + (selectedIndex) * 20, fontHeight, fontHeight, 0, 360);
    }

    private void drawSelectDevice() {
    }

    private void selectDevice() {
    }

    private void drawField() {
    }

    private void submitTurn() {
    }

    private void runMain() {
        mode = 0;
        selectedIndex = 0;
        drawMasterSlave();
        drawSelectDevice();
        Button.ESCAPE.waitForPress();
        //startGame();
    }

    public static void main(String args[]) {
        //RConsole.open();
        TicTacToe ttt = new TicTacToe();
        ttt.runMain();
        //RConsole.close();
    }
}
