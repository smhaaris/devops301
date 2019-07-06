package com.mindtree.cpe.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mindtree.cpe.dto.JenkinsJobDto;
import com.mindtree.cpe.exception.ConfigException;
import com.mindtree.cpe.util.MongoConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
import org.apache.log4j.Logger;


/**
 * @author Abhilash Hegde
 *
 */
@Repository
public class BuildDao {
	private static final Logger LOGGER = Logger.getLogger(BuildDao.class.getName());
	private MongoTemplate mongoTemplate;


	public BuildDao() {
		try {
			mongoTemplate = MongoConfig.mongoTemplate();
		} catch (ConfigException e) {
			LOGGER.error("Error in getting Mongo template");
			e.printStackTrace();
		}
	}


	public void buildStart(String jobName, long startTime) {
		JenkinsJobDto job = mongoTemplate.findOne(query((where("name").is(jobName))), JenkinsJobDto.class);
		job.setRunning(true);
		job.setStartTime(startTime);
		mongoTemplate.save(job);
	}

	public JenkinsJobDto buildCompleted(String jobName) {
		JenkinsJobDto job = mongoTemplate.findOne(query((where("name").is(jobName))), JenkinsJobDto.class);
		System.out.println(job.toString());
		job.setRunning(false);
		System.out.println(job.toString());
		mongoTemplate.save(job);
		return job;
	}

	public List<JenkinsJobDto> getRunningJobs() {
		List<JenkinsJobDto> job = mongoTemplate.find(query((where("running").is(true))), JenkinsJobDto.class);
		return job;
	}

}
