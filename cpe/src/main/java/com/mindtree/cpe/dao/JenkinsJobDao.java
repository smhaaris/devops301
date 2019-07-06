package com.mindtree.cpe.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import com.mindtree.cpe.dto.JenkinsJobDto;
import com.mindtree.cpe.exception.ConfigException;
import com.mindtree.cpe.util.MongoConfig;

@Repository
public class JenkinsJobDao {

	private static final Logger LOGGER = Logger.getLogger(BuildDao.class.getName());
	private MongoTemplate mongoTemplate;


	public JenkinsJobDao() {
		try {
			mongoTemplate = MongoConfig.mongoTemplate();
		} catch (ConfigException e) {
			LOGGER.error("Error in getting Mongo template");
			e.printStackTrace();
		}
	}

	public String saveJob (JenkinsJobDto job) {
		job.setRunning(false);
		job.setStartTime(0);
		mongoTemplate.save(job);
		return  null;		
	}

	public JenkinsJobDto getJob(String jobName) {
		JenkinsJobDto job = mongoTemplate.findOne(query((where("name").is(jobName))), JenkinsJobDto.class);
		System.out.println(job.toString());
		return job;
	}
}
