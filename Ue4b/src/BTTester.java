import java.io.IOException;
import java.util.Scanner;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

public class BTTester {
	private NXTInfo[] nxts;
	private NXTConnector conn = new NXTConnector();
	private Scanner sc = new Scanner(System.in);

	public void start() throws IOException {
		System.out.println("suche nxts");
		nxts = conn.search("", null, NXTCommFactory.BLUETOOTH);
		for (int i = 0; i < nxts.length; i++) {
			System.out.println("i: " + nxts[i].name + " "
					+ nxts[i].deviceAddress);
		}
		System.out.println(nxts.length+" clients gefunden.");
		System.out.println("welcher client?");
		int index = Integer.parseInt(sc.nextLine());
		if (!conn.connectTo(nxts[index], NXTComm.PACKET)) {
			System.out.println("konnte nicht verbinden");
			System.exit(1);
		}
		NXTComm comm = conn.getNXTComm();
		while (true) {
			System.out.println("Send(0) or Read(1) ?");
			index = Integer.parseInt(sc.nextLine());
			switch (index) {
			case 0:
				System.out.println("msg:");
				byte[] msg = {Byte.parseByte(sc.nextLine())};
				comm.write(msg);
				break;
			case 1:
				byte[] reply = comm.read();
				System.out.println(reply[0]);
				break;
			default:
				System.out.println("Falsche eingabe");
				break;
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		new BTTester().start();
	}
}
