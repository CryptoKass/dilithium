package org.dilithium.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Control {

	@FXML
	private JFXListView<transactions> transactionHistory;
	
	@FXML
	private ImageView QRCodePane;
	
    @FXML
    private JFXButton QRCodeButton, historyButton, logoutButton, shopButton, recieveButton;

    @FXML
    private Label Balance;
    //Balance.setText("$ 0");

    @FXML
    private JFXButton sendButton;
    
    @FXML
    void Send(ActionEvent event) {
     	try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Transaction.fxml"));
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

    @FXML
    void history(ActionEvent event) {
    	System.out.println("History dialog");
    	try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("History.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            //stage.setTitle("");
            stage.setScene(new Scene(root));  
            stage.show();
          }
		catch (Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void Close(ActionEvent event) {
    	// close the pane we are on
    	Node source = (Node) event.getSource(); 
    	Stage stage = (Stage) source.getScene().getWindow(); 
    	stage.close();
    	
    }
    
    @FXML
    void getShop(ActionEvent event) {
    	System.out.println("go online");
    	System.out.println("build a shop");
    	System.out.println("connect our coin");
    }
    
    @FXML
    void QRCode(ActionEvent event) {
    	try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("QRCode.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            //stage.setTitle("");
            stage.setScene(new Scene(root1));  
            generateQRCodeImage("sample", 500, 500, "./qrcodes/code0.png");
            //createImageView();
            
            stage.show();
          }
		catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    // this has to be moved
	private static void generateQRCodeImage(String text, int width, int height, String filePath) {
	    	QRCodeWriter qrCodeWriter = new QRCodeWriter();
	        ByteMatrix bitMatrix;
			try {
				bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
				Path path = FileSystems.getDefault().getPath(filePath);
		        File fil = new File(path.toString());
				MatrixToImageWriter.writeToFile(bitMatrix, "PNG", fil);
				String url = "file:///Users/Owner/Documents/School/CS210/GUI/src/application/qrcodes/code0.png";
				System.out.println(url);
				//Image image = new Image(url.toString(), true);
				//ImageView imageView = createImageView();
				
				
			} catch (WriterException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	
private ImageView createImageView(ReadOnlyDoubleProperty widthProperty) {
	ImageView imageView = new ImageView();
	imageView.setPreserveRatio(true);
	imageView.fitWidthProperty().bind(widthProperty);	
	return imageView;
}
	
	//@FXML
	//public static void Initialize() {
		//double xOffset, yOffset;
		//Window root = new Window();
		//root.setOnMousePressed(new EventHandler<MouseEvent>() {
            //@Override
            //public void handle(MouseEvent event) {
                //xOffset = event.getSceneX();
                //yOffset = event.getSceneY();
            //}
        //});
        //root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            //@Override
            //public void handle(MouseEvent event) {
                //stage.setX(event.getScreenX() - xOffset);
                //stage.setY(event.getScreenY() - yOffset);
            //}
        //});
	//}
}