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
import org.zerock.myapp.domain.kakao.KakaoOAuthTokenDTO;
import org.zerock.myapp.domain.kakao.KakaoUserInfoDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@Component
public class KakaoOAuth {
	@Value("${kakao.restApiKey}")
	private String restApiKey; 
	@Value("${kakao.redirectUrl}")
	private String redirectUrl;
	private final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";

	
	public String responseUrl() {
		log.trace("responseUrl() invoked.");

		return "https:kauth.kakao.com/oauth/authorize?client_id=" + restApiKey + "&redirect_uri=" + redirectUrl + "&response_type=code";
	} // responseUrl

	public ResponseEntity<String> requestAccessToken(String code) { 
		log.trace("requestAccessToken() invoked.");
		
		HttpHeaders headersAccess = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		headersAccess.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", restApiKey);
		params.add("redirect_uri", redirectUrl);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> naverRequest = new HttpEntity<>(params, headersAccess);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL, naverRequest,
				String.class);

		return responseEntity;
	} // requestAccessToken
	
	public KakaoOAuthTokenDTO getAccessToken(ResponseEntity<String> response) 
			throws JsonProcessingException { 
		log.trace("getAccessToken({}) invoked.", response);
		
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoOAuthTokenDTO kakaoOAuthTokenDTO = objectMapper.readValue(response.getBody(), KakaoOAuthTokenDTO.class);
		
		return kakaoOAuthTokenDTO;
	} // getAccessToken
	
	public ResponseEntity<String> requestUserInfo(KakaoOAuthTokenDTO oAuthToken) { 
		log.trace("requestUserInfo({}) invoked.", oAuthToken);
		
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class);
		
		return response;
	} // requestUserInfo
	
	public KakaoUserInfoDTO getUserInfo(ResponseEntity<String> response) 
			throws JsonProcessingException { 
		log.trace("getUserInfo({}) invoked.");
		
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoUserInfoDTO kakaoUserInfoDTO = objectMapper.readValue(response.getBody(), KakaoUserInfoDTO.class);
		
		return kakaoUserInfoDTO;		
	} // getUserInfo

} // end class