package org.redcarp.cms.repository;

import org.redcarp.cms.domain.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionsRepository extends JpaRepository<Options, String> {

	List<Options> findAllByBePublic(Boolean bePublic);
}
