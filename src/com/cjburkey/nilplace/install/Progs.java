package com.cjburkey.nilplace.install;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.Prgm;
import com.cjburkey.nilplace.file.CloneFile;
import com.cjburkey.nilplace.file.DeleteFile;
import com.cjburkey.nilplace.file.DownFile;
import com.cjburkey.nilplace.file.ExtractFile;
import com.cjburkey.nilplace.file.Util;
import javafx.application.Platform;

public class Progs {
	
	public static final List<DownFile> downloads = new ArrayList<DownFile>();
	public static final List<ExtractFile> extractions = new ArrayList<ExtractFile>();
	public static final List<CloneFile> clones = new ArrayList<CloneFile>();
	public static final List<DeleteFile> deletions = new ArrayList<DeleteFile>();
	
	public static boolean cancel = false;
	public static String dir = Nilplace.getAppDir();
	
	private static boolean first = true;
	private static int total = 0;
	private static int through = 0;
	private static final void calcTotal() {
		if(first) {
			first = false;
			total = downloads.size() + extractions.size() + clones.size() + deletions.size();
			Prgm.total.setProgress(0);
		}
	}
	
	private static final void fin() {
		through ++;
		Prgm.total.setProgress((double) through / (double) total);
		Prgm.totalProg.setText(through + " / " + total);
	}
	
	public static final void go() {
		new Thread(() -> {
			download();
			extract();
			clones();
			delete();
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
			
			writer.append(new File(dir).getName() + ";\n");
			writer.append(LoadData.getName() + ";");
			
			writer.close();
		} catch(Exception e) {
			Nilplace.err(e);
		}
		Nilplace.log("Done.", false);
	}
	
	private static long downloadedFileSize = 0;
	private static final void download() {
		calcTotal();
		for(DownFile f : downloads) {
			downloadedFileSize = 0;
			Nilplace.log("Downloading to '" + f.file + "' from '" + f.url + "'");
			try {
				File fi = new File(dir, f.file);
				URL url = new URL(f.url);
				
				if(!new File(fi.getParent()).exists()) {
					new File(fi.getParent()).mkdirs();
				}
				
				HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
				long completeFileSize = httpConnection.getContentLength();
				BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
				FileOutputStream fos = new FileOutputStream(fi);
				BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
				
				Platform.runLater(() -> {
					Prgm.prg.setProgress(0);
					Prgm.currentFile.setText(fi.getName());
				});
				
				byte[] data = new byte[1024];
				int x = 0;
				while((x = bis.read(data, 0, 1024)) >= 0 && !cancel) {
					downloadedFileSize += x;
					Platform.runLater(() -> {
						if(completeFileSize > 0) {
							Prgm.prg.setProgress((double) downloadedFileSize / (double) completeFileSize);
						}
					});
					bos.write(data, 0, x);
				}
				
				bos.close();
				fos.close();
				bis.close();
				
				if(cancel) {
					Util.deleteDir(new File(dir));
					break;
				}
			} catch(Exception e) {
				Nilplace.err(e);
			}
			Platform.runLater(() -> { fin(); Prgm.prg.setProgress(1); });
			Nilplace.log("Done.");
		}
		Platform.runLater(() -> { Prgm.currentFile.setText(""); });
	}
	
	private static final void extract() {
		for(ExtractFile f : extractions) {
			calcTotal();
			Nilplace.log("Extracting to '" + f.output + "' from '" + f.input + "'");
			fin();
		}
	}
	
	private static final void clones() {
		for(CloneFile f : clones) {
			calcTotal();
			Nilplace.log("Copying to '" + f.output + "' from '" + f.input + "'");
			fin();
		}
	}
	
	private static final void delete() {
		for(DeleteFile f : deletions) {
			calcTotal();
			Nilplace.log("Deleting '" + f.delete + "'");
			fin();
		}
	}
	
}