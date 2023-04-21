package org.zerock.myapp.oauth;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.zerock.myapp.domain.naver.NaverOAuthTokenDTO;
import org.zerock.myapp.domain.naver.NaverUserInfoDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@Component
public class NaverOAuth {
	@Value("${naver.clientId}")
	private String clientId; 
	@Value("${naver.clientSecret}")
	private String clientSecret;
	@Value("${naver.redirectUrl}")
	private String redirectUrl;
	private final String NAVER_TOKEN_REQUEST_URL = "https://nid.naver.com/oauth2.0/token";

	
	public String responseUrl(HttpSession session) {
		log.trace("responseUrl({}) invoked.");

		// 사이트 간 요청 위조(cross-site request forgery) 공격을 방지하기 위해 
		// 애플리케이션에서 생성한 상태 토큰값으로 URL 인코딩을 적용한 값을 사용
	    SecureRandom random = new SecureRandom();
	    String state = new BigInteger(130, random).toString();
	    
	    session.setAttribute("state", state);

		return "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="
		+ clientId + "&state=" + state + "&redirect_uri=" + redirectUrl;
	} // responseUrl

	public ResponseEntity<String> requestAccessToken(String code, HttpSession session) { // 접근 토큰 발급 요청
		log.trace("requestAccessToken({}) invoked.", code, session);
		
		HttpHeaders headersAccess = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		headersAccess.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code"); // authorization_code: 발급
		params.add("client_id", clientId); // 애플리케이션 등록 시 발급받은 Client ID 값
		params.add("client_secret", clientSecret); // 애플리케이션 등록 시 발급받은 Client ID 값
		params.add("redirect_uri", redirectUrl); // 인가 코드가 리다이렉트된 URI
		params.add("code", code); // 인가 코드 받기 요청으로 얻은 인가 코드
		
	    SecureRandom random = new SecureRandom();
	    String state = new BigInteger(130, random).toString();
	    
	    session.setAttribute("state", state);
		params.add("state", state);

		HttpEntity<MultiValueMap<String, String>> naverRequest = new HttpEntity<>(params, headersAccess);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(NAVER_TOKEN_REQUEST_URL, naverRequest,
				String.class);

		return responseEntity;
	} // requestAccessToken
	
	public NaverOAuthTokenDTO getAccessToken(ResponseEntity<String> response) 
			throws JsonProcessingException { // 접근 토큰 발급 요청에 대한 응답
		log.trace("getAccessToken({}) invoked.", response);
		
		ObjectMapper objectMapper = new ObjectMapper();
		NaverOAuthTokenDTO naverOAuthTokenDTO = objectMapper.readValue(response.getBody(), NaverOAuthTokenDTO.class);
		
		return naverOAuthTokenDTO;
	} // getAccessToken
	
	public ResponseEntity<String> requestUserInfo(NaverOAuthTokenDTO oAuthToken) { // 접근 토큰을 이용하여 프로필 API 호출하기
		log.trace("requestUserInfo({}) invoked.", oAuthToken);
		
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		// 접근 토큰(access token)을 전달하는 헤더
		// 다음과 같은 형식으로 헤더 값에 접근 토큰(access token)을 포함합니다. 
		// 토큰 타입은 "Bearer"로 값이 고정되어 있습니다.
		// Authorization: {토큰 타입] {접근 토큰]
		headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, String.class);
		
		return response;
	} // requestUserInfo
	
	public NaverUserInfoDTO getUserInfo(ResponseEntity<String> response) 
			throws JsonProcessingException { // 접근 토큰을 이용한 프로필 API 호출에 대한 응답
		log.trace("getUserInfo({}) invoked.", response);
		
		ObjectMapper objectMapper = new ObjectMapper();
		NaverUserInfoDTO naverUserInfoDTO = objectMapper.readValue(response.getBody(), NaverUserInfoDTO.class);
		
		return naverUserInfoDTO;		
	} // getUserInfo

} // end class