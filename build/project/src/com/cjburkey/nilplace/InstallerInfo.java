package com.cjburkey.nilplace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class InstallerInfo {
	
	public String name;
	public int id;
	
	public InstallerInfo(String n, int i) {
		this.name = n;
		this.id = i;
	}
	
	public String toString() { return this.name; }
	
	private static final List<InstallerInfo> views = new ArrayList<InstallerInfo>();
	
	public static final List<InstallerInfo> reloadViews() {
		views.clear();
		for(File f : new File(Nilplace.dir).listFiles()) {
			if(f.isDirectory()) {
				File info = new File(f, "/prgm.info");
				if(info.exists()) {
					try {
						BufferedReader reader = new BufferedReader(new FileReader(info));
						String l;
						int id = -1;
						String name;
						if((l = reader.readLine()) != null) {
							id = Integer.parseInt(l);
							if(id >= 0 && (l = reader.readLine()) != null) {
								name = l.trim();
								InstallerInfo v = new InstallerInfo(name, id);
								views.add(v);
							}
						}
						reader.close();
					} catch(Exception e) {
						Nilplace.err(e);
					}
				}
			}
		}
		return views;
	}
}