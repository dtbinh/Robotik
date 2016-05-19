import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.RemoteDevice;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Main {

	private JFrame frame;
	private BTConnection conn;
	private ArrayList<RemoteDevice> devices;
	
	private JButton btnSubmit;
	private JButton btnExit;
	private JButton btnConnect;
	
	DataOutputStream out;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
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
	public Main() {
		initialize();
	}
	
	public ArrayList<RemoteDevice> getDevices(){
		this.devices = Bluetooth.inquire(1, 5, 0);
		return this.devices;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 220);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblKp = new JLabel("Kp");
		GridBagConstraints gbc_lblKp = new GridBagConstraints();
		gbc_lblKp.anchor = GridBagConstraints.WEST;
		gbc_lblKp.insets = new Insets(5, 5, 5, 5);
		gbc_lblKp.gridx = 0;
		gbc_lblKp.gridy = 0;
		frame.getContentPane().add(lblKp, gbc_lblKp);
		
		JSpinner spKp = new JSpinner();
		spKp.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(1)));
		GridBagConstraints gbc_spKp = new GridBagConstraints();
		gbc_spKp.fill = GridBagConstraints.HORIZONTAL;
		gbc_spKp.insets = new Insets(5, 5, 5, 0);
		gbc_spKp.gridx = 1;
		gbc_spKp.gridy = 0;
		frame.getContentPane().add(spKp, gbc_spKp);
		
		JLabel lblTn = new JLabel("Tn");
		GridBagConstraints gbc_lblTn = new GridBagConstraints();
		gbc_lblTn.anchor = GridBagConstraints.WEST;
		gbc_lblTn.insets = new Insets(5, 5, 5, 5);
		gbc_lblTn.gridx = 0;
		gbc_lblTn.gridy = 1;
		frame.getContentPane().add(lblTn, gbc_lblTn);
		
		JSpinner spTn = new JSpinner();
		spTn.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(1)));
		GridBagConstraints gbc_spTn = new GridBagConstraints();
		gbc_spTn.fill = GridBagConstraints.HORIZONTAL;
		gbc_spTn.insets = new Insets(5, 5, 5, 0);
		gbc_spTn.gridx = 1;
		gbc_spTn.gridy = 1;
		frame.getContentPane().add(spTn, gbc_spTn);
		
		JLabel lblTv = new JLabel("Tv");
		GridBagConstraints gbc_lblTv = new GridBagConstraints();
		gbc_lblTv.anchor = GridBagConstraints.WEST;
		gbc_lblTv.insets = new Insets(5, 5, 5, 5);
		gbc_lblTv.gridx = 0;
		gbc_lblTv.gridy = 2;
		frame.getContentPane().add(lblTv, gbc_lblTv);
		
		JSpinner spTv = new JSpinner();
		spTv.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(1)));
		GridBagConstraints gbc_spTv = new GridBagConstraints();
		gbc_spTv.fill = GridBagConstraints.HORIZONTAL;
		gbc_spTv.insets = new Insets(5, 5, 5, 0);
		gbc_spTv.gridx = 1;
		gbc_spTv.gridy = 2;
		frame.getContentPane().add(spTv, gbc_spTv);
		
		JLabel lblBasespeed = new JLabel("basespeed");
		GridBagConstraints gbc_lblBasespeed = new GridBagConstraints();
		gbc_lblBasespeed.anchor = GridBagConstraints.WEST;
		gbc_lblBasespeed.insets = new Insets(5, 5, 5, 5);
		gbc_lblBasespeed.gridx = 0;
		gbc_lblBasespeed.gridy = 3;
		frame.getContentPane().add(lblBasespeed, gbc_lblBasespeed);
		
		JSpinner spSpeed = new JSpinner();
		spSpeed.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spSpeed = new GridBagConstraints();
		gbc_spSpeed.fill = GridBagConstraints.HORIZONTAL;
		gbc_spSpeed.insets = new Insets(5, 5, 5, 0);
		gbc_spSpeed.gridx = 1;
		gbc_spSpeed.gridy = 3;
		frame.getContentPane().add(spSpeed, gbc_spSpeed);
		
		JLabel lblBrakefactor = new JLabel("brakefactor");
		GridBagConstraints gbc_lblBrakefactor = new GridBagConstraints();
		gbc_lblBrakefactor.anchor = GridBagConstraints.WEST;
		gbc_lblBrakefactor.insets = new Insets(5, 5, 5, 5);
		gbc_lblBrakefactor.gridx = 0;
		gbc_lblBrakefactor.gridy = 4;
		frame.getContentPane().add(lblBrakefactor, gbc_lblBrakefactor);
		
		JSpinner spBrake = new JSpinner();
		spBrake.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spBrake = new GridBagConstraints();
		gbc_spBrake.insets = new Insets(5, 5, 5, 0);
		gbc_spBrake.fill = GridBagConstraints.HORIZONTAL;
		gbc_spBrake.gridx = 1;
		gbc_spBrake.gridy = 4;
		frame.getContentPane().add(spBrake, gbc_spBrake);
		
		JPanel pnlButtons = new JPanel();
		GridBagConstraints gbc_pnlButtons = new GridBagConstraints();
		gbc_pnlButtons.gridwidth = 2;
		gbc_pnlButtons.fill = GridBagConstraints.BOTH;
		gbc_pnlButtons.gridx = 0;
		gbc_pnlButtons.gridy = 5;
		frame.getContentPane().add(pnlButtons, gbc_pnlButtons);
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		btnConnect = new JButton("Connect");
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ConnectDialog dialog = new ConnectDialog(Main.this);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setModal(true);
				dialog.setVisible(true);
				int index = dialog.getIndex();
				if (index == -1) {
					return;
				}
				conn = Bluetooth.connect(Main.this.devices.get(index));
				out = conn.openDataOutputStream();
				btnConnect.setEnabled(false);
				btnSubmit.setEnabled(true);
			}
		});
		pnlButtons.add(btnConnect);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					out.writeDouble((double) spKp.getValue());
					out.writeDouble((double) spTn.getValue());
					out.writeDouble((double) spTv.getValue());
					out.writeInt((int) spSpeed.getValue());
					out.writeInt((int) spBrake.getValue());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSubmit.setEnabled(false);
		pnlButtons.add(btnSubmit);
		
		btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (conn != null) {
					conn.close();
				}
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		pnlButtons.add(btnExit);
	}

}
