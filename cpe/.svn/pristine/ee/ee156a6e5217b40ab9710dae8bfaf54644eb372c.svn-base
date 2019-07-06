package com.mindtree.cpe.dto;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Abhilash Hegde
 *
 */
@Entity
@Document(collection = "jobs")
public class JenkinsJobDto {

	@Id
	private String name;
	private String applicationName;
	private long startTime;
	private boolean running;
	private List<ServerDetailsDto> servers;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public List<ServerDetailsDto> getServers() {
		return servers;
	}
	public void setServers(List<ServerDetailsDto> servers) {
		this.servers = servers;
	}
	
	@Override
	public String toString() {
		return "JenkinsJobDto [name=" + name + ", applicationName=" + applicationName + ", startTime=" + startTime
				+ ", running=" + running + ", servers=" + servers + "]";
	}
	
	
}