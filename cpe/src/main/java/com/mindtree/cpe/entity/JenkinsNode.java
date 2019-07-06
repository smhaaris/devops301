package com.mindtree.cpe.entity;

import javax.persistence.Entity;

@Entity
public class JenkinsNode {
	private String name;
	private String description;
	private String rootDirectory;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRootDirectory() {
		return rootDirectory;
	}
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
}
