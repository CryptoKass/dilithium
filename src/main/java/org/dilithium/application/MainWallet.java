//package org.dilithium.application;
package org.dilithium.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class MainWallet extends Application {
	private static double xOffset;
	private static double yOffset;

	@Override
	public void start(Stage myStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("org/dilithium/application/src/DilithiumWallet.fxml"));
			Scene scene = new Scene(root); 
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setOnMousePressed(new EventHandler<MouseEvent>() {	
			
			@Override
			public void handle(MouseEvent event) {
				xOffset = myStage.getX() - event.getScreenX();
				yOffset = myStage.getY() - event.getScreenY();
				}
			});
			scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					myStage.setX(event.getScreenX() + xOffset);
					myStage.setY(event.getScreenY() + yOffset);
				}
			});	
			
			myStage.setScene(scene);
			myStage.show();
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
