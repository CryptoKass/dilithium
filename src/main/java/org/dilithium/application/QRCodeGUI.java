package org.dilithium.application;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.SystemColor;

public class QRCodeGUI {

	private static JFrame frmQRCode;
	private JTextField textFieldTo;
	private JTextField textField;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					QRCodeGUI window = new QRCodeGUI();
					window.frmQRCode.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public QRCodeGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQRCode = new JFrame();
		frmQRCode.setBackground(SystemColor.activeCaption);
		frmQRCode.setTitle("QR Code Generator");
		frmQRCode.setBounds(100, 100, 570, 800);
		frmQRCode.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmQRCode.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(23, 170, 500, 500);
		frmQRCode.getContentPane().add(panel);
		panel.setBackground(Color.GRAY); 
	    ImageIcon icon = new ImageIcon("Code0.png"); 
	    JLabel label = new JLabel(); 
	    label.setIcon(icon); 
	    panel.add(label);
	    
		JButton btnSubmit = new JButton("Generate");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
			int filenum = 0;
			ImageIcon icon;
			File path;
			String recipient = textFieldTo.getText();
			String walletStr = "10101010101010101010101010101010101010101";	
			String text = "https://Julia.coins:555 " + "100 zenny " + "From: " + walletStr + " To: " + recipient;
			String pathStr = "./qrcodes/code0.png";
			path = new File(pathStr);
			boolean exists = path.exists();
			while(exists==true) {
				filenum++;	
				pathStr = "./qrcodes/code" + filenum + ".png";	
				path = new File("./qrcodes/code" + filenum + ".png");
				exists = path.exists();
			}
			path = new File(pathStr);
			generateQRCodeImage(text, 500, 500, pathStr);	
			icon = new ImageIcon(pathStr); 
		    label.setIcon(icon);
			panel.repaint();
						
			}
		});
		
		btnSubmit.setBounds(276, 715, 97, 25);
		frmQRCode.getContentPane().add(btnSubmit);
		
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmQRCode.dispose();
			}
		});
		btnCancel.setBounds(144, 715, 97, 25);
		frmQRCode.getContentPane().add(btnCancel);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setBounds(23, 13, 116, 16);
		frmQRCode.getContentPane().add(lblTo);
		
		textFieldTo = new JTextField();
		textFieldTo.setBounds(23, 42, 497, 22);
		frmQRCode.getContentPane().add(textFieldTo);
		textFieldTo.setColumns(10);
		
		JLabel labelFrom = new JLabel("From:");
		labelFrom.setBounds(23, 89, 116, 16);
		frmQRCode.getContentPane().add(labelFrom);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(23, 118, 497, 22);
		frmQRCode.getContentPane().add(textField);
	}

	public JFrame getFrmLogin() {
		return frmQRCode;
	}
	
	 private static void generateQRCodeImage(String text, int width, int height, String filePath) {
	    	QRCodeWriter qrCodeWriter = new QRCodeWriter();
	        ByteMatrix bitMatrix;
			try {
				bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
				Path path = FileSystems.getDefault().getPath(filePath);
		        File fil = new File(path.toString());
				MatrixToImageWriter.writeToFile(bitMatrix, "PNG", fil);
			
			} catch (WriterException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }	 
}