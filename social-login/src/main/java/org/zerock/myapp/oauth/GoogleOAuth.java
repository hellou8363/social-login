package org.zerock.myapp.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.zerock.myapp.domain.google.GoogleOAuthTokenDTO;
import org.zerock.myapp.domain.google.GoogleUserInfoDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@Component
public class GoogleOAuth {
	@Value("${google.authUrl}")
	private String authUrl; 
	@Value("${google.redirectUrl}")
	private String redirectUrl;
	@Value("${google.clientId}")
	private String clientId;
	@Value("${google.clientSecret}")
	private String clientSecret;
	@Value("${google.authScope}")
	private String authScope;

	
	public String responseUrl() {
		log.trace("responseUrl() invoked.");

		return "https://accounts.google.com/o/oauth2/v2/auth?" 
		+ "client_id=" + clientId + "&" + "redirect_uri=" + redirectUrl + "&" 
		+ "response_type=code&" + "scope=" + getScopeUrl();
	} // responseUrl
	
	public String getScopeUrl() {
		return authScope.replaceAll(" ", "%20"); // %20 : space 공백
	} // getScopeUrl

	public ResponseEntity<String> requestAccessToken(String code) { 
		log.trace("requestAccessToken() invoked."); 
		// code : 4/0AVHEtk6wwzw4M3f6B2eqAYs5YdlOLdIeeh7glUgXYD0Bgk2fspEDiH3-0l1NsMOJ-IZajg
		
		HttpHeaders headersAccess = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		headersAccess.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("clientSecret", clientSecret);
		params.add("redirect_uri", redirectUrl);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> googleRequest = new HttpEntity<>(params, headersAccess);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(authUrl + "/token", googleRequest,
				String.class);

		return responseEntity;
	} // requestAccessToken
	
	public GoogleOAuthTokenDTO getAccessToken(ResponseEntity<String> response) 
			throws JsonProcessingException { 
		log.trace("getAccessToken() invoked.");
		
		ObjectMapper objectMapper = new ObjectMapper();
		GoogleOAuthTokenDTO googleOAuthTokenDTO = objectMapper.readValue(response.getBody(), GoogleOAuthTokenDTO.class);
		
		return googleOAuthTokenDTO;
	} // getAccessToken
	
	public ResponseEntity<String> requestUserInfo(GoogleOAuthTokenDTO oAuthToken) { 
		log.trace("requestUserInfo({}) invoked.", oAuthToken);
		
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(authUrl + "/tokeninfo", HttpMethod.GET, request, String.class);
		
		return response;
	} // requestUserInfo
	
	public GoogleUserInfoDTO getUserInfo(ResponseEntity<String> response) 
			throws JsonProcessingException { 
		log.trace("getUserInfo({}) invoked.");
		
		ObjectMapper objectMapper = new ObjectMapper();
		GoogleUserInfoDTO googleUserInfoDTO = objectMapper.readValue(response.getBody(), GoogleUserInfoDTO.class);
		
		return googleUserInfoDTO;		
	} // getUserInfo

} // end class