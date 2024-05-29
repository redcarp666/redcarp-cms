package org.redcarp.cms.controller;

import org.redcarp.cms.domain.Resource;
import org.redcarp.cms.domain.response.ResourceFilterConditions;
import org.redcarp.cms.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/resources")
public class ResourceController {

	private final ResourceService resourceService;

	@Autowired
	public ResourceController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@GetMapping
	public List<Resource> getResources() {
		return resourceService.getResources();
	}

	@DeleteMapping
	public List<Resource> deleteResource(@RequestAttribute String USER_ROLE, @RequestParam String location) {
		return resourceService.deleteResource(USER_ROLE, location);
	}

	@GetMapping(value = "/filter-conditions")
	public ResourceFilterConditions getFilterConditions() {
		return resourceService.getFilterConditions();
	}

	@GetMapping(value = "/by-conditions")
	public List<Resource> getByConditions(@RequestParam String date, @RequestParam String type) {
		return resourceService.getByConditions(date, type);
	}
}
