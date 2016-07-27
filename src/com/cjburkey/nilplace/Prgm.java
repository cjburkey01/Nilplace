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
		addStep(s, false);
	}
	
	public static final void addStep(String s, boolean addToLastLine) {
		if(addToLastLine) {
			String[] split = a.getText().split("\n");
			split[0] += s;
			String done = "";
			for(String add : split) { done += add + "\n"; }
			a.setText(done);
		} else {
			a.setText(s + "\n" + a.getText());
		}
	}
	
}