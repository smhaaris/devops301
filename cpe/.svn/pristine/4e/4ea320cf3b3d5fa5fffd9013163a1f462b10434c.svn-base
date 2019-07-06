package com.mindtree.cpe.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindtree.cpe.controller.AppController;
import com.mindtree.cpe.dao.JenkinsConfigDao;
import com.mindtree.cpe.entity.JenkinsConfig;
import com.mindtree.cpe.entity.JenkinsUrl;
import com.mindtree.cpe.entity.JenkinsUser;
import com.mindtree.cpe.exception.ConfigException;

/**
 * @author Abhilash Hegde
 *
 */
@Service
public class JenkinsConfigService {
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);
	private static boolean loaded = false;

	@Autowired
	JenkinsConfigDao dao;

	public void readJenkinsConfig() throws ConfigException {

		if (!dao.isDataAlreadyPresent()) {
			String JENKINS_CONFIG = "properties/jenkins_config.properties";
			System.out.println("jenkins_config properties loaded");
			Properties properties = null;
			properties = new Properties();
			InputStream is = null;
			JenkinsUrl jenkinsUrl = new JenkinsUrl();
			JenkinsUser jenkinsUser = new JenkinsUser();
			JenkinsConfig jenkinsConfig = new JenkinsConfig();

			try {
				is = this.getClass().getClassLoader().getResourceAsStream(JENKINS_CONFIG);
				properties.load(is);

				// if(!dao.collectionExists("user")) {
				// jenkinsUser.setUser("user");
				// jenkinsUser.setUsername(properties.getProperty("username"));
				// jenkinsUser.setPassword(properties.getProperty("password"));
				// dao.saveJenkinsUser(jenkinsUser);
				// }
				//
				// if(!dao.collectionExists("url")) {
				// jenkinsUrl.setUser("user");
				// jenkinsUrl.setHostname(properties.getProperty("hostname"));
				// jenkinsUrl.setPort(properties.getProperty("port"));
				// dao.saveJenkinsUrl(jenkinsUrl);
				// }
				//
				// if(!dao.collectionExists("config")) {
				// jenkinsConfig.setUser("user");
				// jenkinsConfig.setJmf(properties.getProperty("jmf"));
				// jenkinsConfig.setGrafana(properties.getProperty("grafana"));
				// jenkinsConfig.setJenkinsPath("");
				// jenkinsConfig.setJmeterHome("");
				// dao.saveJenkinsConfig(jenkinsConfig);
				// }

				jenkinsUser.setUser("user");
				jenkinsUser.setUsername(properties.getProperty("username"));
				jenkinsUser.setPassword(properties.getProperty("password"));
				dao.saveJenkinsUser(jenkinsUser);

				jenkinsUrl.setUser("user");
				jenkinsUrl.setHostname(properties.getProperty("hostname"));
				jenkinsUrl.setPort(properties.getProperty("port"));
				dao.saveJenkinsUrl(jenkinsUrl);

				jenkinsConfig.setUser("user");
				jenkinsConfig.setJmf(properties.getProperty("jmf"));
				jenkinsConfig.setGrafana(properties.getProperty("grafana"));
				jenkinsConfig.setJenkinsPath("");
				jenkinsConfig.setJmeterHome("");
				dao.saveJenkinsConfig(jenkinsConfig);

				loaded = true;
			} catch (IOException e) {
				throw new ConfigException(e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					throw new ConfigException(e);
				}
			}
		}

	}

}
