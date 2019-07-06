package com.mindtree.cpe.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mindtree.cpe.exception.ConfigException;

public class JenkinsConfigUtil {
	public static String USER_NAME;
	public static String PASSWORD;
	public static String HOST_NAME;
	public static String PORT;
	public static String JMF;
	private static boolean loaded = false;
	
	public void loadproperties() throws ConfigException {
		if (!loaded) {
			String CONFIG_LOCATION = "properties/db_properties.properties";
			
			System.out.println("db properties loaded");
			Properties prop = null;
			prop = new Properties();
			InputStream is = null;

			try {
				is = this.getClass().getClassLoader().getResourceAsStream(CONFIG_LOCATION);
				prop.load(is);

				if (prop.containsKey("username"))
					USER_NAME = prop.getProperty("username");
				if (prop.containsKey("password"))
					PASSWORD = prop.getProperty("password");
				if (prop.containsKey("hostname"))
					HOST_NAME = prop.getProperty("hostname");
				if (prop.containsKey("port"))
					PORT = prop.getProperty("port");
				if (prop.containsKey("jmf"))
					JMF = prop.getProperty("jmf");
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
