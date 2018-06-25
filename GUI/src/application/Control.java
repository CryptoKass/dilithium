package application;

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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class Control {

    @FXML
    private JFXButton shopButton;

    @FXML
    private JFXButton recieveButton;

    @FXML
    private JFXButton logoutButton;

    @FXML
    private JFXButton QRCodeButton;

    @FXML
    private Label Balance;
    // Balance.setText("$ 0");

    @FXML
    private JFXButton sendButton;
    
    @FXML
    void Send(ActionEvent event) {
    	System.out.println("Send dialog");
    }

    @FXML
    void Recieve(ActionEvent event) {
    	System.out.println("Recieve dialog");
    }

    @FXML
    void Close(ActionEvent event) {
    	// close the pane
    	System.out.println("goodbye.");
    	Node source = (Node) event.getSource(); 
    	Stage stage = (Stage) source.getScene().getWindow(); 
    	stage.close();
    
    	
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
            stage.show();
          }
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    void getShop(ActionEvent event) {
    	System.out.println("go online");
    	System.out.println("build a shop");
    	System.out.println("connect our coin");
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
	
	public static void Initialize() {
		double xOffset, yOffset;
		
		Window root = new Window();
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
	}
}