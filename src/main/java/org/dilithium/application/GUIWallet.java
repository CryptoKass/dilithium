//package org.dilithium.application;
package org.dilithium.application;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;


public class GUIWallet {

	private JFrame frmDilithiumWallet;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Security.addProvider(new BouncyCastleProvider());
		//String walletPeek = wallet.toString();
		//System.out.println(walletPeek);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIWallet window = new GUIWallet();
					window.frmDilithiumWallet.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIWallet() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDilithiumWallet = new JFrame();
		frmDilithiumWallet.setTitle("Dilithium test wallet version-dev");
		frmDilithiumWallet.setBounds(100, 100, 850, 600);
		frmDilithiumWallet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDilithiumWallet.getContentPane().setLayout(null);
		
		JLabel lblBalance = new JLabel("Balance:");
		lblBalance.setBounds(50, 50, 56, 16);
		frmDilithiumWallet.getContentPane().add(lblBalance);
		
		//Context context = getContext();
		
		//BigInteger balance = Wallet.getBalance(context);
		int balance = 0;
		JLabel labelBalance = new JLabel("$" + balance);
		labelBalance.setForeground(SystemColor.textHighlight);
		labelBalance.setBackground(SystemColor.activeCaption);
		labelBalance.setFont(new Font("Tahoma", Font.BOLD, 22));
		labelBalance.setBounds(50, 74, 160, 62);
		frmDilithiumWallet.getContentPane().add(labelBalance);
		
		JButton btnNewTransaction = new JButton("Send Payment");
		btnNewTransaction.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
			System.out.println("new transaction");	
			//ECKey key = new ECKey();
			
			GUITx.main(null);
			
			//Transaction tx = generateTransaction(value, recipient, networkID, context, axiom);
			//BigInteger balance = Wallet.getBalance(context));
			}
		});
		btnNewTransaction.setBounds(83, 472, 150, 25);
		frmDilithiumWallet.getContentPane().add(btnNewTransaction);
		
		JButton button = new JButton("Generate QR Code");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			QRCodeGUI.main(null);
			}
		});
		button.setBounds(317, 472, 150, 25);
		frmDilithiumWallet.getContentPane().add(button);
		
		JButton button_1 = new JButton("Feature");
		button_1.setBounds(563, 472, 150, 25);
		frmDilithiumWallet.getContentPane().add(button_1);
	}     

}
