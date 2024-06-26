package org.redcarp.cms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class WelcomeController {

	@GetMapping
	public String welcome() {
		return "<h1 style=\"text-align: center\">Welcome to RedCarpCMS back end service!</h1>";
	}
}
