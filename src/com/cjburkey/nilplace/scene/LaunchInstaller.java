package com.cjburkey.nilplace.scene;

import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.OS;
import com.cjburkey.nilplace.install.LoadData;
import com.cjburkey.nilplace.local.Localization;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LaunchInstaller {
	
	public static final Scene go(Stage s, String infoFileUrl) {
		Nilplace.log("Launching installer.");
		
		s.setOnCloseRequest(e -> { e.consume(); });
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/square.css");
		Nilplace.log(Localization.getLocalized("readingFile"), false);
		if(LoadData.load(infoFileUrl)) {
			if(LoadData.getName() != null) {
				Nilplace.log(Localization.getLocalized("finishReadingFile"), false);
				
				if((LoadData.winLaunch == null && OS.current.equals(OS.WIN)) || 
						(LoadData.macLaunch == null && OS.current.equals(OS.MAC)) ||
						(LoadData.linLaunch == null && OS.current.equals(OS.LIN))) {
					Nilplace.resetScene(Localization.getLocalized("noOs", OS.current));
					return null;
				}
				
				HBox box = new HBox();
				
				Label text = new Label(Localization.getLocalized("installerShow",
						LoadData.getName()));
				Button go = new Button(Localization.getLocalized("continue"));
				Button cancel = new Button(Localization.getLocalized("cancel"));
				
				box.getChildren().addAll(cancel, go);
				box.setSpacing(10);
				box.setPadding(new Insets(10));
				box.setAlignment(Pos.CENTER_RIGHT);
				
				text.setWrapText(true);
				
				root.setPadding(new Insets(10));
				root.setTop(new Label(LoadData.getName()));
				root.setCenter(text);
				root.setBottom(box);
				
				cancel.setOnAction(e -> { Nilplace.resetScene(null); });
				go.setOnAction(e -> { LoadData.executeScript();
					s.setScene(StartInstallScreen.go(s)); });
				return scene;
			}
		}
		return LaunchMain.go(s);
	}
	
}