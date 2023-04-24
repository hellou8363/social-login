package org.zerock.myapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.myapp.domain.google.GoogleUserInfoDTO;
import org.zerock.myapp.domain.kakao.KakaoUserInfoDTO;
import org.zerock.myapp.domain.naver.NaverUserInfoDTO;
import org.zerock.myapp.oauth.GoogleOAuth;
import org.zerock.myapp.oauth.KakaoOAuth;
import org.zerock.myapp.oauth.NaverOAuth;
import org.zerock.myapp.service.GoogleOAuthService;
import org.zerock.myapp.service.KakaoOAuthService;
import org.zerock.myapp.service.NaverOAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@RequestMapping("/login")
@Controller
public class LoginController {
	private final KakaoOAuth kakaoOAuth;
	private final KakaoOAuthService kakaoOAuthService;
	private final NaverOAuth naverOAuth;
	private final NaverOAuthService naverOAuthService;
	private final GoogleOAuth googleOAuth;
	private final GoogleOAuthService googleOAuthService;

	
	@GetMapping
	public String showLogin( ) {
		log.trace("showLogin() invoked.");
		
		return "login";
	} // showLogin
	
	// ========== Kakao ==========
	
	@GetMapping("/kakao")
	public void getKakaoOAuthUrl(HttpServletResponse response) throws IOException {
		log.trace("getKakaoOAuthUrl() invoked.");
		
		response.sendRedirect(kakaoOAuth.responseUrl());
	} // getKakaoOAuthUrl
	
	@GetMapping("/kakao/oauth")
	public String kakaoLogin(String code, Model model) throws IOException {
		log.trace("kakaoLogin() invoked.");
		
		KakaoUserInfoDTO dto = kakaoOAuthService.kakaoLogin(code);
		model.addAttribute("userInfo", dto);
		
		return "result";
	} // kakaoLogin
	
	// ========== Naver ==========
	
	@GetMapping("/naver")
	public void getNaverOAuthUrl(HttpServletResponse response, HttpSession session) throws IOException {
		log.trace("getNaverOAuthUrl() invoked.");
		
		response.sendRedirect(naverOAuth.responseUrl(session));
	} // getNaverOAuthUrl
	
	@GetMapping("/naver/oauth")
	public String naverLogin(String code, HttpSession session, Model model) throws IOException {
		log.trace("naverLogin() invoked.");
		
		NaverUserInfoDTO dto = naverOAuthService.naverLogin(code, session);
		model.addAttribute("userInfo", dto);
		
		return "result";
	} // naverLogin
	
	// ========== Google ==========
	
	@GetMapping("/google")
	public void getGoogleOAuthUrl(HttpServletResponse response) throws IOException {
		log.trace("getGoogleOAuthUrl() invoked.");
		
		response.sendRedirect(googleOAuth.responseUrl());
	} // getGoogleOAuthUrl
	
	@GetMapping("/google/oauth")
	public String googleLogin(String code, HttpSession session, Model model) throws IOException {
		log.trace("googleLogin() invoked.");
		
		GoogleUserInfoDTO dto = googleOAuthService.googleLogin(code);
		model.addAttribute("userInfo", dto);
		
		return "result";
	} // googleLogin
	
} // end class
