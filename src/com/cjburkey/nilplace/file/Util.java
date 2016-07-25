package com.cjburkey.nilplace.file;

import java.io.File;
import com.cjburkey.nilplace.Nilplace;

public class Util {
	
	public static final void deleteDir(File dir) {
		Nilplace.log("Deleting dir: '" + dir + "'");
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) { deleteDir(f); }
			if(!f.delete()) { f.deleteOnExit(); }
			Nilplace.log("Deleted: " + f);
		}
		if(!dir.delete()) { dir.deleteOnExit(); }
	}
	
}