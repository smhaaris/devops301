package com.mindtree.cpe.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.mindtree.cpe.dto.JenkinsJobDto;
import com.mindtree.cpe.model.Model;
import com.mindtree.cpe.service.BuildService;

@Controller
public class BuildController {

	@Autowired
	BuildService buildService;
	
	@RequestMapping(value = "/postAPI", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody void postAPI(@RequestBody Model model)
			throws IOException {
		System.out.println(model.getKey());
	}

	@RequestMapping(value = "/buildStart", method = RequestMethod.POST, consumes = "text/html", produces = "text/html")
	public @ResponseBody String buildStart(@RequestParam String jobName)
			throws IOException {
		System.out.println(jobName);
		buildService.buildStart(jobName);


		return "success";
	}

	@RequestMapping(value = "/buildCompleted", method = RequestMethod.GET,  produces = "text/plain")
	public @ResponseBody String buildCompleted(@RequestParam String jobName)
			throws IOException {
		String resp = buildService.buildCompleted(jobName);

		return resp;
	}

	@CrossOrigin
	@RequestMapping(value = "/getRunningJobs", method = RequestMethod.GET,  produces = "application/json")
	public @ResponseBody String getRunningJobs(HttpServletRequest request)
			throws IOException {
		Gson gson =new Gson();
		List<JenkinsJobDto> job= buildService.getRunningJobs();

		return gson.toJson(job);
	}
}
