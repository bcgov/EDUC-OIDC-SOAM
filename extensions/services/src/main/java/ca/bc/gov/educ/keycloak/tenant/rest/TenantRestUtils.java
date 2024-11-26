package ca.bc.gov.educ.keycloak.tenant.rest;

import ca.bc.gov.educ.keycloak.common.properties.ApplicationProperties;
import ca.bc.gov.educ.keycloak.tenant.model.TenantAccess;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


import java.util.*;

/**
 * This class is used for REST calls
 *
 * @author Marco Villeneuve
 */
public class TenantRestUtils {

  private static Logger logger = Logger.getLogger(TenantRestUtils.class);

  private static TenantRestUtils tenantRestUtilsInstance;

  private static ApplicationProperties props;

  private TenantRestUtils() {
    props = new ApplicationProperties();
  }

  public static TenantRestUtils getInstance() {
    if (tenantRestUtilsInstance == null) {
      tenantRestUtilsInstance = new TenantRestUtils();
    }
    return tenantRestUtilsInstance;
  }
  public String getToken() {
    logger.debug("Calling get token method");
    RestClient restClient = RestClient.create();
    return restClient
            .post()
            .uri(props.getTokenURL())
            .headers(
                    headers -> {
                      headers.setBasicAuth(props.getClientID(), props.getClientSecret());
                      headers.set("Content-Type", "application/json");
                    })
            .retrieve()
            .body(String.class);
  }

  public TenantAccess checkForValidTenant(String clientID, String tenantID) {
    String url = props.getSoamApiURL() + "/" + clientID + "/" + tenantID;
    final String correlationID = logAndGetCorrelationID(tenantID, url, HttpMethod.GET.toString());

    RestClient restClient = RestClient.create();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("correlationID", correlationID);
    headers.add("Authorization", "Bearer " + getToken());
    try {
      return restClient.get()
              .uri(url)
              .headers(
                      heads -> heads.addAll(headers))
              .retrieve()
              .body(TenantAccess.class);
    } catch (final HttpClientErrorException e) {
      throw new RuntimeException("Could not complete checkForValidTenant call: " + e.getMessage());
    }
  }

  private String logAndGetCorrelationID(String tenantID, String url, String httpMethod) {
    final String correlationID = UUID.randomUUID().toString();
    MDC.put("correlation_id", correlationID);
    MDC.put("tenant_id", tenantID);
    MDC.put("client_http_request_url", url);
    MDC.put("client_http_request_method", httpMethod);
    logger.info("correlation id for tenant ID=" + tenantID + " is=" + correlationID);
    MDC.clear();
    return correlationID;
  }
}
