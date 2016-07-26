package com.cjburkey.nilplace;

import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class Prgm {
	
	public static ProgressBar prg;
	public static ProgressBar total;
	public static Label totalProg;
	public static File installer = new File(Nilplace.dir, "/installer.jar");
	public static Button loadingGo;
	public static Button loadingCancel;
	public static TextArea a;
	
	public static void done() {
		loadingGo.setDisable(false);
		loadingCancel.setDisable(true);
		addStep("Done!");
	}
	
	public static final void addStep(String s) {
		a.setText(s + ((!a.getText().trim().isEmpty()) ? "\n" : "") + a.getText());
	}
	
}