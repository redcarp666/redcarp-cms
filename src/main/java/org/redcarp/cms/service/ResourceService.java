package org.redcarp.cms.service;

import org.redcarp.cms.domain.Resource;
import org.redcarp.cms.domain.response.ResourceFilterConditions;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

	Resource upload(MultipartFile file);

	List<Resource> getResources();

	List<Resource> deleteResource(String USER_ROLE, String location);

	ResourceFilterConditions getFilterConditions();

	List<Resource> getByConditions(String date, String type);
}
