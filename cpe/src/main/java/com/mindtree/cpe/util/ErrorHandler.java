package com.mindtree.cpe.util;

import org.springframework.stereotype.Service;

import com.mindtree.cpe.exception.JenkinsBadGateawayException;
import com.mindtree.cpe.exception.JenkinsBadRequestException;
import com.mindtree.cpe.exception.JenkinsForbiddenException;
import com.mindtree.cpe.exception.JenkinsNotFoundException;

@Service
public class ErrorHandler {

	public void throwError(int responseCode) {

		if(responseCode == 404) {
			throw new JenkinsNotFoundException("message: "+ responseCode);
		} else if(responseCode == 400) {
			throw new JenkinsBadRequestException("message: "+ responseCode);
		} else if(responseCode == 502) {
			throw new JenkinsBadGateawayException("message: "+ responseCode);
		} else if(responseCode == 403) {
			throw new JenkinsForbiddenException("message: "+ responseCode);
		}
	}
}