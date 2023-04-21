package org.zerock.myapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.myapp.domain.KakaoUserInfoDTO;
import org.zerock.myapp.oauth.KakaoOAuth;
import org.zerock.myapp.service.KakaoOAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor

@RequestMapping("/login")
@Controller
public class LoginController {
	private final KakaoOAuth kakaoOAuth;
	private final KakaoOAuthService kakaoOAuthService;
	
	
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
	} // kakaoLogin
	
	@GetMapping("/kakao/oauth")
	public String kakaoLogin(String code, Model model) throws IOException {
		log.trace("kakaoLogin() invoked.");
		
		KakaoUserInfoDTO dto = kakaoOAuthService.kakaoLogin(code);
		model.addAttribute("userInfo", dto);
		
		return "result";
	} // kakaoLogin
	
} // end class
