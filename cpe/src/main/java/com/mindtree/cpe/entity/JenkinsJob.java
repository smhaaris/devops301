package com.mindtree.cpe.entity;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;

import com.mindtree.cpe.dto.ServerDetailsDto;

@Entity
public class JenkinsJob {
	private String name;
	private String release;
	private boolean baseline;
	private String description;
	private String jmeterScript;
	private String applicationName;
	private String email;
	private boolean successTrigger;
	private boolean failureTrigger;
	private boolean beforebuildTrigger;
	private boolean enableSlave;
	private String slave;
    private boolean enableDistributedT;
    private List<String> remoteHosts;
    private List<ServerDetailsDto> servers;
    private boolean enableProxy;
    private String proxyAddress;
    private int port;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getJmeterScript() {
		return jmeterScript;
	}
	public void setJmeterScript(String jmeterScript) {
		this.jmeterScript = jmeterScript;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isSuccessTrigger() {
		return successTrigger;
	}
	public void setSuccessTrigger(boolean successTrigger) {
		this.successTrigger = successTrigger;
	}
	public boolean isFailureTrigger() {
		return failureTrigger;
	}
	public void setFailureTrigger(boolean failureTrigger) {
		this.failureTrigger = failureTrigger;
	}
	public boolean isBeforebuildTrigger() {
		return beforebuildTrigger;
	}
	public void setBeforebuildTrigger(boolean beforebuildTrigger) {
		this.beforebuildTrigger = beforebuildTrigger;
	}
	public boolean isEnableSlave() {
		return enableSlave;
	}
	public void setEnableSlave(boolean enableSlave) {
		this.enableSlave = enableSlave;
	}
	public String getSlave() {
		return slave;
	}
	public void setSlave(String slave) {
		this.slave = slave;
	}
	public boolean isEnableDistributedT() {
		return enableDistributedT;
	}
	public void setEnableDistributedT(boolean enableDistributedT) {
		this.enableDistributedT = enableDistributedT;
	}
	public List<String> getRemoteHosts() {
		return remoteHosts;
	}
	public void setRemoteHosts(List<String> remoteHosts) {
		this.remoteHosts = remoteHosts;
	}
	public List<ServerDetailsDto> getServers() {
		return servers;
	}
	public void setServers(List<ServerDetailsDto> servers) {
		this.servers = servers;
	}
	
	public boolean isEnableProxy() {
		return enableProxy;
	}
	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}
	public String getProxyAddress() {
		return proxyAddress;
	}
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean getBaseline() {
		return baseline;
	}
	public void setBaseline(boolean baseline) {
		this.baseline = baseline;
	}
	@Override
	public String toString() {
		return "JenkinsJob [name=" + name + ", release=" + release + ", baseline=" + baseline + ", description="
				+ description + ", jmeterScript=" + jmeterScript + ", applicationName=" + applicationName + ", email="
				+ email + ", successTrigger=" + successTrigger + ", failureTrigger=" + failureTrigger
				+ ", beforebuildTrigger=" + beforebuildTrigger + ", enableSlave=" + enableSlave + ", slave=" + slave
				+ ", enableDistributedT=" + enableDistributedT + ", remoteHosts=" + remoteHosts + ", servers=" + servers
				+ ", enableProxy=" + enableProxy + ", proxyAddress=" + proxyAddress + ", port=" + port + "]";
	}
	
	
}
