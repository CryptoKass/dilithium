package org.dilithium.application;
import java.awt.EventQueue;

import javax.swing.JFrame;

//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.awt.event.ActionEvent;

public class GUITx {

	private JFrame frmSendMoney;
	private JTextField textFieldTo;
	private JTextField txtFieldAmount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Security.addProvider(new BouncyCastleProvider());
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUITx window = new GUITx();
					window.frmSendMoney.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUITx() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSendMoney = new JFrame();
		frmSendMoney.setTitle("Send Money");
		frmSendMoney.setBounds(100, 100, 500, 300);
		frmSendMoney.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmSendMoney.getContentPane().setLayout(null);
		
		JLabel lblNewTransaction = new JLabel("New Transaction");
		lblNewTransaction.setBounds(12, 13, 130, 16);
		frmSendMoney.getContentPane().add(lblNewTransaction);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setBounds(40, 63, 56, 16);
		frmSendMoney.getContentPane().add(lblTo);
		
		// public Transaction(byte[] privateKey, byte[] nonce, byte[] value, byte[] data, byte[] recipient, byte networkId, byte[] sender, Axiom axiom){
		    
		String myWalletAddress = "22921C19002CD1DD62AB3D5A146ECCC5C8E9AAECCCE8D95F12854D86D2747FC9"; // fake address
		
		JLabel lblFrom = new JLabel("From:");
		lblFrom.setBounds(40, 110, 56, 16);
		frmSendMoney.getContentPane().add(lblFrom);
		
		JLabel lblAmount = new JLabel("Amount:");
		lblAmount.setBounds(40, 165, 56, 16);
		frmSendMoney.getContentPane().add(lblAmount);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			String valStr = txtFieldAmount.getText();
			BigInteger value = BigInteger.valueOf(Long.parseLong(valStr));
			//ECKey key = Wallet.getKeyPair();
			byte[] recipient = textFieldTo.getText().getBytes();
			//Context context = new Context();
			//Axiom axiom = new Axiom();
			
			//byte[] networkId = being phased out TOMORROW
			
			//Transaction tx = Wallet.generateTransaction(value, recipient, networkID, context, axiom);
			// public Transaction generateTransaction(BigInteger value, byte[] recipient, byte networkId, Context context, Axiom axiom){
		     
			}
		});
		btnSend.setBounds(276, 215, 97, 25);
		frmSendMoney.getContentPane().add(btnSend);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmSendMoney.dispose();
			}
		});
		btnCancel.setBounds(108, 215, 97, 25);
		frmSendMoney.getContentPane().add(btnCancel);
		
		textFieldTo = new JTextField();
		textFieldTo.setBounds(108, 57, 316, 22);
		frmSendMoney.getContentPane().add(textFieldTo);
		textFieldTo.setColumns(10);
		
		txtFieldAmount = new JTextField();
		txtFieldAmount.setBounds(108, 159, 316, 22);
		frmSendMoney.getContentPane().add(txtFieldAmount);
		txtFieldAmount.setColumns(10);
		
		JLabel lblFromAddress = new JLabel(myWalletAddress);
		lblFromAddress.setBounds(108, 106, 316, 25);
		frmSendMoney.getContentPane().add(lblFromAddress);
	}
}
