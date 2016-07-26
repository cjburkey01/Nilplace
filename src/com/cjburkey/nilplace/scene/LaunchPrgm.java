package com.cjburkey.nilplace.scene;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.Prgm;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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

public class LaunchPrgm {
	
	private static final TextArea code = new TextArea();
	
	public static final Scene go(Stage s) {
		Nilplace.log("Launching program.");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		Tab myPrograms = new Tab("Installed Programs", tabList());
		Tab install = new Tab("Install New Program", tabInstall());
		Tab create = new Tab("Create Installer", tabCreate());
		
		TabPane pane = new TabPane();
		pane.getTabs().addAll(myPrograms, install, create);
		
		for(Tab t : pane.getTabs()) {
			t.setClosable(false);
		}
		
		root.setPadding(new Insets(0, 0, 10, 0));
		root.setCenter(pane);
		
		return scene;
	}
	
	private static final Node tabList() {
		VBox programs = new VBox();
		
		programs.setPadding(new Insets(10));
		programs.setSpacing(10);
		
		return programs;
	}
	
	private static final Node tabInstall() {
		VBox inst = new VBox();
		
		TextField url = new TextField();
		Button start = new Button("Install");
		
		inst.setPadding(new Insets(10));
		inst.setSpacing(10);
		inst.setAlignment(Pos.CENTER);
		inst.getChildren().addAll(url, start);
		url.setPromptText("URL of Installation Information File.");
		start.setOnAction(e -> {
			start.setDisable(true);
			if(!Prgm.installer.exists()) {
				Dialog<Integer> d = new Dialog<Integer>();
				d.setTitle("Downloading...");
				d.setContentText("Please wait while Nilplace updates the installer file.");
				d.getDialogPane().getButtonTypes().clear();
				new Thread(() -> {
					try {
						URL u = new URL("http://cjburkey.com/nilplace.jar");
						InputStream in = u.openStream();
						
						Nilplace.log("Starting installer update.");
						Files.copy(in, Prgm.installer.toPath(),
								StandardCopyOption.REPLACE_EXISTING);
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
							"java", "-jar", Prgm.installer + "", "--downloadInfoFile=" +
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
		
		return inst;
	}
	
	private static final Node tabCreate() {
		BorderPane make = new BorderPane();
		
		Label l = new Label("Insert: ");
		Button addDownload = new Button("Download");
		Button addExtract = new Button("Extract");
		Button addCopy = new Button("Copy");
		Button addDelete = new Button("Delete");
		Label la = new Label("File: ");
		Button save = new Button("Save");
		ToolBar buttons = new ToolBar();
		buttons.getItems().addAll(l, addDownload, addExtract, addCopy, addDelete, la, save);
		
		make.setTop(buttons);
		make.setCenter(code);
		
		addDownload(addDownload);
		addExtract(addExtract);
		addCopy(addCopy);
		addDelete(addDelete);
		
		code.setOnKeyTyped(e -> {
			if(e.getCode().equals(KeyCode.TAB)) {
				e.consume();
				code.insertText(code.getCaretPosition(), "\t");
			}
		});
		
		return make;
	}
	
	private static final void addDownload(Button btn) {
		btn.setOnAction(e -> {
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
				code.insertText(code.getCaretPosition(), "DOWNLOAD(" + urle.getText() + ", " +
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
	}
	
	private static final void addExtract(Button btn) {
		btn.setOnAction(e -> {
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
				code.insertText(code.getCaretPosition(), "EXTRACT(" + urle.getText() + ", " +
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
	}
	
	private static final void addCopy(Button btn) {
		btn.setOnAction(e -> {
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
				code.insertText(code.getCaretPosition(), "CLONE(" + urle.getText() + ", " +
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
	}
	
	private static final void addDelete(Button btn) {
		btn.setOnAction(e -> {
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
				code.insertText(code.getCaretPosition(), "EXTRACT(" + urle.getText() + ");\n");
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
	}
	
}