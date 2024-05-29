package org.redcarp.cms.repository;

import org.redcarp.cms.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {

	Tag findByName(String name);
}
