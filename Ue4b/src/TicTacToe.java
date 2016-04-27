import lejos.nxt.comm.RConsole;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;

import lejos.nxt.LCD;
import javax.microedition.lcdui.Graphics;

import java.util.ArrayList;
import javax.bluetooth.RemoteDevice;

public class TicTacToe implements ButtonListener, BtNxt.BtListener {
    private static int WIDTH = LCD.SCREEN_WIDTH;
    private static int HEIGHT = LCD.SCREEN_HEIGHT;

    private BtNxt btConn;
    private Graphics graphicsContext;

    private int mode; // 0: Select Master-Slave --- 1: Select Other Device --- 2: Play Game
    private int selectedIndex;
    private ArrayList<RemoteDevice> devices;
    private RemoteDevice otherDevice;
    private boolean ownTurn;
    private boolean isMaster;

    private TicTacToe() {
        btConn = new BtNxt();
        graphicsContext = new Graphics();
    }

    public void buttonPressed(Button b) {
        if (b == Button.LEFT) {
            switch (mode) {
                case 0: selectedIndex = (selectedIndex + 1) % 2; drawMasterSlave(); break;
                case 1: if (devices.size() > 0) {selectedIndex = (selectedIndex - 1 + devices.size()) % devices.size(); drawSelectDevice();} break;
                case 2: selectedIndex = (selectedIndex + 8) % 9; drawField(); break;
            }
        }
        else if (b == Button.RIGHT) {
            switch (mode) {
                case 0: selectedIndex = (selectedIndex + 1) % 2; drawMasterSlave(); break;
                case 1: if (devices.size() > 0) {selectedIndex = (selectedIndex + 1) % devices.size(); drawSelectDevice();} break;
                case 2: selectedIndex = (selectedIndex + 1) % 9; drawField(); break;
            }
        }
        else if (b == Button.ENTER) {
            switch (mode) {
                case 0: selectMasterSlave(); break;
                case 1: selectDevice(); break;
                case 2: submitTurn(); break;
            }
        }
        else if (b == Button.ESCAPE) {

        }
        else {
        }
    }

    public void buttonReleased(Button b) {
    }

    public void newValue() {
    }

    private void selectMasterSlave() {
        isMaster = (selectedIndex == 0);
        if (isMaster) {
            mode = 1;
            RConsole.println("First");
            devices = btConn.getDevices();
            RConsole.println("LAST");
            selectedIndex = devices.size() - 1;
            drawSelectDevice();
        }
        else {
            mode = 2;
        }
    }

    private void drawMasterSlave() {
        int fontHeight = graphicsContext.getFont().getHeight();
        graphicsContext.clear();
        graphicsContext.drawString("Master", 20, 10, graphicsContext.LEFT);
        graphicsContext.drawString("Slave", 20, 30, graphicsContext.LEFT);
        graphicsContext.fillArc(3, 10 + (selectedIndex) * 20, fontHeight, fontHeight, 0, 360);
    }

    private void drawSelectDevice() {
        int fontHeight = graphicsContext.getFont().getHeight();
        graphicsContext.clear();
        for (int i = 0; i < devices.size(); ++i) {
            graphicsContext.drawString(devices.get(i).getFriendlyName(true), 20, 10 + i * (3 + fontHeight), graphicsContext.LEFT);
        }
        if (selectedIndex > -1) {
            graphicsContext.fillArc(3, 10 + selectedIndex * (3 + fontHeight), fontHeight, fontHeight, 0, 360);
        }
        else {
            graphicsContext.drawString("No devices found!", 20, 10, graphicsContext.LEFT);
            try {Thread.sleep(1000);} catch (Exception e) {}
            mode = 0;
            selectedIndex = 0;
            drawMasterSlave();
        }
    }

    private void selectDevice() {
        otherDevice = devices.get(selectedIndex);
        devices = null;
        btConn.connectTo(otherDevice);
        btConn.startCommunication();
        selectedIndex = 0;
        ownTurn = isMaster;
        drawField();
    }

    private void drawField() {
    }

    private void submitTurn() {
    }

    private void runMain() {
        mode = 0;
        selectedIndex = 0;
        for (Button b : Button.BUTTONS) {
            b.addButtonListener(this);
        }
        drawMasterSlave();
        Button.ESCAPE.waitForPress();
    }

    public static void main(String args[]) {
        RConsole.open();
        TicTacToe ttt = new TicTacToe();
        ttt.runMain();
        RConsole.close();
    }
}
