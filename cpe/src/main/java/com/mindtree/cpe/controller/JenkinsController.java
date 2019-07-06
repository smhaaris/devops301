package com.mindtree.cpe.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.xml.sax.SAXException;
import com.google.gson.Gson;
import com.mindtree.cpe.dto.JenkinsConfigDto;
import com.mindtree.cpe.entity.JenkinsConfig;
import com.mindtree.cpe.entity.JenkinsJob;
import com.mindtree.cpe.entity.JenkinsNode;
import com.mindtree.cpe.entity.JenkinsPipeline;
import com.mindtree.cpe.entity.JenkinsUrl;
import com.mindtree.cpe.entity.JenkinsUser;
import com.mindtree.cpe.model.JenkinsItems;
import com.mindtree.cpe.service.JenkinsService;

@Controller
public class JenkinsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsController.class);
	@Autowired
	JenkinsService jenkinsService;

	public JenkinsController() {
		LOGGER.info("JenkinsController constructor");
	}


	@CrossOrigin
	@RequestMapping("/")
	public String indexPage() {
		System.out.println("in index hml");
		return "redirect:/index.html";
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/createJob", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> createJob(@RequestBody JenkinsJob job, HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		System.out.println("createJob");
		System.out.println(job);
		//System.out.println("proxy = " + job.getProxyAddress());
		String baseUrl = String.format("%s://%s:%d/cpe", request.getScheme(),  request.getServerName(), request.getServerPort());

		Boolean isUpdating = false;
		try {
			String responseMsg=jenkinsService.createJob(job, isUpdating, baseUrl);
			if(responseMsg == "true") return ResponseEntity.ok("OK");
			else return (ResponseEntity<String>) ResponseEntity.badRequest();

		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/checkJobName", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody String checkJobName(@RequestBody String jobName, HttpServletResponse response)
			throws IOException {			
		System.out.println("checkJobName");
		String jobExists=jenkinsService.checkJobName(jobName);
		return jobExists;
	}

	@CrossOrigin
	@RequestMapping(value = "/getAllJobs", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getAllJobs()
			throws IOException, ParseException {
		Gson gson =new Gson();
		JenkinsItems jobs=jenkinsService.getAllJobs();

		System.out.println(jobs);
		return gson.toJson(jobs, JenkinsItems.class);
	}


	@CrossOrigin
	@RequestMapping(value = "/getJobDetails", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<String> getJobDetails(@RequestParam String jobName)
			throws IOException, ParseException, SAXException, ParserConfigurationException {
		Gson gson =new Gson();

		try {
			JenkinsJob jobDetails=jenkinsService.getJobDetails(jobName);	
			System.out.println("jobdetails = "+jobDetails);
			return ResponseEntity.ok(gson.toJson(jobDetails));
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/updateJob", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<String> updateJob(@RequestBody JenkinsJob job, HttpServletResponse response, HttpServletRequest request)
			throws IOException, ParseException {
		System.out.println(job);
		Boolean isUpdating = true;
		String baseUrl = String.format("%s://%s:%d/cpe", request.getScheme(),  request.getServerName(), request.getServerPort());
		try {
			String responseMsg=jenkinsService.createJob(job, isUpdating, baseUrl);
			if(responseMsg == "true") return ResponseEntity.ok("OK");
			else return (ResponseEntity<String>) ResponseEntity.badRequest();

		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/createPipeline", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> createPipeline(@RequestBody JenkinsPipeline pipeline, HttpServletResponse response)
			throws IOException, TransformerException {
		System.out.println(pipeline);
		Boolean isUpdating = false;	
		try {
			String responseMsg=jenkinsService.createPipeline(pipeline, isUpdating);
			if(responseMsg == "true") return ResponseEntity.ok("OK");
			else return (ResponseEntity<String>) ResponseEntity.badRequest();	
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}


	@CrossOrigin
	@RequestMapping(value = "/checkPipelineName", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody String checkPipelineName(@RequestBody String jobName, HttpServletResponse response)
			throws IOException {			
		System.out.println("checkPipelineName");
		String jobExists=jenkinsService.checkJobName(jobName);
		return jobExists;
	}

	@CrossOrigin
	@RequestMapping(value = "/getPipelineDetails", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<String> getPipelineDetails(@RequestParam String pipelineName)
			throws Exception {
		Gson gson =new Gson();
		try {
			JenkinsPipeline pipeline=jenkinsService.getPipelineDetails(pipelineName);		
			return ResponseEntity.ok( gson.toJson(pipeline));
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getAllPipelineStages", method = RequestMethod.GET,  produces = "application/json")
	public @ResponseBody @ResponseStatus ResponseEntity<String> getAllPipelineStages(@RequestParam String pipelineName)
			throws IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		Gson gson = new Gson();
		try {
			ArrayList pipeline=jenkinsService.getAllPipelineStages(pipelineName);		
			return ResponseEntity.ok( gson.toJson(pipeline));
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/updatePipeline", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<String> updatePipeline(@RequestBody JenkinsPipeline pipeline, HttpServletResponse response)
			throws IOException, ParseException, TransformerException {
		Boolean isUpdating = true;
		try {
			String responseMsg=jenkinsService.createPipeline(pipeline, isUpdating);
			if(responseMsg == "true") return ResponseEntity.ok("OK");
			else return (ResponseEntity<String>) ResponseEntity.badRequest();	
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping(value = "/addJobToExistingPipeline", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<String> addJobToExistingPipeline(@RequestBody JenkinsPipeline pipeline, HttpServletResponse response)
			throws IOException, ParseException, TransformerException {
		Boolean isUpdating = true;
		try {
			String responseMsg=jenkinsService.createPipeline(pipeline, isUpdating);
			if(responseMsg == "true") return ResponseEntity.ok("OK");
			else return (ResponseEntity<String>) ResponseEntity.badRequest();	
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/deleteItem", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<String> deleteItem(@RequestBody String itemName)
			throws IOException, ParseException {	
		try {
			jenkinsService.deleteItem(itemName);
			return ResponseEntity.ok("OK");
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/updateUrl", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> updateUrl(@RequestBody JenkinsUrl url, HttpServletResponse response)
			throws IOException {				
		try {
			jenkinsService.updateUrl(url);
			return ResponseEntity.ok("OK");
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> updateUser(@RequestBody JenkinsUser user, HttpServletResponse response)
	{			
		try {
			jenkinsService.updateUser(user);
			return ResponseEntity.ok("OK");
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/updateConfig", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> updateConfig(@RequestBody JenkinsConfig config, HttpServletResponse response)
			throws IOException {			
		try {
			jenkinsService.updateConfig(config);
			return ResponseEntity.ok("OK");
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getConfigDetails", method = RequestMethod.GET,  produces = "application/json")
	public @ResponseBody ResponseEntity<String> getConfigDetails(){			
		System.out.println("getConfigdetails");
		Gson gson =new Gson();
		try {
			JenkinsConfigDto conf=jenkinsService.getConfigDetails();	
			return ResponseEntity.ok(gson.toJson(conf, JenkinsConfigDto.class));
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/createNode", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> createNode(@RequestBody JenkinsNode node, HttpServletResponse response) throws IOException, InterruptedException{			
		Boolean isUpdating = false;
		try {
			System.out.println(node.toString());
			jenkinsService.createNode(node, isUpdating);
			return ResponseEntity.ok("OK");
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getAllNodes", method = RequestMethod.GET,  produces = "application/json")
	public @ResponseBody @ResponseStatus ResponseEntity<String> getAllNodes()
			throws IOException {
		Gson gson = new Gson();
		try {
			ArrayList<String> nodeList=jenkinsService.getAllNodes();
			System.out.println(nodeList);
			return ResponseEntity.ok( gson.toJson(nodeList));
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getNodeDetails", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<String> getNodeDetails(@RequestParam String nodeName)
			throws IOException, ParseException, SAXException, ParserConfigurationException {
		Gson gson =new Gson();		
		try {
			JenkinsNode node=jenkinsService.getNodeDetails(nodeName);	
			return ResponseEntity.ok(gson.toJson(node));
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/updateNode", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> updateNode(@RequestBody JenkinsNode node, HttpServletResponse response) throws IOException, InterruptedException{			
		Boolean isUpdating = true;		
		try {
			jenkinsService.createNode(node, isUpdating);
			return ResponseEntity.ok("OK");
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/deleteNode", method = RequestMethod.POST,  produces = "application/json")
	public @ResponseBody  ResponseEntity<String> deleteNode(@RequestParam String nodeName)
			throws IOException, InterruptedException {			
		try {
			jenkinsService.deleteNode(nodeName);
			return ResponseEntity.ok("OK");
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}


	@CrossOrigin
	@RequestMapping(value = "/downloadSlaveAgent", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<String> downloadSlaveAgent(@RequestParam String slaveName)
			throws IOException, ParseException, SAXException, ParserConfigurationException {
		try {
			String slaveAgent=jenkinsService.downloadSlaveAgent(slaveName);
			return ResponseEntity.ok(slaveAgent);
		}catch (IOException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}		
	}
}
