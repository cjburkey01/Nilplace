package com.cjburkey.nilplace.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import com.cjburkey.nilplace.InstallerInfo;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.file.Util;
import com.cjburkey.nilplace.install.InstallerAction;
import com.cjburkey.nilplace.local.Localization;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LaunchMain {
	
	private static TextArea code;
	private static final List<Button> buttons = new ArrayList<Button>();
	
	public static TabPane pane;
	public static ListView<InstallerInfo> list;
	
	public static final Scene go(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		s.setOnCloseRequest(e -> {  });
		
		Tab myPrograms = new Tab(Localization.getLocalized("installed"), tabList());
		Tab install = new Tab(Localization.getLocalized("newProg"), tabInstall(s));
		Tab create = new Tab(Localization.getLocalized("createInst"), tabCreate(s));
		
		pane = new TabPane();
		pane.getTabs().addAll(myPrograms, install, create);
		pane.getSelectionModel().selectedIndexProperty().addListener(e -> {
			if(pane.getSelectionModel().getSelectedIndex() == 0) {
				list.getItems().clear();
				list.getItems().addAll(InstallerInfo.reloadViews());
			}
		});
		
		for(Tab t : pane.getTabs()) {
			t.setClosable(false);
		}
		
		root.setPadding(new Insets(0, 0, 10, 0));
		root.setCenter(pane);
		
		return scene;
	}
	
	private static InstallerInfo v = null;
	private static final Node tabList() {
		VBox programs = new VBox();
		
		list = new ListView<InstallerInfo>();
		list.getItems().addAll(InstallerInfo.reloadViews());
		
		MenuItem launch = new MenuItem(Localization.getLocalized("launchProgram"));
		MenuItem delete = new MenuItem(Localization.getLocalized("deleteProgram"));
		ContextMenu m = new ContextMenu(launch, delete);
		
		delete.setOnAction(e -> {
			if(v != null) {
				File f = new File(Nilplace.dir, v.id + "");
				Util.deleteDir(f);
				Nilplace.resetScene(null);
			}
		});
		
		list.setOnMouseClicked(e -> {
			int count = e.getClickCount();
			v = list.getSelectionModel().getSelectedItem();
			if(v != null) {
				//int id = v.id;
				if(count == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
					
				} else if(count == 1 && e.getButton().equals(MouseButton.SECONDARY)) {
					m.show(list, e.getScreenX(), e.getScreenY());
				} else if(count == 1 && e.getButton().equals(MouseButton.PRIMARY)) {
					if(m.isShowing()) m.hide();
				}
			}
		});
		
		programs.setPadding(new Insets(10));
		programs.setSpacing(10);
		programs.getChildren().addAll(list);
		
		return programs;
	}
	
	private static final Node tabInstall(Stage s) {
		VBox inst = new VBox();
		
		Button loadFromFile = new Button(Localization.getLocalized("installFromFile"));
		TextField url = new TextField();
		Button start = new Button(Localization.getLocalized("install"));
		
		inst.setPadding(new Insets(10));
		inst.setSpacing(10);
		inst.setAlignment(Pos.CENTER);
		inst.getChildren().addAll(loadFromFile, url, start);
		url.setPromptText(Localization.getLocalized("urlOfFile"));
		start.setOnAction(e -> { Nilplace.install(s, url.getText()); });
		loadFromFile.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.setTitle(Localization.getLocalized("openInstaller"));
			fc.getExtensionFilters().addAll(
				new ExtensionFilter(Localization.getLocalized("nilscriptFiles"), "*.ns"),
				new ExtensionFilter(Localization.getLocalized("allFiles"), "*.*")
			);
			File f = fc.showOpenDialog(s);
			if(f != null) {
				url.setText("file://" + f.getAbsolutePath());
			}
		});
		
		return inst;
	}
	
	private static final Node tabCreate(Stage s) {
		BorderPane make = new BorderPane();
		
		Label l = new Label(Localization.getLocalized("insert") + ": ");
		for(InstallerAction a : InstallerAction.values()) {
			Button but = new Button(a.name());
			initAction(but);
			buttons.add(but);
		}
		Label la = new Label(Localization.getLocalized("file") + ": ");
		Button save = new Button(Localization.getLocalized("save"));
		Button load = new Button(Localization.getLocalized("load"));
		code = new TextArea();
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
			fc.getExtensionFilters().addAll(
					new ExtensionFilter(Localization.getLocalized("nilscriptFiles"), "*.ns"),
					new ExtensionFilter(Localization.getLocalized("allFiles"), "*.*"));
			
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
			fc.getExtensionFilters().addAll(
					new ExtensionFilter(Localization.getLocalized("nilscriptFiles"), "*.ns"),
					new ExtensionFilter(Localization.getLocalized("allFiles"), "*.*"));
			
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