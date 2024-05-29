package org.redcarp.cms.repository;

import org.redcarp.cms.domain.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Integer> {

	List<ArticleTag> findAllByTagName(String tagName);

	@Transactional
	void deleteArticleTagByArticleIdAndTagName(Integer articleId, String tagName);
}
