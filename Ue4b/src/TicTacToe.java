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
    private boolean winMessage;

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

    private void win(int field) {
        winMessage = true;
        try {Thread.sleep(1000);} catch (Exception e) {}
        if (fields[field] == 1) {
            drawWin();
        }
        else {
            drawLose();
        }
        try {Thread.sleep(2000);} catch (Exception e) {}
        winMessage = false;
        mode = 0;
        selectedIndex = 0;
        fields = null;
        btConn.stop();
    }

    private void drawWin() {
        graphicsContext.clear();
        graphicsContext.drawString("YOU WIN!", 10, 10, graphicsContext.LEFT);
    }

    private void drawLose() {
        graphicsContext.clear();
        graphicsContext.drawString("YOU LOSE!", 10, 10, graphicsContext.LEFT);
    }

    private void checkField() {
        RConsole.println("Checking horizontal wins");
        if (fields[0] != 0 && fields[0] == fields[1] && fields[0] == fields[2]) {win(0); return;}
        if (fields[3] != 0 && fields[3] == fields[4] && fields[3] == fields[5]) {win(3); return;}
        if (fields[6] != 0 && fields[6] == fields[7] && fields[6] == fields[8]) {win(6); return;}
        RConsole.println("Checking vertical wins");
        if (fields[0] != 0 && fields[0] == fields[3] && fields[0] == fields[6]) {win(0); return;}
        if (fields[1] != 0 && fields[1] == fields[4] && fields[1] == fields[7]) {win(1); return;}
        if (fields[2] != 0 && fields[2] == fields[5] && fields[2] == fields[8]) {win(2); return;}
        RConsole.println("Checking diagonal wins");
        if (fields[0] != 0 && fields[0] == fields[4] && fields[0] == fields[8]) {win(0); return;}
        if (fields[6] != 0 && fields[6] == fields[4] && fields[6] == fields[2]) {win(6); return;}
    }

    public void newValue() {
        RConsole.println("Received new value, mode: " + mode + " ownTurn: " + (ownTurn ? "true" : "false"));
        if (mode == 2) {
            if (!ownTurn) {
                RConsole.println("Getting new value");
                int val = btConn.getNextValue();
                RConsole.println("New value: " + val);
                fields[val - 1] = -1;
                ownTurn = true;
                drawField();
                checkField();
            }
        }
    }

    private void submitTurn() {
        RConsole.println("Submitting turn, ownTurn: " + (ownTurn ? "true" : "false") + " selectedIndex: " + selectedIndex);
        if (ownTurn) {
            if (fields[selectedIndex] == 0 && selectedIndex <= 8 && selectedIndex >= 0) {
                fields[selectedIndex] = 1;
                ownTurn = false;
                btConn.sendValue(selectedIndex + 1);
                drawField();
                checkField();
            }
        }
    }

    public void connectionFinished() {
        while (winMessage);
        RConsole.println("Connection finished");
        drawConnectionFinished();
        try {Thread.sleep(2000);} catch (Exception e) {}
        btConn = new BtNxt();
        btConn.addListener(this);
        mode = 0;
        selectedIndex = 0;
        drawMasterSlave();
    }

    private void selectMasterSlave() {
        RConsole.println("Selecting Master slave, selectedIndex: " + selectedIndex);
        isMaster = (selectedIndex == 0);
        if (isMaster) {
            mode = 1;
            devices = btConn.getDevices();
            selectedIndex = devices.size() - 1;
            drawSelectDevice();
        }
        else {
            drawWaitForConnection();
            selectDevice();
        }
    }

    private void drawConnectionFinished() {
        RConsole.println("Drawing Connection finished");
        graphicsContext.clear();
        graphicsContext.drawString("Connection", 10, 10, graphicsContext.LEFT);
        graphicsContext.drawString("finished", 10, 30, graphicsContext.LEFT);
    }

    private void drawWaitForConnection() {
        RConsole.println("Drawing Wait for connection");
        graphicsContext.clear();
        graphicsContext.drawString("Waiting for", 10, 10, graphicsContext.LEFT);
        graphicsContext.drawString("connection...", 10, 30, graphicsContext.LEFT);
    }

    private void drawMasterSlave() {
        RConsole.println("Drawing Master Slave");
        int fontHeight = graphicsContext.getFont().getHeight();
        graphicsContext.clear();
        graphicsContext.drawString("Master", 20, 10, graphicsContext.LEFT);
        graphicsContext.drawString("Slave", 20, 30, graphicsContext.LEFT);
        graphicsContext.fillArc(3, 10 + (selectedIndex) * 20, fontHeight, fontHeight, 0, 360);
    }

    private void drawSelectDevice() {
        RConsole.println("Drawing Select Device");
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

    private void drawNoConnection() {
        RConsole.println("Drawing No connection");
        graphicsContext.clear();
        graphicsContext.drawString("No Connection!", 10, 10, graphicsContext.LEFT);
        try {Thread.sleep(2000);} catch (Exception e) {}
        mode = 0;
        selectedIndex = 0;
        drawMasterSlave();
    }

    private void selectDevice() {
        RConsole.println("Selecting device, isMaster: " + (isMaster ? "true" : "false") + " selectedIndex: " + selectedIndex);
        if (isMaster) {
            otherDevice = devices.get(selectedIndex);
            devices = null;
            btConn.connectTo(otherDevice);
            btConn.startCommunication();
        }
        else {
            boolean hasConnection = btConn.slave();
            if (!hasConnection) {
                drawNoConnection();
            }
            btConn.startCommunication();
        }
        selectedIndex = 0;
        mode = 2;
        winMessage = false;
        fields = new int[9];
        ownTurn = isMaster;
        drawField();
    }

    private void drawCross(int x, int y) {
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * y,
                                 WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * (x + 1), HEIGHT / 3 * (y + 1));
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * (y + 1),
                                 WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * (x + 1), HEIGHT / 3 * y);
    }

    private void drawCircle(int x, int y) {
        graphicsContext.drawArc(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * y, HEIGHT / 3, HEIGHT / 3, 0, 360);
    }

    private void drawRect(int x, int y) {
        graphicsContext.drawRoundRect(WIDTH / 2 - HEIGHT / 2 + HEIGHT / 3 * x, HEIGHT / 3 * y, HEIGHT / 3, HEIGHT / 3, HEIGHT / 10, HEIGHT / 10);
    }

    private void drawField() {
        RConsole.println("Drawing field");
        graphicsContext.clear();
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 6, 0, WIDTH / 2 - HEIGHT / 6, HEIGHT);
        graphicsContext.drawLine(WIDTH / 2 + HEIGHT / 6, 0, WIDTH / 2 + HEIGHT / 6, HEIGHT);
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2, HEIGHT / 3, WIDTH / 2 + HEIGHT / 2, HEIGHT / 3);
        graphicsContext.drawLine(WIDTH / 2 - HEIGHT / 2, 2 * HEIGHT / 3, WIDTH / 2 + HEIGHT / 2, 2 * HEIGHT / 3);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                switch (fields[i * 3 + j]) {
                    case 1: drawCross(j, i); break;
                    case -1: drawCircle(j, i); break;
                }
                if (ownTurn && (i * 3 + j) == selectedIndex) {
                    drawRect(j, i);
                }
            }
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
