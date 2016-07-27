package com.cjburkey.nilplace.install;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.Prgm;
import com.cjburkey.nilplace.file.Util;
import com.cjburkey.nilplace.local.Localization;
import javafx.application.Platform;

public enum InstallerAction {
	
	Download((args) -> {
		String url = args[0].trim();
		String loc = args[1].trim();

		Platform.runLater(() -> { Prgm.addStep(Localization.getLocalized("downloading", loc)); });
		
		download(url, loc);
		
		Platform.runLater(() -> { Prgm.addStep("     .." +
				Localization.getLocalized("done"), true); });
	}, "URL", "Location"),
	
	Extract((args) -> {
		String original = args[0].trim();
		String folder = args[1].trim();
		
		Platform.runLater(() -> {
			Prgm.addStep(Localization.getLocalized("extracting", original)); });
		
		extract(original, folder);
		
		Platform.runLater(() -> { Prgm.addStep("     .." +
				Localization.getLocalized("done"), true); });
	}, "Location", "Output"),
	
	Clone((args) -> {
		String original = args[0].trim();
		String copy = args[1].trim();

		Platform.runLater(() -> {
			Prgm.addStep(Localization.getLocalized("copying", original, copy)); });
		
		copy(original, copy);
		
		Platform.runLater(() -> { Prgm.addStep("     .." +
				Localization.getLocalized("done"), true); });
	}, "Original", "Duplicate"),
	
	Delete((args) -> {
		String file = args[0].trim();
		File f = new File(file);
		Platform.runLater(() -> { Worker.fin(); Prgm.addStep(Localization.getLocalized("deleting", file)); });
		if(!f.delete()) {
			f.deleteOnExit();
		}
		Platform.runLater(() -> { Prgm.addStep("     .." +
				Localization.getLocalized("done"), true); });
	}, "File");

	public Action action;
	public String[] args;
	InstallerAction(Action a, String... args) {
		this.action = a;
		this.args = args;
	}
	
	public static boolean hasKey(String key) {
		for(InstallerAction a : InstallerAction.values()) {
			if(a.name().equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	private static long downloadedFileSize;
	private static final void download(String inUrl, String file) {
		downloadedFileSize = 0;
		Nilplace.log("Downloading to '" + file + "' from '" + inUrl + "'");
		try {
			File fi = new File(Worker.dir, file);
			URL url = new URL(inUrl);
			
			if(!new File(fi.getParent()).exists()) {
				new File(fi.getParent()).mkdirs();
			}
			
			Platform.runLater(() -> {
				Prgm.prg.setProgress(0);
			});
			
			HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
			long completeFileSize = httpConnection.getContentLength();
			BufferedInputStream bis =
					new BufferedInputStream(httpConnection.getInputStream());
			FileOutputStream fos = new FileOutputStream(fi);
			BufferedOutputStream bos = new BufferedOutputStream(fos, Worker.bufferSize);
			
			byte[] data = new byte[Worker.bufferSize];
			int x = 0;
			while((x = bis.read(data, 0, Worker.bufferSize)) >= 0 && !Worker.cancel) {
				downloadedFileSize += x;
				if(completeFileSize > 0) {
					Platform.runLater(() -> {
						Prgm.prg.setProgress((double) downloadedFileSize /
								(double) completeFileSize);
						Worker.updateTotalBar();
					});
				}
				bos.write(data, 0, x);
			}
			
			bos.close();
			fos.close();
			bis.close();
			
			if(Worker.cancel) {
				Util.deleteDir(new File(Worker.dir));
			}
		} catch(Exception e) {
			Nilplace.err(e);
		}
		Platform.runLater(() -> { Worker.fin(); });
		Nilplace.log("Done.");
	}
	
	private static float prev = -1;
	private static final void extract(String og, String out) {
		Nilplace.log("Extracting to '" + out + "' from '" + og + "'");
		try {
			Platform.runLater(() -> {
				Prgm.prg.setProgress(0);
				Prgm.addStep("Extract: " + new File(out).getName());
			});
			
			FileInputStream stream = new FileInputStream(new File(Worker.dir, og));
			ZipInputStream zis = new ZipInputStream(stream);
			
			byte[] buffer = new byte[Worker.bufferSize];
			int length;
			int finalSize = 0;
			float current = 0;

			finalSize = (int) new File(Worker.dir, og).length();
			ZipEntry e = null;
			while((e = zis.getNextEntry()) != null) {
				current += e.getCompressedSize();
				
				File fi = new File(new File(Worker.dir, out), e.getName());
				if(!fi.getParentFile().exists()) { fi.getParentFile().mkdirs(); }
				
				FileOutputStream fos = new FileOutputStream(fi);
				while((length = zis.read(buffer)) > 0 && !Worker.cancel) {
					fos.write(buffer, 0, length);
				}
				fos.close();
				zis.closeEntry();
				
				if(prev != current / finalSize) {
					prev = current / finalSize;
					Platform.runLater(() -> {
						Prgm.prg.setProgress(prev);
						Worker.updateTotalBar();
					});
				}
				
			}
			
			zis.close();
			stream.close();
			
			if(Worker.cancel) {
				Util.deleteDir(new File(Worker.dir));
			}
		} catch(Exception e) {
			Nilplace.err(e);
		}
		Platform.runLater(() -> { Worker.fin(); });
		Nilplace.log("Done.");
	}
	
	private static int finalSize = 0;
	private static float current = 0;
	private static final void copy(String og, String copy) {
		Nilplace.log("Copying to '" + copy + "' from '" + og + "'");
		try {
			if(!new File(Worker.dir, copy).isDirectory()) {
				Platform.runLater(() -> {
					Prgm.prg.setProgress(0);
					Prgm.addStep("Copy: " + new File(copy).getName());
				});
				
				FileInputStream fis = new FileInputStream(new File(Worker.dir, og));
				FileOutputStream fos = new FileOutputStream(new File(Worker.dir, copy));
				
				byte[] buffer = new byte[Worker.bufferSize];
				
				int length = 0;
				
				finalSize = (int) new File(Worker.dir, og).length();
				
				while((length = fis.read(buffer, 0, Worker.bufferSize)) >= 0 && !Worker.cancel) {
					current += length;
					
					Platform.runLater(() -> {
						Prgm.prg.setProgress((double) current / (double) finalSize);
						Worker.updateTotalBar();
					});
					
					fos.write(buffer, 0, length);
				}
				
				fos.close();
				fis.close();
			}
			
			if(Worker.cancel) {
				Util.deleteDir(new File(Worker.dir));
			}
		} catch(Exception e) {
			Nilplace.err(e);
		}
		Platform.runLater(() -> { Worker.fin(); });
		Nilplace.log("Done.");
	}
	
}