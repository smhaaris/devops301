package com.mindtree.cpe.entity;

import javax.persistence.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "config")
public class JenkinsConfig {
	
	@Id
	private String user;
	private String jmf;
	private String grafana;
	private String jenkinsPath;
	private String jmeterHome;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getJmf() {
		return jmf;
	}
	public void setJmf(String jmf) {
		this.jmf = jmf;
	}
	public String getGrafana() {
		return grafana;
	}
	public void setGrafana(String grafana) {
		this.grafana = grafana;
	}
	public String getJenkinsPath() {
		return jenkinsPath;
	}
	public void setJenkinsPath(String jenkinsPath) {
		this.jenkinsPath = jenkinsPath;
	}
	public String getJmeterHome() {
		return jmeterHome;
	}
	public void setJmeterHome(String jmeterHome) {
		this.jmeterHome = jmeterHome;
	}
	@Override
	public String toString() {
		return "JenkinsConfig [user=" + user + ", jmf=" + jmf + ", grafana=" + grafana + ", jenkinsPath=" + jenkinsPath
				+ ", jmeterHome=" + jmeterHome + "]";
	}
}
