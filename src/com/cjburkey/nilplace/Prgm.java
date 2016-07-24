package com.cjburkey.nilplace;

import com.cjburkey.nilplace.install.LoadData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Prgm {
	
	public static final Scene launchInstaller(Stage s, String infoFileUrl) {
		Nilplace.log("Launching installer.");
		
		BorderPane root = new BorderPane();
		//VBox root = new VBox();
		Scene scene = new Scene(root);
		Nilplace.log("Reading file.", false);
		LoadData.load(infoFileUrl);
		Nilplace.log("Finished reading file.", false);
		
		HBox box = new HBox();
		
		Label text = new Label("The following installer will install '" + LoadData.getName() +"'");
		Button go = new Button("Continue");
		Button cancel = new Button("Cancel");
		
		box.getChildren().addAll(cancel, go);
		box.setSpacing(10);
		box.setPadding(new Insets(10));
		box.setAlignment(Pos.CENTER_RIGHT);
		
		text.setWrapText(true);
		
		root.setPadding(new Insets(10));
		root.setTop(new Label(LoadData.getName()));
		root.setCenter(text);
		root.setBottom(box);
		
		cancel.setOnAction(e -> { go.setDisable(true); Platform.exit(); });
		go.setOnAction(e -> { s.setScene(startInstallScreen(s)); });
		return scene;
	}
	
	private static final Scene startInstallScreen(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		HBox box = new HBox();
		VBox center = new VBox();
		
		Label t = new Label(LoadData.getName());
		Button go = new Button("Continue");
		Button cancel = new Button("Cancel");
		
		Label l = new Label("This program will run the following commands:");
		TextArea te = new TextArea();
		te.setWrapText(false);
		te.setEditable(false);
		
		for(String ss : LoadData.getLines()) {
			te.appendText(ss.replaceAll("\\;", "") + "\n");
		}
		
		box.getChildren().addAll(cancel, go);
		center.getChildren().addAll(l, te);
		
		box.setAlignment(Pos.CENTER_RIGHT);
		box.setPadding(new Insets(10));
		box.setSpacing(10);
		
		center.setAlignment(Pos.CENTER);
		center.setPadding(new Insets(10));
		center.setSpacing(10);
		
		root.setPadding(new Insets(10));
		root.setTop(t);
		root.setBottom(box);
		root.setCenter(center);
		
		cancel.setOnAction(e -> { go.setDisable(true); Platform.exit(); });
		
		return scene;
	}
	
	public static final Scene launchCreator(Stage s) {
		Nilplace.log("Launching creator.");
		VBox root = new VBox();
		Scene scene = new Scene(root);
		return scene;
	}
	
}