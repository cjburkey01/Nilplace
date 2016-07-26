package com.cjburkey.nilplace;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Nilplace extends Application {
	
	public static final String dir = System.getProperty("user.home") +
			File.separator + "nilplace" + File.separator;
	
	private String downloadInfoFile = null;
	
	public static final String getAppDir() {
		int num = ThreadLocalRandom.current().nextInt(0, 999999999 + 1);
		if(!new File(dir, num + "").exists()) {
			return dir + num;
		}
		return getAppDir();
	}
	
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
			s.setScene(Prgm.launchPrgm(s));
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
		s.getIcons().add(new Image("img/icon.png"));
		
		log("Done.", false);
	}
	
	public static void main(String[] args) {
		log("Launching", false);
		launch(args);
	}
	
}