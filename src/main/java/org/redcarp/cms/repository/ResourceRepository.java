package org.redcarp.cms.repository;

import org.redcarp.cms.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, String> {

	Resource findByLocation(String location);

	@Query(value = "select distinct(fileType) from Resource")
	List<String> getFileTypes();

	@Query(value = "select new Resource(resource) from Resource resource where resource.createdAt between :startDate and :endDate and resource.fileType like :fileType order by resource.createdAt desc")
	List<Resource> findAllByConditions(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("fileType") String fileType);
}
