package com.cjburkey.nilplace.install;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
	public static final int bufferSize = 2 * 1024;
	
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
	
	private static final void updateTotalBar() {
		Prgm.total.setProgress((Prgm.prg.getProgress() / (double) total) +
				((double) through / (double) total));
	}
	
	private static final void fin() {
		updateTotalBar();
		through ++;
		Prgm.totalProg.setText(through + " / " + total);
	}
	
	public static final void go() {
		new Thread(() -> {
			calcTotal();
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
		for(DownFile f : downloads) {
			downloadedFileSize = 0;
			Nilplace.log("Downloading to '" + f.file + "' from '" + f.url + "'");
			try {
				File fi = new File(dir, f.file);
				URL url = new URL(f.url);
				
				if(!new File(fi.getParent()).exists()) {
					new File(fi.getParent()).mkdirs();
				}
				
				Platform.runLater(() -> {
					Prgm.prg.setProgress(0);
					Prgm.addStep("Download: " + fi.getName());
				});
				
				HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
				long completeFileSize = httpConnection.getContentLength();
				BufferedInputStream bis =
						new BufferedInputStream(httpConnection.getInputStream());
				FileOutputStream fos = new FileOutputStream(fi);
				BufferedOutputStream bos = new BufferedOutputStream(fos, bufferSize);
				
				byte[] data = new byte[bufferSize];
				int x = 0;
				while((x = bis.read(data, 0, bufferSize)) >= 0 && !cancel) {
					downloadedFileSize += x;
					Platform.runLater(() -> {
						if(completeFileSize > 0) {
							Prgm.prg.setProgress((double) downloadedFileSize /
									(double) completeFileSize);
							updateTotalBar();
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
	}

	private static float prev = -1;
	private static final void extract() {
		Platform.runLater(() -> { Prgm.addStep(""); });
		for(ExtractFile f : extractions) {
			Nilplace.log("Extracting to '" + f.output + "' from '" + f.input + "'");
			try {
				Platform.runLater(() -> {
					Prgm.prg.setProgress(0);
					Prgm.addStep("Extract: " + new File(f.output).getName());
				});
				
				FileInputStream stream = new FileInputStream(new File(dir, f.input));
				ZipInputStream zis = new ZipInputStream(stream);
				
				byte[] buffer = new byte[bufferSize];
				int length;
				int finalSize = 0;
				float current = 0;

				finalSize = (int) new File(dir, f.input).length();
				ZipEntry e = null;
				while((e = zis.getNextEntry()) != null) {
					current += e.getCompressedSize();
					
					File fi = new File(new File(dir, f.output), e.getName());
					if(!fi.getParentFile().exists()) { fi.getParentFile().mkdirs(); }
					
					FileOutputStream fos = new FileOutputStream(fi);
					while((length = zis.read(buffer)) > 0 && !cancel) {
						fos.write(buffer, 0, length);
					}
					fos.close();
					zis.closeEntry();
					
					if(prev != current / finalSize) {
						prev = current / finalSize;
						Platform.runLater(() -> { Prgm.prg.setProgress(prev);
							updateTotalBar(); });
					}
					
				}
				
				zis.close();
				stream.close();
				
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
	}
	
	private static int finalSize = 0;
	private static float current = 0;
	private static final void clones() {
		Platform.runLater(() -> { Prgm.addStep(""); });
		for(CloneFile f : clones) {
			Nilplace.log("Copying to '" + f.output + "' from '" + f.input + "'");
			try {
				if(!new File(dir, f.input).isDirectory()) {
					Platform.runLater(() -> {
						Prgm.prg.setProgress(0);
						Prgm.addStep("Copy: " + new File(f.output).getName());
					});
					
					FileInputStream fis = new FileInputStream(new File(dir, f.input));
					FileOutputStream fos = new FileOutputStream(new File(dir, f.output));
					
					byte[] buffer = new byte[bufferSize];
					
					int length = 0;
					
					finalSize = (int) new File(dir, f.input).length();
					
					while((length = fis.read(buffer, 0, bufferSize)) >= 0 && !cancel) {
						current += length;
						
						Platform.runLater(() -> {
							Prgm.prg.setProgress((double) current / (double) finalSize);
							updateTotalBar();
						});
						
						fos.write(buffer, 0, length);
					}
					
					fos.close();
					fis.close();
				}
				
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
	}
	
	private static final void delete() {
		Platform.runLater(() -> { Prgm.addStep(""); });
		for(DeleteFile f : deletions) {
			Nilplace.log("Deleting '" + f.delete + "'");
			File fi = new File(dir, f.delete);
			if(fi.isDirectory()) { Util.deleteDir(fi); }
			else if(!fi.delete()) { fi.deleteOnExit(); }
			Nilplace.log("Done.");
			Platform.runLater(() -> {
				fin(); Prgm.addStep("Delete: " + fi.getName()); Prgm.prg.setProgress(1);
			});
		}
	}
	
}