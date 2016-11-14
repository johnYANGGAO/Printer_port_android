package com.bjut.printer.bean;


public class FileInfo   implements Comparable<FileInfo>{
	//filename
	private String name;
	//if is dir
	private boolean isdir;


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setIsdir(boolean isdir) {
		this.isdir = isdir;
	}
	public boolean isIsdir() {
		return isdir;
	}

	@Override
	public int compareTo(FileInfo another) {
		// TODO Auto-generated method stub
		String name1=this.getName().toLowerCase();
		String name2=another.getName().toLowerCase();
		return name1.compareTo(name2);
	}
}
	

