package com.cjburkey.nilplace;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Prgm {
	
	public static ProgressBar prg;
	public static ProgressBar total;
	public static Label totalProg;
	public static File installer = new File(Nilplace.dir, "/installer.jar");
	
	public static final Scene launchInstaller(Stage s, String infoFileUrl) {
		Nilplace.log("Launching installer.");
		
		s.setOnCloseRequest(e -> { e.consume(); });
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/square.css");
		Nilplace.log("Reading file.", false);
		LoadData.load(infoFileUrl);
		Nilplace.log("Finished reading file.", false);
		
		HBox box = new HBox();
		
		Label text = new Label("The following installer will install '" +
				LoadData.getName() +"'");
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
		scene.getStylesheets().add("css/square.css");
		
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
		addStep("Done!");
	}
	
	public static final void addStep(String s) {
		a.setText(s + ((!a.getText().trim().isEmpty()) ? "\n" : "") + a.getText());
	}
	
	private static Button loadingGo;
	private static Button loadingCancel;
	private static TextArea a;
	private static final Scene loadingScene(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/square.css");
		
		Label top = new Label(LoadData.getName());
		VBox center = new VBox();
		HBox bottom = new HBox();
		
		loadingCancel = new Button("Cancel");
		loadingGo = new Button("Continue");
		total = new ProgressBar();
		prg = new ProgressBar();
		totalProg = new Label();
		a = new TextArea();
		
		loadingGo.setDisable(true);
		
		a.setEditable(false);
		
		bottom.getChildren().addAll(loadingCancel, loadingGo);
		bottom.setAlignment(Pos.CENTER_RIGHT);
		bottom.setSpacing(10);
		bottom.setPadding(new Insets(10));
		
		center.getChildren().addAll(totalProg, total, prg, a);
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
		
		finish.setOnAction(e -> { finish.setDisable(true); Platform.exit(); });
		
		return scene;
	}
	
	public static final Scene launchPrgm(Stage s) {
		Nilplace.log("Launching program.");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		VBox programs = new VBox();
		VBox inst = new VBox();
		BorderPane make = new BorderPane();
		
		TextField url = new TextField();
		Button start = new Button("Install");
		
		Label l = new Label("Insert: ");
		Button addDownload = new Button("Download");
		Button addExtract = new Button("Extract");
		Button addCopy = new Button("Copy");
		Button addDelete = new Button("Delete");
		Label la = new Label("File: ");
		Button save = new Button("Save");
		TextArea a = new TextArea();
		ToolBar buttons = new ToolBar();
		buttons.getItems().addAll(l, addDownload, addExtract, addCopy, addDelete, la, save);
		
		Tab myPrograms = new Tab("Installed Programs", programs);
		Tab install = new Tab("Install New Program", inst);
		Tab create = new Tab("Create Installer", make);
		
		programs.setPadding(new Insets(10));
		programs.setSpacing(10);
		
		addDownload.setOnAction(e -> {
			Stage d = new Stage();
			BorderPane ro = new BorderPane();
			VBox center = new VBox();
			HBox bottom = new HBox();
			
			TextField urle = new TextField();
			TextField file = new TextField();
			
			urle.setPrefColumnCount(35);
			file.setPrefColumnCount(35);
			
			urle.setPromptText("Input");
			file.setPromptText("Output");
			
			Button cancel = new Button("Cancel");
			Button ok = new Button("OK");
			
			ro.setCenter(center);
			ro.setBottom(bottom);
			
			center.getChildren().addAll(urle, file);
			bottom.getChildren().addAll(cancel, ok);
			
			center.setPadding(new Insets(10));
			center.setSpacing(10);
			center.setAlignment(Pos.CENTER);

			bottom.setPadding(new Insets(10));
			bottom.setSpacing(10);
			bottom.setAlignment(Pos.CENTER_RIGHT);
			
			cancel.setOnAction(ev -> { d.close(); });
			ok.setOnAction(ev -> {
				d.close();
				a.insertText(a.getCaretPosition(), "DOWNLOAD(" + urle.getText() + ", " +
						file.getText() + ");\n");
			});
			
			d.initModality(Modality.APPLICATION_MODAL);
			d.setTitle("Insert");
			d.setScene(new Scene(ro));
			d.sizeToScene();
			d.setResizable(false);
			d.centerOnScreen();
			d.show();
			
			ok.requestFocus();
		});
		
		addExtract.setOnAction(e -> {
			Stage d = new Stage();
			BorderPane ro = new BorderPane();
			VBox center = new VBox();
			HBox bottom = new HBox();
			
			TextField urle = new TextField();
			TextField file = new TextField();
			
			urle.setPrefColumnCount(35);
			file.setPrefColumnCount(35);
			
			urle.setPromptText("Input");
			file.setPromptText("Output");
			
			Button cancel = new Button("Cancel");
			Button ok = new Button("OK");
			
			ro.setCenter(center);
			ro.setBottom(bottom);
			
			center.getChildren().addAll(urle, file);
			bottom.getChildren().addAll(cancel, ok);
			
			center.setPadding(new Insets(10));
			center.setSpacing(10);
			center.setAlignment(Pos.CENTER);

			bottom.setPadding(new Insets(10));
			bottom.setSpacing(10);
			bottom.setAlignment(Pos.CENTER_RIGHT);
			
			cancel.setOnAction(ev -> { d.close(); });
			ok.setOnAction(ev -> {
				d.close();
				a.insertText(a.getCaretPosition(), "EXTRACT(" + urle.getText() + ", " +
						file.getText() + ");\n");
			});
			
			d.initModality(Modality.APPLICATION_MODAL);
			d.setTitle("Insert");
			d.setScene(new Scene(ro));
			d.sizeToScene();
			d.setResizable(false);
			d.centerOnScreen();
			d.show();
			
			ok.requestFocus();
		});
		
		addCopy.setOnAction(e -> {
			Stage d = new Stage();
			BorderPane ro = new BorderPane();
			VBox center = new VBox();
			HBox bottom = new HBox();
			
			TextField urle = new TextField();
			TextField file = new TextField();
			
			urle.setPrefColumnCount(35);
			file.setPrefColumnCount(35);
			
			urle.setPromptText("Input");
			file.setPromptText("Output");
			
			Button cancel = new Button("Cancel");
			Button ok = new Button("OK");
			
			ro.setCenter(center);
			ro.setBottom(bottom);
			
			center.getChildren().addAll(urle, file);
			bottom.getChildren().addAll(cancel, ok);
			
			center.setPadding(new Insets(10));
			center.setSpacing(10);
			center.setAlignment(Pos.CENTER);

			bottom.setPadding(new Insets(10));
			bottom.setSpacing(10);
			bottom.setAlignment(Pos.CENTER_RIGHT);
			
			cancel.setOnAction(ev -> { d.close(); });
			ok.setOnAction(ev -> {
				d.close();
				a.insertText(a.getCaretPosition(), "CLONE(" + urle.getText() + ", " +
						file.getText() + ");\n");
			});
			
			d.initModality(Modality.APPLICATION_MODAL);
			d.setTitle("Insert");
			d.setScene(new Scene(ro));
			d.sizeToScene();
			d.setResizable(false);
			d.centerOnScreen();
			d.show();
			
			ok.requestFocus();
		});
		
		addDelete.setOnAction(e -> {
			Stage d = new Stage();
			BorderPane ro = new BorderPane();
			VBox center = new VBox();
			HBox bottom = new HBox();
			
			TextField urle = new TextField();
			TextField file = new TextField();
			
			urle.setPrefColumnCount(35);
			file.setPrefColumnCount(35);
			
			urle.setPromptText("Input");
			
			Button cancel = new Button("Cancel");
			Button ok = new Button("OK");
			
			ro.setCenter(center);
			ro.setBottom(bottom);
			
			center.getChildren().addAll(urle);
			bottom.getChildren().addAll(cancel, ok);
			
			center.setPadding(new Insets(10));
			center.setSpacing(10);
			center.setAlignment(Pos.CENTER);

			bottom.setPadding(new Insets(10));
			bottom.setSpacing(10);
			bottom.setAlignment(Pos.CENTER_RIGHT);
			
			cancel.setOnAction(ev -> { d.close(); });
			ok.setOnAction(ev -> {
				d.close();
				a.insertText(a.getCaretPosition(), "EXTRACT(" + urle.getText() + ");\n");
			});
			
			d.initModality(Modality.APPLICATION_MODAL);
			d.setTitle("Insert");
			d.setScene(new Scene(ro));
			d.sizeToScene();
			d.setResizable(false);
			d.centerOnScreen();
			d.show();
			
			ok.requestFocus();
		});
		
		make.setTop(buttons);
		make.setCenter(a);
		
		a.setOnKeyTyped(e -> {
			if(e.getCode().equals(KeyCode.TAB)) {
				e.consume();
				a.insertText(a.getCaretPosition(), "\t");
			}
		});

		inst.setPadding(new Insets(10));
		inst.setSpacing(10);
		inst.setAlignment(Pos.CENTER);
		inst.getChildren().addAll(url, start);
		url.setPromptText("URL of Installation Information File.");
		start.setOnAction(e -> {
			start.setDisable(true);
			if(!installer.exists()) {
				Dialog<Integer> d = new Dialog<Integer>();
				d.setTitle("Downloading...");
				d.setContentText("Please wait while Nilplace updates the installer file.");
				d.getDialogPane().getButtonTypes().clear();
				new Thread(() -> {
					try {
						URL u = new URL("http://cjburkey.com/nilplace.jar");
						InputStream in = u.openStream();
						
						Nilplace.log("Starting installer update.");
						Files.copy(in, installer.toPath(), StandardCopyOption.REPLACE_EXISTING);
						Platform.runLater(() -> { d.setResult(0); d.close(); });
						Nilplace.log("Done.");
						
						in.close();
					} catch(Exception err) {
						Nilplace.err(err);
					}
				}).start();
				d.show();
			}
			
			Dialog<Integer> d = new Dialog<Integer>();
			d.setTitle("Loading...");
			d.setContentText("Loading installer.");
			d.getDialogPane().getButtonTypes().clear();
			new Thread(() -> {
				try {
					ProcessBuilder b = new ProcessBuilder(new String[] {
							"java", "-jar", installer + "", "--downloadInfoFile=" +
									url.getText() });
					Process p = b.start();
					Platform.runLater(() -> { d.show(); });
					p.waitFor();
					Platform.runLater(() -> {
						d.setResult(0);
						d.close();
						url.clear();
						start.setDisable(false);
					});
				} catch(Exception err) {
					Nilplace.err(err);
				}
			}).start();
		});
		
		TabPane pane = new TabPane();
		pane.getTabs().addAll(myPrograms, install, create);
		
		for(Tab t : pane.getTabs()) {
			t.setClosable(false);
		}
		
		root.setPadding(new Insets(0, 0, 10, 0));
		root.setCenter(pane);
		
		return scene;
	}
	
	public static final Scene launchCreator(Stage s) {
		Nilplace.log("Launching creator.");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		return scene;
	}
	
}