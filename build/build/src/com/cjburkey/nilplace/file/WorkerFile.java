package com.cjburkey.nilplace.file;

import com.cjburkey.nilplace.install.InstallerAction;

public class WorkerFile {
	
	public InstallerAction a;
	public String[] args;
	
	public WorkerFile(InstallerAction a, String[] args) {
		this.a = a;
		this.args = args;
	}
	
}