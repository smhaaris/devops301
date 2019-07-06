package com.mindtree.cpe.entity;

import javax.persistence.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Entity
@Document(collection = "url")
public class JenkinsUrl {
	
	@Id
	private String user;
	private String hostname;
	private String port;
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "Url [hostname=" + hostname + ", port=" + port + "]";
	}
	
	
}
