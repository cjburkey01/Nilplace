package com.cjburkey.nilplace.install;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.nilscript.ReadScript;

public class LoadData {
	
	private static final List<String> lines = new ArrayList<String>();
	private static String name;
	
	public static final void load(String url) {
		try {
			Scanner s = new Scanner(new URL(url).openStream());
			while(s.hasNextLine()) {
				lines.add(s.nextLine());
			}
			s.close();
			name = lines.get(0);
			lines.remove(0);
		} catch (Exception e) {
			Nilplace.err(e);
		}
	}
	
	public static final void executeScript() {
		for(String s : lines) {
			ReadScript.read(s);
		}
	}
	
	public static final String getName() {
		return name;
	}
	
	public static final String[] getLines() {
		String[] ls = new String[lines.size()];
		ls = lines.toArray(ls);
		return ls;
	}
	
}