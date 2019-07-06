package com.mindtree.cpe.util;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mindtree.cpe.exception.ConfigException;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfig.class.getName());

	public static @Bean MongoDbFactory mongoDbFactory() throws ConfigException {
		try {

			if (DatabaseUtil.DB_AUTH_ENABLED) {
				MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(DatabaseUtil.DB_USER,
						DatabaseUtil.DB_NAME, DatabaseUtil.DB_PASSWORD.toCharArray());
				MongoClient mongoClient = new MongoClient(new ServerAddress(DatabaseUtil.DB_HOST, DatabaseUtil.DB_PORT),
						Arrays.asList(mongoCredential));
				SimpleMongoDbFactory mongoFactory = new SimpleMongoDbFactory(mongoClient, DatabaseUtil.DB_NAME);
				return mongoFactory;

			} else {
				SimpleMongoDbFactory mongoFactory = new SimpleMongoDbFactory(
						new MongoClient(DatabaseUtil.DB_HOST, DatabaseUtil.DB_PORT), DatabaseUtil.DB_NAME);
				return mongoFactory;
			}

		} catch (Exception e) {
			LOGGER.error("Error in obtaining MongoDBFactory");
			throw new ConfigException(e.getMessage());
		}
	}

	public static @Bean MongoTemplate mongoTemplate() throws ConfigException {

		MongoTemplate template = new MongoTemplate(mongoDbFactory());
		return template;
	}
}