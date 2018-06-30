package org.dilithium.application;
//package application;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bouncycastle.util.encoders.Hex;
import org.dilithium.crypto.ecdsa.ECKey;
import org.dilithium.network.Peer2Peer;

import java.lang.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import com.jfoenix.controls.JFXScrollPane;


public class PaperController {

    @FXML
    private JFXTextField port;

    @FXML
    private JFXTextField ipAddr;

    @FXML
    private JFXButton connectButton;
	
    @FXML
    private JFXTextField msg_text;

    @FXML
    private JFXTextArea textView;

    @FXML
    private JFXButton msgSend;

    @FXML
    void Send(ActionEvent event) {
    	String MessageQue = "";
    	String message = "me: " + msg_text.getText();
        msg_text.setText("");
        textView.appendText(message + "\n");
        // lock the thread & tx-ing time!
        // org.dilithium.network.Peer.  net.broadcast(0xF0, sending.getBytes("UTF-8"));
        
    }

    @FXML
    public void onEnter(ActionEvent ae){
    	String MessageQue = "";
    	String message = "me: " + msg_text.getText();
        msg_text.setText("");
        textView.appendText(message + "\n");
    
    }
    
    @FXML
    public void ConnectDlg(ActionEvent event) {
    	try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PaperConnect.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.setTitle("");
        stage.setScene(new Scene(root1)); 
        stage.show();
      }
	catch (Exception e) {
		e.printStackTrace();
	}
    	
    }
    
    public void Connect(ActionEvent event) {
    	String ip = ipAddr.getText();
    	String portStr = port.getText();
    	int portNum = Integer.parseInt(portStr);
    	
    	System.out.println("Connecting ...");
    	
    	try {
    	ECKey key = new ECKey();
        System.out.println(Hex.toHexString(key.getAddress()));
        Peer2Peer net = new Peer2Peer(40424, key, 6, 10);
        net.start();
		Peer2Peer.connect(ip, portNum); 
        System.out.println("Connected");
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	// close the pane we are on
    	Node source = (Node) event.getSource(); 
    	Stage stage = (Stage) source.getScene().getWindow(); 
    	stage.close();
    }
    
    @FXML
    void Close(ActionEvent event) {
    	// close the pane we are on
    	Node source = (Node) event.getSource(); 
    	Stage stage = (Stage) source.getScene().getWindow(); 
    	stage.close();
    	
    }
    
    public void put(String key, String message) {
    	ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock writeLock = lock.writeLock();
    	try {
    		writeLock.lock();
    		// send encrypted message
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	finally {
    		writeLock.unlock();
    	}
    }
}
