package com.github.bcgov.keycloak.soam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

public class SoamProtocolMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper{

	public static final String PROVIDER_ID = "oidc-customprotocolmapper";
	private static Logger logger = Logger.getLogger(SoamProtocolMapper.class);

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

	/**
	 * Maybe you want to have config fields for your Mapper
	 */
	/*
    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(ProtocolMapperUtils.USER_ATTRIBUTE);
        property.setLabel(ProtocolMapperUtils.USER_MODEL_ATTRIBUTE_LABEL);
        property.setHelpText(ProtocolMapperUtils.USER_MODEL_ATTRIBUTE_HELP_TEXT);
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);

        property = new ProviderConfigProperty();
        property.setName(ProtocolMapperUtils.MULTIVALUED);
        property.setLabel(ProtocolMapperUtils.MULTIVALUED_LABEL);
        property.setHelpText(ProtocolMapperUtils.MULTIVALUED_HELP_TEXT);
        property.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        configProperties.add(property);

    }
	 */

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		logger.info("SOAM: inside authenticateImpl");
		return configProperties;
	}

	@Override
	public String getDisplayCategory() {
		logger.info("SOAM: inside authenticateImpl");
		return TOKEN_MAPPER_CATEGORY;
	}

	@Override
	public String getDisplayType() {
		logger.info("SOAM: inside authenticateImpl");
		return "Stackoverflow Custom Protocol Mapper";
	}

	@Override
	public String getId() {
		logger.info("SOAM: inside authenticateImpl");
		return PROVIDER_ID;
	}

	@Override
	public String getHelpText() {
		logger.info("SOAM: inside authenticateImpl");
		return "some help text";
	}

	public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session,
			UserSessionModel userSession, AuthenticatedClientSessionModel clientSession) {
		logger.info("SOAM: inside transformAccessToken");
		token.getOtherClaims().put("stackoverflowCustomToken", "stackoverflow");

		setClaim(token, mappingModel, userSession, session);
		return token;
	}

	public static ProtocolMapperModel create(String name, boolean accessToken, boolean idToken, boolean userInfo) {
		logger.info("SOAM: inside create");
		ProtocolMapperModel mapper = new ProtocolMapperModel();
		mapper.setName(name);
		mapper.setProtocolMapper(PROVIDER_ID);
		mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
		Map<String, String> config = new HashMap<String, String>();
		mapper.setConfig(config);
		return mapper;
	}



}