package org.zerock.myapp.service;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zerock.myapp.domain.naver.NaverOAuthTokenDTO;
import org.zerock.myapp.domain.naver.NaverUserInfoDTO;
import org.zerock.myapp.oauth.NaverOAuth;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@Component
public class NaverOAuthService {
	private final NaverOAuth naverOAuth;
	
	
	private NaverUserInfoDTO getNaverUserInfoDTO(String code, HttpSession session) throws JsonProcessingException {
		log.trace("getNaverUserInfoDTO() invoked.");
		
		ResponseEntity<String> accessTokenResponse = naverOAuth.requestAccessToken(code, session);
		NaverOAuthTokenDTO oAuthToken = naverOAuth.getAccessToken(accessTokenResponse);
		ResponseEntity<String> userInfoResponse = naverOAuth.requestUserInfo(oAuthToken);
		NaverUserInfoDTO naverUser = naverOAuth.getUserInfo(userInfoResponse);
		
		return naverUser;
	} // getNaverUserInfoDTO
	
	public NaverUserInfoDTO naverLogin(String code, HttpSession session) throws JsonProcessingException {
		log.trace("naverLogin() invoked.");
		
		NaverUserInfoDTO naverUser = getNaverUserInfoDTO(code, session);
		log.info("naverUser: {}", naverUser);

		return naverUser;
	} // naverLogin

} // end class