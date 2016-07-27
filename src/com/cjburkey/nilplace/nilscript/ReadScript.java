package com.cjburkey.nilplace.nilscript;

import com.cjburkey.nilplace.Nilplace;
import com.cjburkey.nilplace.file.WorkerFile;
import com.cjburkey.nilplace.install.InstallerAction;
import com.cjburkey.nilplace.install.Worker;
import com.cjburkey.nilplace.local.Localization;

public class ReadScript {
	
	public static final void read(String input) {
		String[] lines = input.split("\\;");
		if(lines.length >= 1) {
			for(String line : lines) {
				if(line != null && !line.trim().isEmpty()) {
					readLine(line.trim());
				}
			}
		}
	}
	
	public static final void read(String... input) {
		if(input.length >= 1) {
			for(String s : input) {
				if(s != null && !s.trim().isEmpty()) {
					readLine(s.trim());
				}
			}
		}
	}
	
	public static final void readLine(String line) {
		if(!line.startsWith("//")) {
			String command = line.split("\\(")[0];
			String[] args = line.substring(line.indexOf('(') + 1, line.indexOf(')')).split("\\,");
			if(InstallerAction.hasKey(command)) {
				InstallerAction cmd = InstallerAction.valueOf(command);
				if(args.length == cmd.args.length) {
					//cmd.action.call(args);
					Worker.workers.add(new WorkerFile(cmd, args));
				} else {
					String arguments = "";
					for(String s : cmd.args) {
						arguments += s + ((s != cmd.args[cmd.args.length - 1]) ? ", " : "");
					}
					Nilplace.err(Localization.getLocalized("missingArguments", cmd,
							cmd.args.length, arguments));
				}
			} else {
				Nilplace.err(Localization.getLocalized("cmdNotFound", command));
			}
		}
	}
	
}