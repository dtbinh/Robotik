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

    private int[] fields;

    private TicTacToe() {
        btConn = new BtNxt();
        graphicsContext = new Graphics();
    }

    public void buttonPressed(Button b) {
        if (b == Button.LEFT) {
            RConsole.println("Button LEFT, Mode " + mode + ", Index " + selectedIndex);
            switch (mode) {
                case 0: selectedIndex = (selectedIndex + 1) % 2; drawMasterSlave(); break;
                case 1: if (devices.size() > 0) {selectedIndex = (selectedIndex - 1 + devices.size()) % devices.size(); drawSelectDevice();} break;
                case 2: selectedIndex = (selectedIndex + 8) % 9; drawField(); break;
            }
        }
        else if (b == Button.RIGHT) {
            RConsole.println("Button RIGHT, Mode " + mode + ", Index " + selectedIndex);
            switch (mode) {
                case 0: selectedIndex = (selectedIndex + 1) % 2; drawMasterSlave(); break;
                case 1: if (devices.size() > 0) {selectedIndex = (selectedIndex + 1) % devices.size(); drawSelectDevice();} break;
                case 2: selectedIndex = (selectedIndex + 1) % 9; drawField(); break;
            }
        }
        else if (b == Button.ENTER) {
            RConsole.println("Button ENTER, Mode " + mode + ", Index " + selectedIndex);
            switch (mode) {
                case 0: selectMasterSlave(); break;
                case 1: selectDevice(); break;
                case 2: submitTurn(); break;
            }
        }
        else {
        }
    }

    public void buttonReleased(Button b) {
    }

    public void newValue() {
        if (mode == 2) {
            if (!ownTurn) {
                fields[btConn.getNextValue() - 1] = isMaster ? -1 : 1;
                ownTurn = true;
            }
        }
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
        if (isMaster) {
            otherDevice = devices.get(selectedIndex);
            devices = null;
            btConn.connectTo(otherDevice);
            btConn.startCommunication();
        }
        else {
            btConn.slave();
            btConn.startCommunication();
        }
        selectedIndex = 0;
        fields = new int[9];
        ownTurn = isMaster;
        drawField();
    }

    private void drawErrorMessage(String message) {
        graphicsContext.clear();
        graphicsContext.drawString(message, 0, 0, graphicsContext.LEFT);
    }

    private void drawCross(int x, int y) {
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * y,
                                 WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * (x + 1), HEIGHT / 3 * (y + 1));
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * (y + 1),
                                 WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * (x + 1), HEIGHT / 3 * y);
    }

    private void drawCircle(int x, int y) {
        graphicsContext.drawArc(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * y, HEIGHT / 6, HEIGHT / 6, 0, 360);
    }

    private void drawRect(int x, int y) {
        graphicsContext.drawRoundRect(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * y, HEIGHT / 6, HEIGHT / 6, HEIGHT / 10, HEIGHT / 10);
    }

    private void drawField() {
        graphicsContext.clear();
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 6, 0, WIDTH / 2 - HEIGHT / 6, HEIGHT);
        graphicsContext.drawLine(WIDTH / 2 + HEIGHT / 6, 0, WIDTH / 2 + HEIGHT / 6, HEIGHT);
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2, HEIGHT / 3, WIDTH / 2 + HEIGHT / 2, HEIGHT / 3);
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2, 2 * HEIGHT / 3, WIDTH / 2 + HEIGHT / 2, 2 * HEIGHT / 3);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                switch (fields[i * 3 + j]) {
                    case 1: drawCross(j, i);
                    case -1: drawCircle(j, i);
                }
                if (ownTurn && (i * 3 + j) == selectedIndex) {
                    drawRect(j, i);
                }
            }
        }

    }

    private void submitTurn() {
        if (ownTurn) {
            if (fields[selectedIndex] == 0 && selectedIndex <= 8 && selectedIndex >= 0) {
                fields[selectedIndex] = isMaster ? 1 : -1;
                ownTurn = false;
                btConn.sendValue(selectedIndex + 1);
            }
            else {
                drawErrorMessage("ERROR!");
            }
            drawField();
        }
    }

    private void runMain() {
        for (Button b : Button.BUTTONS) {
            if (b != Button.ESCAPE) {
                b.addButtonListener(this);
            }
        }
        btConn.addListener(this);
        mode = 0;
        selectedIndex = 0;
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
