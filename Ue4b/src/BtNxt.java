import java.util.ArrayList;
import java.util.LinkedList;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;

public class BtNxt implements Runnable{
	
	public static final int bufferLen = 1;
	
	private BTConnection conn;
	private LinkedList<Integer> q;
	private LinkedList<BtListener> listener;
    private boolean running;
	
	public interface BtListener{
		void newValue();
        void connectionFinished();
	}
	
	public BtNxt(){
		q = new LinkedList<Integer>();
		Bluetooth.setFriendlyName("Kibi");
		listener = new LinkedList<BtNxt.BtListener>();
	}
	
	public void addListener(BtListener l){
		listener.add(l);
	}
	
	public void startCommunication(){
        running = true;
		new Thread(this).start();
	}

    public void stop() {
        running = false;
    }

	public boolean slave(){
		conn = Bluetooth.waitForConnection(30000, NXTConnection.PACKET);
        return !(conn == null);
	}
	
	public ArrayList<RemoteDevice> getDevices(){
		ArrayList<RemoteDevice> devices = Bluetooth.inquire(1, 5, 0);
		return devices;
	}
	
	public void connectTo(RemoteDevice d){
		conn = Bluetooth.connect(d);
	}
	
	public boolean hasNewValues(){
		return !q.isEmpty();
	}
	
	public int getNextValue(){
		int a = -1;
		synchronized (this) {
			a = q.get(0);
			q.remove(0);
		}
		return a;
	}
	
	public void sendValue(int i){
		byte[] buffer = new byte[1];
		buffer[0] = (byte)i;
		conn.write(buffer, 1);
	}

	public void run() {
		byte[] b = new byte[bufferLen];
		while(running){
			int len = conn.read(b, bufferLen);
            RConsole.println("Received " + len + " bytes:");
            for (int i = 0; i < len; ++i) {
                RConsole.println(Integer.toHexString(b[i]));
            }
            RConsole.println("DONE!");
            if (len == -1) {
                break;
            }
			int value = b[0]; 
			synchronized (this) {
                RConsole.println("Adding Value to queue");
				q.add(value);
			}
			notifyListener();
		}
        notifyFinished();
		//TODO: protokoll mit anderen abstimmen
	}
	
	private void notifyListener(){
		for (BtListener l : listener)
			l.newValue();
	}

    private void notifyFinished() {
        for (BtListener l : listener)
            l.connectionFinished();
    }
	
	public static void main(String[] args){
		BtNxt bt = new BtNxt();
	}
}
