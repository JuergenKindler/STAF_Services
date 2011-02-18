package org.jki.staf.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read information about version from Maven generated metadata.
 */
public class VersionReader {
	private static final String ARTIFACT_ID = "artifactId";
	private static final String GROUP_ID = "groupId";
	private static final String VERSION = "version";
	private final static String FILE = "/version.properties";
	private Properties versionProperties;
	
	/**
	 * Create a new reader and load the properties.
	 */
	public VersionReader() {
		versionProperties = new Properties();
		
		try {
			Class clasz = this.getClass();
			InputStream is = clasz.getResourceAsStream(FILE);
			versionProperties.load(is);
			
		} catch (IOException e) {
			e.printStackTrace();
			// In case we have a problem, we simply fake the properties >;->
			versionProperties.put(VERSION, "Unknown");
			versionProperties.put(GROUP_ID, "Unknown");
			versionProperties.put(ARTIFACT_ID, "Unknown");
		}
	}
	
	/**
	 * Get the version.
	 * @return the version stored.
	 */
	public final String getVersion() {
		return (String) versionProperties.get(VERSION);
	}
	
	/**
	 * Get the group id.
	 * @return the group id
	 */
	public final String getGroupId() {
		return (String) getProperty(GROUP_ID);
	}
	
	/**
	 * Get the artifact id.
	 * @return the artifact id
	 */
	public final String getArtifactId() {
		return (String) getProperty(ARTIFACT_ID);
	}

	/**
	 * Get any property by name
	 * @param key - the key of the property to be fetched
	 * @return the value or null
	 */
	public final Object getProperty(final String key) {
		return versionProperties.getProperty(key);
	}
}
