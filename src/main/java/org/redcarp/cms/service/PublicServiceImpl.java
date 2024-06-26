package org.redcarp.cms.service;

import org.redcarp.cms.domain.Article;
import org.redcarp.cms.domain.Category;
import org.redcarp.cms.domain.Options;
import org.redcarp.cms.repository.ArticleRepository;
import org.redcarp.cms.repository.OptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicServiceImpl implements PublicService {

	private final CategoryService categoryService;

	private final ArticleRepository articleRepository;

	@Autowired
	private OptionsRepository optionsRepository;

	@Autowired
	public PublicServiceImpl(CategoryService categoryService, ArticleRepository articleRepository) {
		this.categoryService = categoryService;
		this.articleRepository = articleRepository;
	}

	@Override
	public List<Category> getCategories() {
		return categoryService.getCategories();
	}

	@Override
	public List<Article> getArticlesByCategory(String categoryUrlAlias, Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "createdAt"));
		return articleRepository.getArticlesByCategory(pageable, categoryUrlAlias).getContent();
	}

	@Override
	public Article getArticleById(Integer id) {
		return articleRepository.findById(id).orElse(new Article());
	}

	@Override
	public List<Options> getOptions() {
		return optionsRepository.findAllByBePublic(true);
	}
}
