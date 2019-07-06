package com.mindtree.cpe.entity;

/**
 * @author Abhilash Hegde
 *
 */
public class JenkinsPipeline {
	private String name;
	private String stageRef;
	private String where;
	private String jobName;
	private String release;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStageRef() {
		return stageRef;
	}
	public void setStageRef(String stageRef) {
		this.stageRef = stageRef;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	
	
	
}