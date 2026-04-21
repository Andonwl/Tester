package com.bridgetrack.bridgetrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "redirect:/homepage";
	}
	
	@GetMapping("/homepage")
	public String homepage() {
		return "homepage";
	}
}
