package com.mindtree.cpe.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mindtree.cpe.dao.BuildDao;
import com.mindtree.cpe.dao.JenkinsConfigDao;
import com.mindtree.cpe.dto.JenkinsJobDto;
import com.mindtree.cpe.dto.ServerDetailsDto;

@Service
public class BuildService {

	@Autowired
	BuildDao buildDao;

	@Autowired
	JenkinsConfigDao jenkinsConfigDao;

	public void buildStart(String jobName) {
		Date date = new Date();
		long startTime = date.getTime();
		buildDao.buildStart(jobName, startTime);
	}

	@SuppressWarnings("unused")
	public String buildCompleted(String jobName) {
		//		JenkinsJobDto jenkinsJobDto = new JenkinsJobDto();
		//		jenkinsJobDto.setRunning(false);
		JenkinsJobDto job = buildDao.buildCompleted(jobName);
		String response = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n" + 
				"\r\n" + 
				"<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\r\n" + 
				"	\r\n" + 
				"	<title></title>\r\n" + 
				"	<meta name=\"GENERATOR\" content=\"OpenOffice 4.1.1  (FreeBSD/amd64)\">\r\n" + 
				"	<meta name=\"CREATED\" content=\"20180718;2094400\">\r\n" + 
				"	<meta name=\"CHANGED\" content=\"0;0\">\r\n" + 
				"	<style type=\"text/css\">\r\n" + 
				"	</style>\r\n" + 
				"</head>\r\n" + 
				"<body lang=\"en-US\" dir=\"LTR\">\r\n" + 
				"<pre class=\"western\"> Dashboard Links: \n";
		Date date = new Date();
		long startTime = job.getStartTime();
		List<ServerDetailsDto> servers= job.getServers();
		String applicationName= job.getApplicationName();
		long endTime = date.getTime();

		HashMap<String, String> urls = new HashMap<String, String> ();


		String  grafanaUrl = jenkinsConfigDao.getJenkinsConfig().getGrafana();

		String jmeterDashboard = "/d/fr--ToImk/jmeter-dashboard?orgId=1&from=" + startTime +
				"&to="+ endTime +"&var-Application=" + applicationName +
				"&var-request=all&var-aggregation=10s&refresh=5s&kiosk=true";

		urls.put("Jmeter Dashboard", grafanaUrl + jmeterDashboard);

		response = response + "Jmeter Dashboard- " + grafanaUrl + jmeterDashboard + "\n\n";


		for(ServerDetailsDto server : servers){
			String hostname= server.getName();

			String correlatedWin = "/d/cvj5jvOmz/windows-correlated-dashboard?orgId=1&from=" + startTime +
					"&to="+ endTime +"&var-hostname=" + hostname + "&var-Application=" + applicationName +
					"&var-request=all&var-aggregation=10s&var-process=All&refresh=5s&kiosk=true";

			String serverWin = "/d/-ZGmrzZiz/windows-host-dashboard?orgId=1&from=" + startTime +
					"&to="+ endTime +"&var-hostname=" + hostname+
					"&var-disk=All&var-process=All&var-network=All&&refresh=5s&kiosk=true";
			String serverLinux = "/d/MfS2FDOmz/linux-host-dashboard?orgId=1&from=" + startTime +
					"&to="+ endTime +"&var-datasource=default&var-server=" + hostname +
					"&var-inter=10s&var-netif=All&refresh=5s&kiosk=true";
			String correlatedLinux = "/d/h39jXzWmz/linux-correlated-dashboard?orgId=1&from=" + startTime +
					"&to="+ endTime +"&var-hostname=" + hostname +
					"&var-Application=" + applicationName +
					"&var-request=checkout&var-aggregation=10s&var-process=All&refresh=5s&kiosk=true";

			if(server.getOs().equalsIgnoreCase("windows")) {
				response = response + "Server: " + hostname + "\n";
				response = response + "Correlated- " +  grafanaUrl + correlatedWin + "\n";
				response = response+ "ServerWindows- "+   grafanaUrl + serverWin + "\n\n";
			}else {
				response = response + "Server: " + hostname + "\n";
				response = response + "Correlated- " +  grafanaUrl + correlatedLinux + "\n";
				response = response+ "ServerLinux- "+   grafanaUrl + serverLinux + "\n\n";
			}
		}
		response = response + "</pre>\r\n" + 
				"\r\n" + 
				"</body></html>";
		return response;

	}

	public List<JenkinsJobDto> getRunningJobs() {
		return  buildDao.getRunningJobs();

	}

}
