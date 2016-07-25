package com.cjburkey.nilplace;

import com.cjburkey.nilplace.file.CloneFile;
import com.cjburkey.nilplace.file.DeleteFile;
import com.cjburkey.nilplace.file.DownFile;
import com.cjburkey.nilplace.file.ExtractFile;
import com.cjburkey.nilplace.install.LoadData;
import com.cjburkey.nilplace.install.Progs;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Prgm {
	
	public static ProgressBar prg;
	public static ProgressBar total;
	public static Label totalProg;
	public static Label currentFile;
	
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
		go.setOnAction(e -> { LoadData.executeScript(); s.setScene(startInstallScreen(s)); });
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
		
		te.appendText("Downloads:\n");
		if(Progs.downloads.size() > 0) {
			for(DownFile f : Progs.downloads) {
				te.appendText("\t- " + f.url + "\n");
			}
		} else {
			te.appendText("\t- None\n");
		}
		
		te.appendText("\nExtractions:\n");
		if(Progs.extractions.size() > 0) {
			for(ExtractFile f : Progs.extractions) {
				te.appendText("\t- " + f.input + "\n");
			}
		} else {
			te.appendText("\t- None\n");
		}
		
		te.appendText("\nFile copies:\n");
		if(Progs.clones.size() > 0) {
			for(CloneFile f : Progs.clones) {
				te.appendText("\t- " + f.input + "\n");
			}
		} else {
			te.appendText("\t- None\n");
		}
		
		te.appendText("\nFile deletions:\n");
		if(Progs.deletions.size() > 0) {
			for(DeleteFile f : Progs.deletions) {
				te.appendText("\t- " + f.delete + "\n");
			}
		} else {
			te.appendText("\t- None\n");
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
		go.setOnAction(e -> { s.setScene(loadingScene(s)); });
		
		te.setScrollTop(0);
		te.setScrollLeft(0);
		
		return scene;
	}
	
	public static void done() {
		loadingGo.setDisable(false);
		loadingCancel.setDisable(true);
	}
	
	private static Button loadingGo;
	private static Button loadingCancel;
	private static final Scene loadingScene(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/progBar.css");
		
		Label top = new Label(LoadData.getName());
		VBox center = new VBox();
		HBox bottom = new HBox();
		
		loadingCancel = new Button("Cancel");
		loadingGo = new Button("Continue");
		total = new ProgressBar();
		prg = new ProgressBar();
		totalProg = new Label();
		currentFile = new Label();
		
		loadingGo.setDisable(true);
		
		bottom.getChildren().addAll(loadingCancel, loadingGo);
		bottom.setAlignment(Pos.CENTER_RIGHT);
		bottom.setSpacing(10);
		bottom.setPadding(new Insets(10));
		
		center.getChildren().addAll(totalProg, total, prg, currentFile);
		center.setAlignment(Pos.CENTER);
		center.setSpacing(10);
		center.setPadding(new Insets(10));
		
		total.setMaxWidth(Double.MAX_VALUE);
		prg.setMaxWidth(Double.MAX_VALUE);
		center.setFillWidth(true);
		
		root.setPadding(new Insets(10));
		root.setTop(top);
		root.setCenter(center);
		root.setBottom(bottom);
		
		loadingCancel.setOnAction(e -> { Progs.cancel = true; Platform.exit(); });
		loadingGo.setOnAction(e -> { s.setScene(done(s)); });
		
		Progs.go();
		
		return scene;
	}
	
	private static final Scene done(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
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
		
		finish.setOnAction(e -> { finish.setDisable(true); Platform.exit(); });
		
		return scene;
	}
	
	public static final Scene launchCreator(Stage s) {
		Nilplace.log("Launching creator.");
		VBox root = new VBox();
		Scene scene = new Scene(root);
		return scene;
	}
	
}