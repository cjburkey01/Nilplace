package com.cjburkey.nilplace.scene;

import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.file.WorkerFile;
import com.cjburkey.nilplace.install.LoadData;
import com.cjburkey.nilplace.install.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartInstallScreen {
	
	public static final Scene go(Stage s) {
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
		
		te.appendText("Commands:\n");
		for(WorkerFile f : Worker.workers) {
			String args = "";
			for(String st : f.args) {
				args += st + ((st.equals(f.args[f.args.length - 1])) ? "" : ", ");
			}
			te.appendText("\t- " + f.a + ": [" + args + "]" + 
					((f.equals(Worker.workers.get(Worker.workers.size() - 1))) ? "" : "\n"));
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
		
		cancel.setOnAction(e -> { go.setDisable(true); Nilplace.resetScene(); });
		go.setOnAction(e -> { s.setScene(LoadingScene.go(s)); });
		
		te.setScrollTop(0);
		te.setScrollLeft(0);
		
		return scene;
	}
	
}