package ca.bc.gov.educ.keycloak.soam.rest;

import ca.bc.gov.educ.keycloak.common.properties.ApplicationProperties;
import ca.bc.gov.educ.keycloak.soam.model.SoamLoginEntity;
import ca.bc.gov.educ.keycloak.soam.model.SoamServicesCard;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.UUID;

/**
 * This class is used for REST calls
 *
 * @author Marco Villeneuve
 */
public class SoamRestUtils {

  private static Logger logger = Logger.getLogger(SoamRestUtils.class);

  private static SoamRestUtils soamRestUtilsInstance;

  private static ApplicationProperties props;

  private SoamRestUtils() {
    props = new ApplicationProperties();
  }

  public static SoamRestUtils getInstance() {
    if (soamRestUtilsInstance == null) {
      soamRestUtilsInstance = new SoamRestUtils();
    }
    return soamRestUtilsInstance;
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

  public void performLogin(String identifierType, String identifierValue, String userID, SoamServicesCard servicesCard) {
    String url = props.getSoamApiURL() + "/login";
    final String correlationID = logAndGetCorrelationID(identifierValue, url, HttpMethod.POST.toString());

    RestClient restClient = RestClient.create();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add("correlationID", correlationID);
    headers.add("Authorization", "Bearer " + getToken());
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("identifierType", identifierType);
    map.add("identifierValue", identifierValue);
    map.add("userID", userID);

    if (servicesCard != null) {
      map.add("birthDate", servicesCard.getBirthDate());
      map.add("did", servicesCard.getDid());
      map.add("email", servicesCard.getEmail());
      map.add("gender", servicesCard.getGender());
      map.add("givenName", servicesCard.getGivenName());
      map.add("givenNames", servicesCard.getGivenNames());
      map.add("identityAssuranceLevel", servicesCard.getIdentityAssuranceLevel());
      map.add("postalCode", servicesCard.getPostalCode());
      map.add("surname", servicesCard.getSurname());
      map.add("userDisplayName", servicesCard.getUserDisplayName());
    }

    try {
      restClient.post()
              .uri(url)
              .headers(
                      heads -> heads.addAll(headers))
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .body(map)
              .retrieve()
              .toBodilessEntity();
    } catch (final HttpClientErrorException e) {
      throw new RuntimeException("Could not complete login call: " + e.getMessage());
    }
  }

  public SoamLoginEntity getSoamLoginEntity(String identifierType, String identifierValue) {
    String url = props.getSoamApiURL() + "/" + identifierType + "/" + identifierValue;
    final String correlationID = logAndGetCorrelationID(identifierValue, url, HttpMethod.GET.toString());

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
              .body(SoamLoginEntity.class);
    } catch (final HttpClientErrorException e) {
      throw new RuntimeException("Could not complete getSoamLoginEntity call: " + e.getMessage());
    }
  }

  private String logAndGetCorrelationID(String identifierValue, String url, String httpMethod) {
    final String correlationID = UUID.randomUUID().toString();
    MDC.put("correlation_id", correlationID);
    MDC.put("user_guid", identifierValue);
    MDC.put("client_http_request_url", url);
    MDC.put("client_http_request_method", httpMethod);
    logger.info("correlation id for guid=" + identifierValue + " is=" + correlationID);
    MDC.clear();
    return correlationID;
  }
}
