package com.cjburkey.nilplace.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import com.cjburkey.nilplace.Nilplace;

public class Localization {
	
	public String unlocName;
	public String locName;
	
	public Localization(String u, String l) {
		this.unlocName = u;
		this.locName = l;
	}
	
	private static final List<Localization> loc = new ArrayList<Localization>();
	
	public static final String getLocalized(String unloc, Object... values) {
		String s = getLocalized(unloc);
		if(s != null) {
			return String.format(s, values);
		}
		return null;
	}
	
	public static final String getLocalized(String unloc) {
		for(Localization l : loc) {
			if(l.unlocName.equals(unloc)) {
				return l.locName;
			}
		}
		return null;
	}
	
	public static final void loadLocalizations(String language) {
		try {
			Properties p = new Properties();
			p.load(Localization.class.getResourceAsStream("/lang/" + language + ".lang"));
			loc.clear();
			for(Object s : p.keySet()) {
				if(s instanceof String) {
					String key = (String) s;
					String value = p.getProperty(key);
					loc.add(new Localization(key, value));
				}
			}
		} catch(Exception e) { Nilplace.err(e); }
	}
	
}