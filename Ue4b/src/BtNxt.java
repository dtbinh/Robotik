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
	
	public BtNxt(){
		q = new LinkedList<Integer>();
		Bluetooth.setName("Kibi");
	}
	
	public void startCommunication(){
		new Thread(this).start();
	}

	public void host(){
		conn = Bluetooth.waitForConnection(0, NXTConnection.PACKET);
	}
	
	public void connect(){
		ArrayList<RemoteDevice> devices = Bluetooth.inquire(1, 0, 0);
		RemoteDevice d = devices.get(0);
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
		}
		//TODO: protokoll mit anderen abstimmen
	}
	
	public static void main(String[] args){
		BtNxt bt = new BtNxt();
		bt.host();
	}
}
