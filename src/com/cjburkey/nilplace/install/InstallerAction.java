package com.cjburkey.nilplace.install;

import com.cjburkey.nilplace.file.CloneFile;
import com.cjburkey.nilplace.file.DeleteFile;
import com.cjburkey.nilplace.file.DownFile;
import com.cjburkey.nilplace.file.ExtractFile;

public enum InstallerAction {
	
	DOWNLOAD((args) -> {
		String url = args[0].trim();
		String loc = args[1].trim();
		
		Progs.downloads.add(new DownFile(url, loc));
	}, "url", "location"),
	
	EXTRACT((args) -> {
		String original = args[0].trim();
		String output = args[1].trim();
		
		Progs.extractions.add(new ExtractFile(original, output));
	}, "location", "output"),
	
	CLONE((args) -> {
		String original = args[0].trim();
		String copy = args[1].trim();
		
		Progs.clones.add(new CloneFile(original, copy));
	}, "original", "duplicate"),
	
	DELETE((args) -> {
		String file = args[0].trim();
		
		Progs.deletions.add(new DeleteFile(file));
	}, "file");

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
	
}