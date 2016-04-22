import java.util.ArrayList;
import java.util.LinkedList;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class BtNxt implements Runnable{
	
	public static final int bufferLen = 1;
	
	private BTConnection conn;
	private LinkedList<Integer> q;
	private LinkedList<BtListener> listener;
	
	public interface BtListener{
		void newValue();
	}
	
	public BtNxt(){
		q = new LinkedList<Integer>();
		Bluetooth.setName("Kibi");
		listener = new LinkedList<BtNxt.BtListener>();
	}
	
	public void addListener(BtListener l){
		listener.add(l);
	}
	
	public void startCommunication(){
		new Thread(this).start();
	}

	public void slave(){
		conn = Bluetooth.waitForConnection(100, NXTConnection.PACKET);
	}
	
	public ArrayList<RemoteDevice> getDevices(){
		ArrayList<RemoteDevice> devices = Bluetooth.inquire(1, 100, 0);
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
			q.remove(0);
			a = q.get(0);
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
		while(true){
			int len = conn.read(b, bufferLen);
			int value =b[0]; 
			synchronized (this) {
				q.add(value);
			}
			notifyListener();
		}
		//TODO: protokoll mit anderen abstimmen
	}
	
	private void notifyListener(){
		for(BtListener l : listener)
			l.newValue();
	}
	
	public static void main(String[] args){
		BtNxt bt = new BtNxt();
	}
}
