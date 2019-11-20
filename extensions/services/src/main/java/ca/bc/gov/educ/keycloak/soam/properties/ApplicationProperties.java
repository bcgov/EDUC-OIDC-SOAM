package ca.bc.gov.educ.keycloak.soam.properties;

import org.jboss.logging.Logger;

/**
 * Class holds all application properties
 * 
 * @author Marco Villeneuve
 *
 */
public class ApplicationProperties {
	
	private static Logger logger = Logger.getLogger(ApplicationProperties.class);

	private String soamURL;
	private String tokenURL;
	private String clientID;
	private String clientSecret;

	public ApplicationProperties() {
		logger.info("SOAM: Building application properties");
		soamURL = System.getenv().getOrDefault("soamURL", "MissingSoamURL");
		tokenURL = System.getenv().getOrDefault("tokenURL", "MissingSoamTokenURL");
		clientID = System.getenv().getOrDefault("clientID", "MissingSoamClientID");
		clientSecret = System.getenv().getOrDefault("clientSecret", "MissingSoamClientSecret");
	}

	public String getSoamURL() {
		return soamURL;
	}

	public String getTokenURL() {
		return tokenURL;
	}

	public String getClientID() {
		return clientID;
	}

	public String getClientSecret() {
		return clientSecret;
	}

}
