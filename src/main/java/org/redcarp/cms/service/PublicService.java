package org.redcarp.cms.service;

import org.redcarp.cms.domain.Article;
import org.redcarp.cms.domain.Category;
import org.redcarp.cms.domain.Options;

import java.util.List;

public interface PublicService {

	List<Category> getCategories();

	List<Article> getArticlesByCategory(String categoryUrlAlias, Integer page, Integer size);

	Article getArticleById(Integer id);

	List<Options> getOptions();
}
