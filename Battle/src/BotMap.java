import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

public class BotMap {

	private JFrame frame;
	private BotMapPanel pnlBotMap;
	private JScrollPane scpScroll;
	private JTextArea txtLog;
	private JButton btConnect;
	
	private NXTConnector conn;
	private NXTComm comm;
	private ListenerThread thread;
	
	private class ListenerThread extends Thread {
		private boolean running = true;
		private byte[] data;
		String message;
		
		public void run() {
			log("Start listening...");
			while (running) {
				try {
					data = comm.read();
					message = new String(data);
					if (message.startsWith("pos:")) {
						String[] splits = message.substring(4).split(";");
						pnlBotMap.setPosition(
								Integer.parseInt(splits[0]),
								Integer.parseInt(splits[1]),
								Integer.parseInt(splits[2]));
					}
					else {
						log(message);
					}
				} catch (IOException e) {
					log("IOException occured:");
					log(e.getMessage());
					running = false;
				}
			}
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BotMap window = new BotMap();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BotMap() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		conn = new NXTConnector();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		pnlBotMap = new BotMapPanel();
		pnlBotMap.setBackground(Color.WHITE);
		GridBagConstraints gbc_pnlBotMap = new GridBagConstraints();
		gbc_pnlBotMap.insets = new Insets(5, 5, 5, 5);
		gbc_pnlBotMap.fill = GridBagConstraints.BOTH;
		gbc_pnlBotMap.gridx = 0;
		gbc_pnlBotMap.gridy = 0;
		frame.getContentPane().add(pnlBotMap, gbc_pnlBotMap);
		
		scpScroll = new JScrollPane();
		GridBagConstraints gbc_scpScroll = new GridBagConstraints();
		gbc_scpScroll.weighty = 0.25;
		gbc_scpScroll.insets = new Insets(5, 5, 5, 5);
		gbc_scpScroll.fill = GridBagConstraints.BOTH;
		gbc_scpScroll.gridx = 0;
		gbc_scpScroll.gridy = 1;
		frame.getContentPane().add(scpScroll, gbc_scpScroll);
		
		txtLog = new JTextArea();
		txtLog.setText("");
		scpScroll.setViewportView(txtLog);
		
		btConnect = new JButton("Connect");
		btConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				NXTInfo[] devices = conn.search("Kibi", null, NXTCommFactory.BLUETOOTH);
				if (devices.length > 1) {
					log("Found more devices with name 'Kibi'. Aborting...");
				}
				else if (devices.length == 0) {
					log("Found no compatible bluetooth NXTs. Aborting...");
				}
				else {
					log("'Kibi' found. Connecting...");
					conn.connectTo(devices[0], NXTComm.PACKET);
					comm = conn.getNXTComm();
					log("Connected!");
					thread = new ListenerThread();
					thread.start();
					btConnect.setEnabled(false);
				}
			}
		});
		GridBagConstraints gbc_btConnect = new GridBagConstraints();
		gbc_btConnect.insets = new Insets(5, 5, 5, 5);
		gbc_btConnect.fill = GridBagConstraints.BOTH;
		gbc_btConnect.gridx = 0;
		gbc_btConnect.gridy = 2;
		frame.getContentPane().add(btConnect, gbc_btConnect);
	}

	private void log(String message) {
		txtLog.setText(txtLog.getText() + "\n" + message);
	}
}
