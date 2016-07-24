package com.cjburkey.nilplace;

import java.util.Map.Entry;
import java.util.Set;
import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Nilplace extends Application {
	
	private String downloadInfoFile = null;
	
	public static final void log(Object msg) {
		log(msg, true);
	}
	
	public static final void log(Object msg, boolean tab) {
		System.out.println(((tab) ? "\t" : "") + msg);
	}
	
	public static final void err(Object msg) {
		System.err.println(msg);
	}
	
	public static final void err(Throwable t) {
		err("An error occurred!");
		err("Main error: '" + t.getMessage() + "'");
		err("--[ BEGIN ERR REPORT STACKTRACE ]--");
		t.printStackTrace();
		err("--[ END ERR REPORT STACKTRACE ]--");
	}
	
	public void start(Stage s) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> { err(e); });
		
		Parameters ps = this.getParameters();
		Set<Entry<String, String>> args = ps.getNamed().entrySet();
		for(Entry<String, String> arg : args) {
			String key = arg.getKey();
			String value = arg.getValue();
			
			if(key.equalsIgnoreCase("downloadInfoFile")) {
				log("Found valid argument: '" + key + "' = '" + value + "'.");
				downloadInfoFile = value.trim();
			}
		}
		
		if(downloadInfoFile == null) {
			log("Didn't find main info file.");
			s.setScene(Prgm.launchCreator(s));
		} else {
			log("Found main info file.");
			s.setScene(Prgm.launchInstaller(s, downloadInfoFile));
		}
		
		s.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
		s.setHeight(Screen.getPrimary().getVisualBounds().getHeight() / 2);
		s.centerOnScreen();
		s.setTitle("Nilplace");
		s.setResizable(false);
		s.show();
		
		log("Done.", false);
	}
	
	public static void main(String[] args) {
		log("Launching", false);
		launch(args);
	}
	
}