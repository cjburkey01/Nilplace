package com.cjburkey.nilplace;

public enum OS {
	
	WIN,
	MAC,
	LIN,
	OTHER;
	
	public static OS current;
	public static final void init() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")) {
			current = WIN;
		} else if(os.contains("mac")) {
			current = MAC;
		} else if(os.contains("lin")) {
			current = LIN;
		} else {
			current = OTHER;
		}
	}
	
}