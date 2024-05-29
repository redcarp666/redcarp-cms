package org.redcarp.cms.repository;

import org.redcarp.cms.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

	Category findByUrlAlias(String urlAlias);

	Integer countByNodeLevelAndParentNodeUrlAlias(Integer nodeLevel, String parentUrlAlias);

	Category findByParentNodeUrlAliasAndSequence(String parentNodeUrlAlias, Integer sequence);

	List<Category> findAllByParentNodeUrlAlias(String parentNodeUrlAlias);
}
