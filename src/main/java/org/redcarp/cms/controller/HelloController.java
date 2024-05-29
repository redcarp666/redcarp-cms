package org.redcarp.cms.controller;

import org.redcarp.cms.domain.request.InitSystemInfo;
import org.redcarp.cms.domain.response.LoginUserInfo;
import org.redcarp.cms.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/hello")
public class HelloController {

	private final HelloService helloService;

	@Autowired
	public HelloController(HelloService helloService) {
		this.helloService = helloService;
	}

	@GetMapping
	public Boolean alreadyInitSystem() {
		return helloService.alreadyInitSystem();
	}

	@PostMapping
	public LoginUserInfo initSystem(@RequestBody InitSystemInfo initSystemInfo) {
		return helloService.initSystem(initSystemInfo);
	}
}
