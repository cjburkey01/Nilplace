package com.cjburkey.nilplace;

import java.util.Map.Entry;
import java.util.Set;

import com.cjburkey.nilplace.nilscript.ReadScript;

import javafx.application.Application;
import javafx.stage.Stage;

public class Nilplace extends Application {
	
	private String downloadInfoFile = null;
	
	public static final void log(Object msg) {
		log(msg, true);
	}
	
	public static final void log(Object msg, boolean tab) {
		System.out.println(((tab) ? "\t" : "") + msg);
	}
	
	public void start(Stage s) {
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
		
		s.sizeToScene();
		s.centerOnScreen();
		s.setTitle("Nilplace");
		s.setResizable(false);
		s.show();
		
		log("Done.", false);
		
		ReadScript.read(
				"DOWNLOAD(http://cjburkey.com/,/Users/cjburkey/Desktop/test.zip);",
				"EXTRACT(/Users/cjburkey/Desktop/test.zip,/Users/cjburkey/Desktop/test);",
				"CLONE(/Users/cjburkey/Desktop/test.zip,/Goodbye);",
				"DELETE(/Users/cjburkey/Desktop/test.zip);"
		);
	}
	
	public static void main(String[] args) {
		log("Launching", false);
		launch(args);
	}
	
}