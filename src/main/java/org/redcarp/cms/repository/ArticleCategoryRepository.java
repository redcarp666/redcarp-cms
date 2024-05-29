package org.redcarp.cms.repository;

import org.redcarp.cms.domain.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Integer> {

	ArticleCategory findByArticleIdAndCategoryUrlAlias(Integer articleId, String categoryUrlAlias);

	@Transactional
	void deleteArticleCategoryByArticleIdAndCategoryUrlAlias(Integer articleId, String categoryUrlAlias);

	@Transactional
	void deleteAllByCategoryUrlAlias(String categoryUrlAlias);
}
