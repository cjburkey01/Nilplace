package com.cjburkey.nilplace;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Preload extends Preloader {
	
	ProgressBar bar;
	Stage stage;
	
	private Scene createPreloaderScene() {
		bar = new ProgressBar();
		BorderPane p = new BorderPane();
		p.setCenter(bar);
		return new Scene(p, 300, 150);
	}
	
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.stage.setScene(createPreloaderScene());
		this.stage.setResizable(false);
		this.stage.setTitle("Loading...");
		this.stage.show();
	}
	
	public void handleProgressNotification(ProgressNotification pn) {
		bar.setProgress(pn.getProgress());
	}
	
	public void handleStateChangeNotification(StateChangeNotification evt) {
		if(evt.getType() == StateChangeNotification.Type.BEFORE_START) {
			this.stage.hide();
		}
	}
	
}