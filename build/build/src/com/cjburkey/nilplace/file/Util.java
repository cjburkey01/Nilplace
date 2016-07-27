package com.cjburkey.nilplace.file;

import java.io.File;
import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.local.Localization;

public class Util {
	
	public static final void deleteDir(File dir) {
		Nilplace.log(Localization.getLocalized("deletingDir", dir));
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) { deleteDir(f); }
			if(!f.delete()) { f.deleteOnExit(); }
			Nilplace.log(Localization.getLocalized("deleted", f));
		}
		if(!dir.delete()) { dir.deleteOnExit(); }
	}
	
}