package com.mindtree.cpe.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mindtree.cpe.entity.JenkinsConfig;
import com.mindtree.cpe.entity.JenkinsUrl;
import com.mindtree.cpe.entity.JenkinsUser;
import com.mindtree.cpe.exception.ConfigException;
import com.mindtree.cpe.service.JenkinsConfigService;
import com.mindtree.cpe.util.DatabaseUtil;
import com.mindtree.cpe.util.JenkinsConfigUtil;

/**
 * @author Abhilash Hegde
 *
 */
@Controller
public class AppController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);
	private static boolean loaded = false;
	
	@Autowired
	JenkinsConfigService jenkinsConfigService;
	
	public AppController() {
		DatabaseUtil dbUtil = new DatabaseUtil();
		try {
			dbUtil.loadDBProperties();
		} catch (ConfigException e) {
			e.printStackTrace();
			logger.error("Error while loading database properties file, " + e.getMessage());
		}	
		
	}
	
	@PostConstruct
	public void readConfigFiles() throws ConfigException {
		jenkinsConfigService.readJenkinsConfig();		
	}

}
