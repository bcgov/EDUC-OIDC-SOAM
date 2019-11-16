package com.github.bcgov.keycloak.soam;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.broker.AbstractIdpAuthenticator;
import org.keycloak.authentication.authenticators.broker.util.SerializedBrokeredIdentityContext;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;


public class SoamPostLoginAuthenticator extends AbstractIdpAuthenticator {

    private static Logger logger = Logger.getLogger(SoamPostLoginAuthenticator.class);


    @Override
    protected void actionImpl(AuthenticationFlowContext context, SerializedBrokeredIdentityContext serializedCtx, BrokeredIdentityContext brokerContext) {
    	logger.info("SOAM Post: inside actionImpl");
        
    }
    
    @Override
    public void authenticate(AuthenticationFlowContext context) {
    	logger.info("SOAM Post: inside authenticate");
        
        logger.info("context.getUser(): " + context.getUser());
        logger.info("context.getSession(): " + context.getSession());
        
        if(context.getUser()!=null) {
        	logger.info("User GUID: " + context.getUser().getFirstAttribute("GUID"));
        }
        
        
        HttpRequest req = context.getHttpRequest();
        
        KeycloakSecurityContext kc = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
        if(kc == null) {
        	logger.info("KSC is null");
        }
        logger.info("Token: " + kc.getTokenString());
        
        
//        JsonWebToken token = (JsonWebToken)brokerContext.getContextData().get("VALIDATED_ID_TOKEN");
//        
//        for(String s: token.getOtherClaims().keySet()) {
//        	logger.info("Key: " + s + " Value: " + token.getOtherClaims().get(s));
//        }
        
        UserModel existingUser = context.getSession().users().getUserByUsername(context.getUser().getUsername(), context.getRealm());
        
        
        context.setUser(existingUser);
        context.success();
    }

    @Override
    protected void authenticateImpl(AuthenticationFlowContext context, SerializedBrokeredIdentityContext serializedCtx, BrokeredIdentityContext brokerContext) {
    	logger.info("SOAM Post: inside returning authenticateImpl");
    	//Not used for Post Login Authenticator
    }
    
    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

}
