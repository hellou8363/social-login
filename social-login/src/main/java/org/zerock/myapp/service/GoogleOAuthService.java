package org.zerock.myapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zerock.myapp.domain.google.GoogleOAuthTokenDTO;
import org.zerock.myapp.domain.google.GoogleUserInfoDTO;
import org.zerock.myapp.oauth.GoogleOAuth;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@Component
public class GoogleOAuthService {
	private final GoogleOAuth googleOAuth;
	
	
	private GoogleUserInfoDTO getGoogleUserInfoDTO(String code) throws JsonProcessingException {
		log.trace("getNaverUserInfoDTO() invoked.");
		
		ResponseEntity<String> accessTokenResponse = googleOAuth.requestAccessToken(code);
		GoogleOAuthTokenDTO oAuthToken = googleOAuth.getAccessToken(accessTokenResponse);
		ResponseEntity<String> userInfoResponse = googleOAuth.requestUserInfo(oAuthToken);
		GoogleUserInfoDTO googleUser = googleOAuth.getUserInfo(userInfoResponse);
		
		return googleUser;
	} // getGoogleUserInfoDTO
	
	public GoogleUserInfoDTO googleLogin(String code) throws JsonProcessingException {
		log.trace("googleLogin() invoked.");
		
		GoogleUserInfoDTO googleUser = getGoogleUserInfoDTO(code);
		log.info("googleUser: {}", googleUser);

		return googleUser;
	} // googleLogin

} // end class