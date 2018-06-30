//package org.dilithium.application;
package org.dilithium.application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private Button LoginButton, signupButton, closeButton, registerButton;

    @FXML
    private PasswordField password;
    
    @FXML
    void Login(ActionEvent event) throws IOException {
    	int fail = 0;
    	String user = username.getText();	
    	String pass = password.getText();
    	// no one uses real passwords for these dev things, do they?
    	String userSHA = getSHA512Password(user, "axdf"); 	// login Julia	//both hashes are salted
    	String passSHA = getSHA512Password(pass, "axdf");	// pass  Rocks
    	
    	if(userSHA.equals("2f4135f8736fade3fc983fb693bd36e21be8f107fbf3281fa5d6af6b627af74d60c79264635ea5b999f68a88ca76cee19064e3d7fdef72f4747005f6c47ee9dc") 
    			&& passSHA.equals("1b254274548d34914ccb63a93dc86cd81f8bcb0adf61a62ae7ecd0c3c5ad9d3efa2c64a7fea0dc0f5443b105cb6de8bb5d29687f2c14d772841abd3005a36fc3")) {
    		System.out.println("Login Successful!");
    		try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("org/dilithium/application/src/DilithiumWallet.fxml"));
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
    	else {
    		System.out.println("Wrong username / password");
    		fail++;
    		if(fail >=5) {
    			System.out.println("Goodbye.");
    			Platform.exit();
    		}
    	}
    }

    @FXML
    void Preview(MouseEvent event) {
    	System.out.println("StartSlideshow();");
    }
    
    @FXML
    void Close(ActionEvent event) {
    	// close the pane
    	Stage stage = (Stage) closeButton.getScene().getWindow();
    	stage.close();
    }
    
    @FXML
    void Signup(ActionEvent event) {
    	System.out.println("Calling signup window...");
    	try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            // stage.setTitle("Register");
            stage.setScene(new Scene(root));  
            stage.show();
          }
		catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    private String getSHA512Password(String passwordToHash, String salt){
    String generatedPassword = null;
        try {
             MessageDigest md = MessageDigest.getInstance("SHA-512");
             md.update(salt.getBytes(StandardCharsets.UTF_8));
             byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
             StringBuilder sb = new StringBuilder();
             for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
             }
             generatedPassword = sb.toString();
            } 
           catch (NoSuchAlgorithmException e){
            e.printStackTrace();
           }
        return generatedPassword;
    }
      
    @FXML
    void Reg(ActionEvent event) {
    	System.out.println("Account created");
    }
}
