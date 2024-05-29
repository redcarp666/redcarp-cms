package org.redcarp.cms.controller;

import org.redcarp.cms.domain.Resource;
import org.redcarp.cms.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/upload")
public class UploadController {

	private final ResourceService resourceService;

	@Autowired
	public UploadController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@PostMapping
	public Resource upload(@RequestParam MultipartFile file) {
		return resourceService.upload(file);
	}
}
