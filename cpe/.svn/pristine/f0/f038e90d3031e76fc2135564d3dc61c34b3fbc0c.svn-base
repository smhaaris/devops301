package com.mindtree.cpe.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.mindtree.cpe.entity.JenkinsConfig;
import com.mindtree.cpe.entity.JenkinsUrl;
import com.mindtree.cpe.entity.JenkinsUser;
import com.mindtree.cpe.exception.ConfigException;
import com.mindtree.cpe.util.MongoConfig;

/**
 * @author Abhilash Hegde
 *
 */
@Repository
public class JenkinsConfigDao {
	private static final Logger LOGGER = Logger.getLogger(BuildDao.class.getName());
	private MongoTemplate mongoTemplate;

	public JenkinsConfigDao() {
		try {
			mongoTemplate = MongoConfig.mongoTemplate();
		} catch (ConfigException e) {
			LOGGER.error("Error in getting Mongo template");
			e.printStackTrace();
		}
	}

	public boolean collectionExists(String collectionName) {
		Set<String> collectionNames = mongoTemplate.getCollectionNames();
		for (final String name : collectionNames) {
			if (name.equalsIgnoreCase(collectionName)) {
				return true;
			}
		}
		return false;
	}

	public JenkinsUser saveJenkinsUser(JenkinsUser user) {
		mongoTemplate.save(user);
		return user;
	}

	public JenkinsConfig saveJenkinsConfig(JenkinsConfig config) {
		mongoTemplate.save(config);
		return config;
	}

	public JenkinsUrl saveJenkinsUrl(JenkinsUrl url) {
		JenkinsUrl existingUrl = mongoTemplate.findOne(query((where("user").is("user"))), JenkinsUrl.class);
		existingUrl.getHostname();
		mongoTemplate.save(url);
		return url;
	}

	public JenkinsUrl getJenkinsUrl() {
		JenkinsUrl url = mongoTemplate.findOne(query((where("user").is("user"))), JenkinsUrl.class);
		return url;
	}

	public JenkinsUser getJenkinsUser() {
		JenkinsUser user = mongoTemplate.findOne(query((where("user").is("user"))), JenkinsUser.class);
		return user;
	}

	public JenkinsConfig getJenkinsConfig() {
		JenkinsConfig config = mongoTemplate.findOne(query((where("user").is("user"))), JenkinsConfig.class);
		return config;
	}

	public boolean isDataAlreadyPresent() {
		JenkinsConfig config = mongoTemplate.findOne(query((where("user").is("user"))), JenkinsConfig.class);
		if (config.getJenkinsPath() == null || config.getJmeterHome() == null) {
			return false;
		} else {
			return true;
		}

	}
}
