package com.cjburkey.nilplace.scene;

import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.install.LoadData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Done {
	
	public static final Scene go(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/square.css");
		
		Label top = new Label(LoadData.getName());
		VBox center = new VBox();
		HBox bottom = new HBox();
		
		Button finish = new Button("Close Installer.");
		
		bottom.getChildren().addAll(finish);
		bottom.setAlignment(Pos.CENTER_RIGHT);
		bottom.setSpacing(10);
		bottom.setPadding(new Insets(10));
		
		center.getChildren().addAll(new Label("The installation is complete!"));
		center.setAlignment(Pos.CENTER);
		center.setSpacing(10);
		center.setPadding(new Insets(10));
		center.setFillWidth(true);
		
		root.setPadding(new Insets(10));
		root.setTop(top);
		root.setCenter(center);
		root.setBottom(bottom);
		
		finish.setOnAction(e -> { Nilplace.resetScene(null); });
		
		return scene;
	}
	
}