package com.mindtree.cpe.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Logger;
import com.mindtree.cpe.exception.ConfigException;

public class DatabaseUtil {

		public static String DB_HOST;
		public static Integer DB_PORT;
		public static String DB_NAME;
		public static Boolean DB_AUTH_ENABLED;
		public static String DB_USER;
		public static String DB_PASSWORD;
		private static boolean loaded = false;

		public DatabaseUtil() {
		}

		public void loadDBProperties() throws ConfigException {
			if (!loaded) {
				String CONFIG_LOCATION = "properties/db_properties.properties";
				
				System.out.println("db properties loaded");
				Properties prop = null;
				prop = new Properties();
				InputStream is = null;

				try {
					is = this.getClass().getClassLoader().getResourceAsStream(CONFIG_LOCATION);
					prop.load(is);

					if (prop.containsKey("DB_HOST"))
						DB_HOST = prop.getProperty("DB_HOST");
					if (prop.containsKey("DB_PORT"))
						DB_PORT = Integer.parseInt(prop.getProperty("DB_PORT"));
					if (prop.containsKey("DB_NAME"))
						DB_NAME = prop.getProperty("DB_NAME");
					if (prop.containsKey("DB_AUTH_ENABLED"))
						DB_AUTH_ENABLED = Boolean.parseBoolean(prop.getProperty("DB_AUTH_ENABLED"));
					if (prop.containsKey("DB_USER"))
						DB_USER = prop.getProperty("DB_USER");
					if (prop.containsKey("DB_PASSWORD"))
						DB_PASSWORD = prop.getProperty("DB_PASSWORD");
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
