package com.cjburkey.nilplace;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import com.cjburkey.nilplace.local.Localization;
import com.cjburkey.nilplace.scene.LaunchInstaller;
import com.cjburkey.nilplace.scene.LaunchMain;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Nilplace extends Application {
	
	public static final String dir = System.getProperty("user.home") +
			File.separator + "nilplace" + File.separator;
	
	private String downloadInfoFile = null;
	public static Stage stage;
	
	public static final void resetScene(String error) {
		stage.setScene(LaunchMain.go(stage));
		if(error != null) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle(Localization.getLocalized("basicError"));
			a.setContentText(error);
			a.showAndWait();
		}
	}
	
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
	
	public static final void err(Throwable t, boolean display) {
		err(Localization.getLocalized("basicError"));
		err(Localization.getLocalized("mainError", t.getMessage()));
		err(Localization.getLocalized("beginErrRep"));
		t.printStackTrace();
		err(Localization.getLocalized("endErrRep"));
		
		if(display) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(Localization.getLocalized("basicError"));
			alert.setHeaderText(Localization.getLocalized("clickShowDetails"));
			alert.setContentText(t.getMessage());
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label(Localization.getLocalized("stacktrace"));
			
			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			BorderPane expContent = new BorderPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.setCenter(textArea);
			expContent.setTop(label);
			
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}
	}
	
	public static final void err(Throwable e) {
		err(e, true);
	}
	
	public void start(Stage s) {
		stage = s;
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> { err(e); });
		
		Parameters ps = this.getParameters();
		Set<Entry<String, String>> args = ps.getNamed().entrySet();
		for(Entry<String, String> arg : args) {
			String key = arg.getKey();
			String value = arg.getValue();
			
			if(key.equalsIgnoreCase("downloadInfoFile")) {
				log(Localization.getLocalized("argumentValid", key, value));
				downloadInfoFile = value.trim();
			}
		}
		
		if(downloadInfoFile == null) {
			log(Localization.getLocalized("noMainInfo"));
			s.setScene(LaunchMain.go(s));
		} else {
			log(Localization.getLocalized("mainInfoFound"));
			s.setScene(LaunchInstaller.go(s, downloadInfoFile));
		}
		
		s.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
		s.setHeight(Screen.getPrimary().getVisualBounds().getHeight() / 2);
		s.centerOnScreen();
		s.setTitle(Localization.getLocalized("windowTitle"));
		s.setResizable(false);
		s.show();
		s.getIcons().add(new Image("img/icon.png"));
		
		log(Localization.getLocalized("done"), false);
	}
	
	public static void main(String[] args) {
		Localization.loadLocalizations("en");
		OS.init();
		log(Localization.getLocalized("launching"), false);
		launch(args);
	}
	
	public static final void install(Stage s, String durl) {
		try {
			new URL(durl);
			Scene sc = LaunchInstaller.go(s, durl);
			if(sc != null) s.setScene(sc);
		} catch(Exception e) { err(e); }
	}
	
}