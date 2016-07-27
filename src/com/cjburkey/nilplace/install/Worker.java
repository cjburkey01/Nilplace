package com.cjburkey.nilplace.install;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.Prgm;
import com.cjburkey.nilplace.file.WorkerFile;

public class Worker {
	
	public static final List<WorkerFile> workers = new ArrayList<WorkerFile>();
	
	public static boolean cancel = false;
	public static String dir = Nilplace.getAppDir();
	public static final int bufferSize = 2 * 1024;
	
	private static boolean first = true;
	private static int total = 0;
	private static int through = 0;
	
	public static final void calcTotal() {
		if(first) {
			first = false;
			total = workers.size();
			Prgm.total.setProgress(0);
		}
	}
	
	public static final void updateTotalBar() {
		Prgm.total.setProgress((Prgm.prg.getProgress() / (double) total) +
				((double) through / (double) total));
	}
	
	public static final void fin() {
		updateTotalBar();
		through ++;
		Prgm.totalProg.setText(through + " / " + total);
	}
	
	public static final void go() {
		new Thread(() -> {
			calcTotal();
			for(WorkerFile f : workers) {
				if(!cancel) {
					f.a.action.call(f.args);
				}
			}
			if(!cancel) { writeInfo(); }
			Prgm.done();
		}).start();;
	}
	
	private static final void writeInfo() {
		Nilplace.log("Writing program information.", false);
		File f = new File(dir, "prgm.info");
		try {
			f.delete();
			f.createNewFile();
			FileWriter writer = new FileWriter(f, true);
			
			writer.append(new File(dir).getName() + "\n");
			writer.append(LoadData.getName() + "");
			
			writer.close();
		} catch(Exception e) {
			Nilplace.err(e);
		}
		Nilplace.log("Done.", false);
	}
	
}