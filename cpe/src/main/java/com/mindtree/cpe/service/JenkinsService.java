package com.mindtree.cpe.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.mindtree.cpe.dao.JenkinsConfigDao;
import com.mindtree.cpe.dao.JenkinsJobDao;
import com.mindtree.cpe.dto.JenkinsConfigDto;
import com.mindtree.cpe.dto.JenkinsJobDto;
import com.mindtree.cpe.entity.JenkinsConfig;
import com.mindtree.cpe.entity.JenkinsJob;
import com.mindtree.cpe.entity.JenkinsNode;
import com.mindtree.cpe.entity.JenkinsPipeline;
import com.mindtree.cpe.entity.JenkinsUrl;
import com.mindtree.cpe.entity.JenkinsUser;
import com.mindtree.cpe.exception.JenkinsForbiddenException;
import com.mindtree.cpe.model.JenkinsItems;
import com.mindtree.cpe.util.ErrorHandler;
import com.mindtree.cpe.util.XmlHandler;

//from jenkinsapi.utils.crumb_requester import CrumbRequester


/**
 * @author Abhilash Hegde
 *
 */
@Service
public class JenkinsService{

	JenkinsUrl jenkinsUrl;
	JenkinsUser jenkinsUser;
	JenkinsConfig  jenkinsConfig;

	@Autowired
	JenkinsConfigDao jenkinsConfigDao;

	@Autowired
	JenkinsJobDao jenkinsJobDao;

	@Autowired
	XmlHandler xmlHandler;

	@Autowired
	ErrorHandler errorHandler;

	public String createJob(JenkinsJob jenkinsJob, Boolean isUpdating, String baseUrl) throws IOException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		int responseCode=00;
		String jobCreated= "false";
		String response;	
		URL url;
		if(!isUpdating) {
			url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/createItem?name="+jenkinsJob.getName());
		} else {
			url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/job/"+jenkinsJob.getName()+"/config.xml");
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/xml");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		String xml = xmlHandler.createJobXml(jenkinsJob, jenkinsConfig, baseUrl);
		OutputStream os = conn.getOutputStream();
		os.write(xml.getBytes());
		os.flush();
		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		conn.disconnect();
		System.out.println(responseCode);
		if(responseCode == 200) {
			JenkinsJobDto JenkinsJobDto = new JenkinsJobDto();
			JenkinsJobDto.setName(jenkinsJob.getName());
			JenkinsJobDto.setApplicationName(jenkinsJob.getApplicationName());
			JenkinsJobDto.setServers(jenkinsJob.getServers());
			jenkinsJobDao.saveJob(JenkinsJobDto);
			jobCreated = checkJobName(jenkinsJob.getName());
			return jobCreated;

		} else {
			errorHandler.throwError(responseCode);
			return null;
		}
	}

	public String createPipeline(JenkinsPipeline jenkinsPipeline, Boolean isUpdating) throws IOException, TransformerException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();

		int responseCode=00;
		String jobCreated= "false";
		String response;
		URL url;
		String xml;
		if(!isUpdating) {
			url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/createItem?name="+jenkinsPipeline.getName());

			xml = xmlHandler.createPipelineXml(jenkinsPipeline);
		}else {
			url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/job/"+jenkinsPipeline.getName()+"/config.xml");
			String xmlResponse = getPipelineAPI(jenkinsPipeline.getName());
			xml = xmlHandler.updatePipelineXml(jenkinsPipeline, xmlResponse);
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/xml");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		OutputStream os = conn.getOutputStream();
		os.write(xml.getBytes());
		os.flush();
		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		conn.disconnect();
		if(responseCode == 200) {
			jobCreated = checkJobName(jenkinsPipeline.getName());
			return jobCreated;

		} else {
			errorHandler.throwError(responseCode);
			return null;
		}
	}


	public String checkJobName(String jobName) throws IOException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();

		int responseCode=00;
		boolean jobExists = false;
		String output;
		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/checkJobName?value="+jobName);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);
		OutputStream os = conn.getOutputStream();
		os.flush();
		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
			if(output.contains("A job already exists with the name")) {
				jobExists= true;
			}
		}
		conn.disconnect();

		if(responseCode==200) {
			if(jobExists) {
				return "true";
			}
			else
				return "false";
		}else
			errorHandler.throwError(responseCode);
		return null;

	}

	public static String encode(String url)  
	{  
		try {  
			String encodeURL=URLEncoder.encode( url, "UTF-8" );  
			return encodeURL;  
		} catch (UnsupportedEncodingException e) {  
			return "Issue while encoding" +e.getMessage();  
		}  
	}

	public JenkinsItems getAllJobs( ) throws IOException  {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		ArrayList<String> jobs = new ArrayList<String>();
		ArrayList<String> pipelines = new ArrayList<String>();
		int responseCode=00;
		String jsonStr =null;
		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/api/json?tree=jobs[name]");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		
		
		
		conn.setRequestProperty ("Authorization", basicAuth);

		OutputStream os = conn.getOutputStream();
		os.flush();
		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		System.out.println("Fetching jobs from jenkins .... \n");
		String output;
		while (( output = br.readLine()) != null) {
			if(output!=null && output !="")
				output= output.substring(40, output.length()-2);
			jsonStr=output;
		}
		conn.disconnect();

		if(responseCode == 200) {
			JSONArray jsonarr = new JSONArray("["+jsonStr+"]");
			for(int i = 0; i < jsonarr.length(); i++){
				JSONObject jsonobj = jsonarr.getJSONObject(i);
				if(jsonobj.getString("_class").equalsIgnoreCase("hudson.model.FreeStyleProject"))
					jobs.add(jsonobj.getString("name"));
				else if(jsonobj.getString("_class").equalsIgnoreCase("org.jenkinsci.plugins.workflow.job.WorkflowJob"))
					pipelines.add(jsonobj.getString("name"));
			}
			JenkinsItems jenkinsItems= new JenkinsItems();
			jenkinsItems.setJobs(jobs);
			jenkinsItems.setPipelines(pipelines);

			return jenkinsItems;
		} else {
			errorHandler.throwError(responseCode);
			return null;
		}
	}

	public JenkinsJob getJobDetails(String jobName) throws IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();

		JenkinsJob jenkinsJob=null;
		try {
			URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/job/"+jobName+"/config.xml");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty ("Authorization", basicAuth);

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();
			System.out.println(response);

			jenkinsJob = xmlHandler.parseXml(response, jobName);
			jenkinsJob.setServers(jenkinsJobDao.getJob(jobName).getServers());
			jenkinsJob.setApplicationName(jenkinsJobDao.getJob(jobName).getApplicationName());

		}catch (MalformedURLException e) {        
			e.printStackTrace();        
		} catch (IOException e) {        
			e.printStackTrace();        
		}
		return jenkinsJob;
	}

	public void updateUrl(JenkinsUrl url) {
		jenkinsConfigDao.saveJenkinsUrl(url);
	}

	public void updateUser(JenkinsUser user) {
		jenkinsConfigDao.saveJenkinsUser(user);

	}

	public void updateConfig(JenkinsConfig config) {
		jenkinsConfigDao.saveJenkinsConfig(config);

	}

	public JenkinsConfigDto getConfigDetails() {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		return new JenkinsConfigDto(jenkinsUrl, jenkinsUser, jenkinsConfig);
	}

	public void createNode(JenkinsNode node, Boolean isUpdating) throws IOException, InterruptedException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		int responseCode=00;
		String url;
		if(!isUpdating) {
			url = "http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/computer/doCreateItem?name="+node.getName()+"&type=hudson.slaves.DumbSlave";
		}else {
			url = "http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/computer/"+node.getName()+"/config.xml";
		}

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");

		if(isUpdating) {
			conn.setRequestProperty("Content-Type", "application/xml");
		}else {
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		}

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		if(isUpdating) {
			String xml = xmlHandler.createNodeXml(node);
			OutputStream os = conn.getOutputStream();
			os.write(xml.getBytes());
			os.flush();
		}else {
			String json = xmlHandler.createNodeJson(node);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(json);
			out.close();
		}

		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			//				System.out.println(output);
		}

		conn.disconnect();

		if(responseCode != 200) {
			errorHandler.throwError(responseCode);
		}
	}

	public void deleteNode(String nodeName) throws IOException, InterruptedException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		int responseCode=00;
		String url = "http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/computer/"+nodeName+"/doDelete";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("POST");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			//System.out.println(output);
		}   
		conn.disconnect();
		if(responseCode != 200) {
			errorHandler.throwError(responseCode);
		}
	}

	public ArrayList<String> getAllNodes() throws IOException, JenkinsForbiddenException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		int responseCode = 00;
		String jsonStr = null;
		ArrayList<String> nodeList = new ArrayList<String>();

		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/computer/api/json?tree=computer[displayName]");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		OutputStream os = conn.getOutputStream();
		os.flush();
		responseCode = conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		System.out.println("Fetching jobs from jenkins .... \n");
		String output;
		while (( output = br.readLine()) != null) {
			if(output!=null && output !="")
				output= output.substring(48, output.length()-1);
			jsonStr=output;

		}
		conn.disconnect();

		if(responseCode == 200) {
			JSONArray jsonarr = new JSONArray(jsonStr);
			for(int i = 0; i < jsonarr.length(); i++){
				JSONObject jsonobj = jsonarr.getJSONObject(i);
				if(!jsonobj.getString("displayName").equals("master"))
					nodeList.add(jsonobj.getString("displayName"));
			}
			System.out.println(nodeList.toString());
			return nodeList;
		} else {
			System.out.println(responseCode);
			errorHandler.throwError(responseCode);
		}
		return null;
	}


	public JenkinsPipeline getPipelineDetails(String pipelineName) 
			throws IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		JenkinsPipeline pipeline;
		String response = getPipelineAPI(pipelineName);
		System.out.println(response);
		if(response != null) {
			//			if(xmlHandler.parsePipelineStagesXml(response).contains("Performance Test"))
			pipeline = xmlHandler.parsePipelineXml(response, pipelineName);
			//			else {
			//				System.out.println("Performance-stage is not added to '"+ pipelineName + "'");
			//				throw new Error("Performance-stage is not added to '"+ pipelineName + "'");
			//			}
			return pipeline;
		}
		return null;
	}


	public ArrayList getAllPipelineStages(String pipelineName) throws IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		ArrayList pipelineStages;
		String response = getPipelineAPI(pipelineName);
		System.out.println(response);
		if(response != null) {
			pipelineStages = xmlHandler.parsePipelineStagesXml(response);
			return pipelineStages;
		}
		return null;
	}

	private String getPipelineAPI(String pipelineName) throws IOException {	
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/job/"+pipelineName+"/config.xml");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		//		conn.setRequestProperty("Content-Type", "application/xml");
		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		int responseCode = conn.getResponseCode();
		System.out.println("Response Code : " + responseCode);


		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			response.append(inputLine);
		}
		in.close();

		//		File file = new File("D://test.xml"); // If you want to write as file to local.
		//		FileWriter fileWriter = new FileWriter(file);
		//		fileWriter.write(response.toString());
		//		fileWriter.close();
		conn.disconnect();
		if(responseCode == 200) {
			System.out.println(response);
			return response.toString();
		}else {
			errorHandler.throwError(responseCode);
			return null;
		}

	}


	public void deleteItem(String itemName) throws IOException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();
		int responseCode = 00;
		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/job/"+itemName+"/doDelete");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/xml");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		conn.disconnect();

		if(responseCode != 200) {
			errorHandler.throwError(responseCode);
		}
	}


	public JenkinsNode getNodeDetails(String nodeName) throws IOException {
		jenkinsUrl = jenkinsConfigDao.getJenkinsUrl();
		jenkinsUser = jenkinsConfigDao.getJenkinsUser();
		jenkinsConfig = jenkinsConfigDao.getJenkinsConfig();

		JenkinsNode node = null;
		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/computer/"+nodeName+"/config.xml");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		int responseCode = conn.getResponseCode();
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		conn.disconnect();
		System.out.println(response);

		node = xmlHandler.parseNodeXml(response, nodeName);

		return node;
	}


	public String downloadSlaveAgent(String slaveName) throws IOException {
		int responseCode=00;
		String slaveAgent =null;
		URL url = new URL("http://"+jenkinsUrl.getHostname()+":"+jenkinsUrl.getPort()+"/computer/"+slaveName+"/slave-agent.jnlp");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");

		String userpass = jenkinsUser.getUsername() + ":" + jenkinsUser.getPassword();
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty ("Authorization", basicAuth);

		OutputStream os = conn.getOutputStream();
		os.flush();
		responseCode= conn.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		String output;
		while (( output = br.readLine()) != null) {
			if(output != null && output !="") {
				slaveAgent = output;
			}
			System.out.println(output);
		}
		conn.disconnect();

		if(responseCode == 200) {
			return slaveAgent;
		} else {
			errorHandler.throwError(responseCode);
			return null;
		}
	}
}