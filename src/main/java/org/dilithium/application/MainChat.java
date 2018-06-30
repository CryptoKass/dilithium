package org.dilithium.application;

//import com.sun.prism.paint.Color;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class MainChat extends Application {
    private static double xOffset;
    private static double yOffset;


    @Override
    public void start(Stage myStage) throws Exception {
        try {
        
        	Parent root = FXMLLoader.load(getClass().getResource("PaperChat.fxml"));
            Scene scene = new Scene(root);
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

    // check messages initializes from main
    
    public static void main(String[] args) {
        launch(args);
    }

}
