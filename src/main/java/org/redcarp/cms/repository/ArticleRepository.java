package org.redcarp.cms.repository;

import org.redcarp.cms.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

	long countByBeDelete(Boolean beDelete);

	long countByBeDeleteAndStatus(Boolean beDelete, String status);

	List<Article> findAllByBeDelete(Boolean beDelete, Sort sort);

	List<Article> findAllByBeDeleteAndStatus(Boolean beDelete, String status, Sort sort);

	@Query(value = "select new Article(article) from Article article where article.createdAt between :startDate and :endDate and article.status like :status and article.beDelete = :beDelete and article.id in (select articleCategory.articleId from ArticleCategory articleCategory where articleCategory.categoryUrlAlias like :categoryUrlAlias) and article.id in (select articleTag.articleId from ArticleTag articleTag where articleTag.tagName like :tagName) order by article.createdAt desc")
	List<Article> findAllByConditions(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("status") String status, @Param("beDelete") Boolean beDelete, @Param("categoryUrlAlias") String categoryUrlAlias, @Param("tagName") String tagName);

	@Modifying
	@Transactional
	@Query(value = "update Article article set article.beDelete = true where article.id in (select articleCategory.articleId from ArticleCategory articleCategory where articleCategory.categoryUrlAlias = :categoryUrlAlias)")
	void moveCategoryArticleToRecycleBin(@Param("categoryUrlAlias") String categoryUrlAlias);

	@Query(value = "select new Article(article) from Article article where article.id in (select articleCategory.articleId from ArticleCategory articleCategory where articleCategory.categoryUrlAlias = :categoryUrlAlias) and article.beDelete = false and article.status = '已发布'")
	Page<Article> getArticlesByCategory(Pageable pageable, @Param("categoryUrlAlias") String categoryUrlAlias);
}
