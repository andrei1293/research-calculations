package bp;

import java.util.Properties;

/**
 * Provides access to the application properties.
 * 
 * @author Andrii Kopp
 */
public class AppProperties {
	private Properties properties;

	public static final AppProperties INSTANCE = (AppProperties) AppContext.CONTEXT.getBean("appProperties");

	/**
	 * Returns value of the property according to the specified key.
	 * 
	 * @param key
	 *            - a property key.
	 * @return a value of a corresponding key.
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
