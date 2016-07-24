package com.cjburkey.nilplace;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Prgm {
	
	public static final Scene launchInstaller(Stage s, String infoFileUrl) {
		Nilplace.log("Launching installer.");
		VBox root = new VBox();
		Scene scene = new Scene(root);
		return scene;
	}
	
	public static final Scene launchCreator(Stage s) {
		Nilplace.log("Launching creator.");
		VBox root = new VBox();
		Scene scene = new Scene(root);
		return scene;
	}
	
}