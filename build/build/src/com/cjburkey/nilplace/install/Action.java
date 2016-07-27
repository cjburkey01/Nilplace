package com.cjburkey.nilplace.install;

@FunctionalInterface
public interface Action {
	
	public abstract void call(String[] args);
	
}