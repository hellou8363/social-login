package org.zerock.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;


@Log4j2

@RequestMapping("/")
@Controller
public class MainController {
	
	@GetMapping
	public String main() {
		log.trace("main() invoked.");

		return "main";
	} // main
	
} // end class
