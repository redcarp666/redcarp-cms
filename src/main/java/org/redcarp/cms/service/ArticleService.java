package org.redcarp.cms.service;

import org.redcarp.cms.domain.Article;
import org.redcarp.cms.domain.response.ArticleFilterConditions;

import java.util.List;

public interface ArticleService {

	List<Article> getArticles();

	Article newArticle(String USER_ROLE, Article article);

	Article updateArticle(String USER_ROLE, Article article);

	void deleteArticle(String USER_ROLE, Integer articleId);

	ArticleFilterConditions getFilterConditions();

	List<Article> getArticlesByStatus(String status);

	List<Article> getArticlesByConditions(String status, String selectedDate, String selectedCategory, String selectedTag);

	Article moveToRecycleBin(String USER_ROLE, Boolean beDelete, Article article);
}
