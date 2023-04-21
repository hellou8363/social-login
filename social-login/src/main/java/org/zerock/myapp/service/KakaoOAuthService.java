package org.zerock.myapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zerock.myapp.domain.kakao.KakaoOAuthTokenDTO;
import org.zerock.myapp.domain.kakao.KakaoUserInfoDTO;
import org.zerock.myapp.oauth.KakaoOAuth;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@Component
public class KakaoOAuthService {
	private final KakaoOAuth kakaoOAuth;
	
	
	private KakaoUserInfoDTO getKakaoUserInfoDTO(String code) throws JsonProcessingException {
		log.trace("getNaverUserInfoDTO() invoked.");
		
		ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
		KakaoOAuthTokenDTO oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
		ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
		KakaoUserInfoDTO kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
		
		return kakaoUser;
	} // getKakaoUserInfoDTO
	
	public KakaoUserInfoDTO kakaoLogin(String code) throws JsonProcessingException {
		log.trace("kakaoLogin() invoked.");
		
		KakaoUserInfoDTO kakaoUser = getKakaoUserInfoDTO(code);
		log.info("kakaoUser: {}", kakaoUser);

		return kakaoUser;
	} // kakaoLogin

} // end class