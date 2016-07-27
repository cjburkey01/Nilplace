package com.cjburkey.nilplace.install;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.local.Localization;
import com.cjburkey.nilplace.nilscript.ReadScript;

public class LoadData {
	
	private static List<String> lines;
	private static String name;
	
	public static String winLaunch, macLaunch, linLaunch;
	
	public static final boolean load(String url) {
		lines = new ArrayList<String>();
		try {
			Scanner s = new Scanner(new URL(url).openStream());
			while(s.hasNextLine()) {
				lines.add(s.nextLine());
			}
			s.close();
			if(readInfo(lines.get(0))) {
				lines.remove(0);
				return true;
			}
		} catch (Exception e) {
			Nilplace.err(e, false);
		}
		return false;
	}
	
	public static final void executeScript() {
		for(String s : lines) {
			ReadScript.read(s);
		}
	}
	
	public static final boolean readInfo(String url) {
		try {
			Properties p = new Properties();
			p.load(new URL(url).openStream());
			Nilplace.log(url + " - " + p.keySet());
			if((name = p.getProperty("programName")) == null) {
				Nilplace.err(Localization.getLocalized("noNameFound"));
				Nilplace.resetScene(Localization.getLocalized("noNameFound"));
				return false;
			}
			winLaunch = p.getProperty("winLaunch");
			macLaunch = p.getProperty("macLaunch");
			linLaunch = p.getProperty("linLaunch");
			if(winLaunch == null && macLaunch == null && linLaunch == null) {
				Nilplace.err(Localization.getLocalized("noLaunchMethod"));
				Nilplace.resetScene(Localization.getLocalized("noLaunchMethod"));
				return false;
			}
			return true;
		} catch(Exception e) {
			Nilplace.err(e);
			Nilplace.resetScene(Localization.getLocalized("noInstallerInfo"));
		}
		return false;
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