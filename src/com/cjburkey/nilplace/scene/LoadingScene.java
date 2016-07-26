package com.cjburkey.nilplace.scene;

import com.cjburkey.nilplace.Prgm;
import com.cjburkey.nilplace.install.LoadData;
import com.cjburkey.nilplace.install.Worker;
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

public class LoadingScene {
	
	public static final Scene go(Stage s) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/square.css");
		
		Label top = new Label(LoadData.getName());
		VBox center = new VBox();
		HBox bottom = new HBox();
		
		Prgm.loadingCancel = new Button("Cancel");
		Prgm.loadingGo = new Button("Continue");
		Prgm.total = new ProgressBar();
		Prgm.prg = new ProgressBar();
		Prgm.totalProg = new Label();
		Prgm.a = new TextArea();
		
		Prgm.loadingGo.setDisable(true);
		
		Prgm.a.setEditable(false);
		
		bottom.getChildren().addAll(Prgm.loadingCancel, Prgm.loadingGo);
		bottom.setAlignment(Pos.CENTER_RIGHT);
		bottom.setSpacing(10);
		bottom.setPadding(new Insets(10));
		
		center.getChildren().addAll(Prgm.totalProg, Prgm.total, Prgm.prg, Prgm.a);
		center.setAlignment(Pos.CENTER);
		center.setSpacing(10);
		center.setPadding(new Insets(10));
		
		Prgm.total.setMaxWidth(Double.MAX_VALUE);
		Prgm.prg.setMaxWidth(Double.MAX_VALUE);
		center.setFillWidth(true);
		
		root.setPadding(new Insets(10));
		root.setTop(top);
		root.setCenter(center);
		root.setBottom(bottom);
		
		Prgm.loadingCancel.setOnAction(e -> { Worker.cancel = true; Platform.exit(); });
		Prgm.loadingGo.setOnAction(e -> { s.setScene(Done.go(s)); });
		
		Worker.go();
		
		return scene;
	}
	
}