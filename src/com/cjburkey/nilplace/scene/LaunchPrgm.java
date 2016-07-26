package com.cjburkey.nilplace.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.Prgm;
import com.cjburkey.nilplace.install.InstallerAction;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LaunchPrgm {
	
	private static final TextArea code = new TextArea();
	private static final List<Button> buttons = new ArrayList<Button>();
	
	public static final Scene go(Stage s) {
		Nilplace.log("Launching program.");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		Tab myPrograms = new Tab("Installed Programs", tabList());
		Tab install = new Tab("Install New Program", tabInstall());
		Tab create = new Tab("Create Installer", tabCreate(s));
		
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
					Platform.runLater(() -> { d.show(); });
					Process p = b.start();
					
					new Thread(() -> {
						Scanner s = new Scanner(p.getInputStream());
						while(s.hasNextLine()) {
							System.out.println("Installer: " + s.nextLine());
						}
						s.close();
					}).start();
					
					new Thread(() -> {
						Scanner s = new Scanner(p.getErrorStream());
						while(s.hasNextLine()) {
							System.out.println("Installer error: " + s.nextLine());
						}
						s.close();
					}).start();
					
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
	
	private static final Node tabCreate(Stage s) {
		BorderPane make = new BorderPane();
		
		Label l = new Label("Insert: ");
		for(InstallerAction a : InstallerAction.values()) {
			Button but = new Button(a.name());
			initAction(but);
			buttons.add(but);
		}
		Label la = new Label("File: ");
		Button save = new Button("Save");
		Button load = new Button("Load");
		ToolBar toolb = new ToolBar();
		toolb.getItems().addAll(l);
		toolb.getItems().addAll(buttons);
		toolb.getItems().addAll(la, save,load);
		
		make.setTop(toolb);
		make.setCenter(code);
		
		addSave(s, save);
		addLoad(s, load);
		
		code.setOnKeyTyped(e -> {
			if(e.getCode().equals(KeyCode.TAB)) {
				e.consume();
				code.insertText(code.getCaretPosition(), "\t");
			}
		});
		
		return make;
	}
	
	private static final void initAction(Button btn) {
		List<TextField> texts = new ArrayList<TextField>();
		btn.setOnAction(e -> {
			Stage d = new Stage();
			BorderPane ro = new BorderPane();
			VBox center = new VBox();
			HBox bottom = new HBox();
			
			for(String arg : InstallerAction.valueOf(btn.getText()).args) {
				TextField t = new TextField();
				t.setPrefColumnCount(20);
				t.setPromptText(arg);
				texts.add(t);
			}
			
			Button cancel = new Button("Cancel");
			Button ok = new Button("OK");
			
			ro.setCenter(center);
			ro.setBottom(bottom);
			
			center.getChildren().addAll(texts);
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
				String args = "";
				for(TextField t : texts) {
					args += t.getText() + ((texts.get(texts.size() - 1).equals(t)) ? "" : ", ");
				}
				code.insertText(code.getCaretPosition(), btn.getText() + "(" + args + ");\n");
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
	
	private static final void addSave(Stage s, Button btn) {
		btn.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Save");
			fc.setInitialDirectory(new File(System.getProperty("user.home")));
			fc.getExtensionFilters().addAll( new ExtensionFilter("Nilscript File", "*.ns"),
					new ExtensionFilter("All Files", "*.*"));
			
			File f = fc.showSaveDialog(s);
			if(f != null) {
				if(!f.getName().endsWith(".ns")) { f = new File(f + ".ns"); }
				if(!f.getParentFile().exists()) { f.getParentFile().mkdirs(); }
				try {
					FileWriter writer = new FileWriter(f, false);
					writer.write(code.getText());
					writer.close();
				} catch(Exception err) {
					Nilplace.err(err);
				}
			}
		});
	}
	
	private static final void addLoad(Stage s, Button btn) {
		btn.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Load");
			fc.setInitialDirectory(new File(System.getProperty("user.home")));
			fc.getExtensionFilters().addAll( new ExtensionFilter("Nilscript File", "*.ns"),
					new ExtensionFilter("All Files", "*.*"));
			
			File f = fc.showOpenDialog(s);
			if(f != null) {
				code.clear();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line;
					while((line = reader.readLine()) != null) {
						code.appendText(line + "\n");
					}
					reader.close();
				} catch(Exception err) {
					Nilplace.err(err);
				}
			}
		});
	}
	
}