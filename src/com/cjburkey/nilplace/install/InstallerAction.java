package com.cjburkey.nilplace.install;

import com.cjburkey.nilplace.Nilplace;

public enum InstallerAction {
	
	DOWNLOAD((args) -> {
		String url = args[0].trim();
		String loc = args[1].trim();
		
		Nilplace.log("Downloading to '" + loc + "' from '" + url + "'");
	}, "url", "location"),
	
	EXTRACT((args) -> {
		String original = args[0].trim();
		String output = args[1].trim();
		
		Nilplace.log("Extracting to '" + output + "' from '" + original + "'");
	}, "location", "output"),
	
	CLONE((args) -> {
		String original = args[0].trim();
		String copy = args[1].trim();
		
		Nilplace.log("Copying '" + original + "' to '" + copy + "'");
	}, "original", "duplicate"),
	
	DELETE((args) -> {
		String file = args[0].trim();
		
		Nilplace.log("Deleting '" + file + "'");
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