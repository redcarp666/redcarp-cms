package org.redcarp.cms.service;

import org.redcarp.cms.config.ArticleStatus;
import org.redcarp.cms.config.UserRoleFields;
import org.redcarp.cms.domain.*;
import org.redcarp.cms.domain.response.ArticleFilterConditions;
import org.redcarp.cms.exception.IllegalParameterException;
import org.redcarp.cms.exception.UserRolePermissionException;
import org.redcarp.cms.repository.*;
import org.redcarp.cms.util.QueryDateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

	private final Sort ORDER_BY_CREATED_AT = new Sort(Sort.Direction.DESC, "createdAt");

	private final ArticleRepository articleRepository;

	private final CategoryRepository categoryRepository;

	private final TagRepository tagRepository;

	private final ArticleTagRepository articleTagRepository;

	private final ResourceRepository resourceRepository;

	private final ArticleCategoryRepository articleCategoryRepository;

	@Autowired
	public ArticleServiceImpl(ArticleRepository articleRepository, CategoryRepository categoryRepository, TagRepository tagRepository, ArticleTagRepository articleTagRepository, ResourceRepository resourceRepository, ArticleCategoryRepository articleCategoryRepository) {
		this.articleRepository = articleRepository;
		this.categoryRepository = categoryRepository;
		this.tagRepository = tagRepository;
		this.articleTagRepository = articleTagRepository;
		this.resourceRepository = resourceRepository;
		this.articleCategoryRepository = articleCategoryRepository;
	}

	@Override
	public List<Article> getArticles() {
		return articleRepository.findAllByBeDelete(false, ORDER_BY_CREATED_AT);
	}

	@Override
	public Article newArticle(String USER_ROLE, Article article) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) <= 1 && article.getStatus().equals(ArticleStatus.PUBLISHED)) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户角色等级没有发布文章的权限，可提交文章并联系管理员审核！");
		}

		Article articleResult = articleRepository.save(article);

		// 文章保存成功后……
		// 1、更新 Category 表 articleCount 字段，并添加 文章-分类 绑定到 ArticleCategory 表
		Category category = categoryRepository.findByUrlAlias(articleResult.getCategory().getUrlAlias());
		category.setArticleCount(category.getArticleCount() + 1);
		categoryRepository.save(category);
		articleCategoryRepository.save(new ArticleCategory(articleResult.getId(),
		                                                   articleResult.getCategory().getUrlAlias()));

		// 2、更新 Tag 表 articleCount 字段，并添加 文章-标签 绑定到 ArticleTag 表
		articleResult.getTags();
		for (String tagName : articleResult.getTags()) {
			// 添加新标签， 或更新已存在标签 articleCount
			Tag tag = tagRepository.findByName(tagName);
			if (tag == null) {
				tagRepository.save(new Tag(tagName, 1));
			} else {
				tag.setArticleCount(tag.getArticleCount() + 1);
				tagRepository.save(tag);
			}

			// 添加 文章-标签 绑定到 ArticleTag 表
			articleTagRepository.save(new ArticleTag(articleResult.getId(), tagName));
		}
		// 添加一个空标签的 文章-标签 绑定，用于筛选查询
		articleTagRepository.save(new ArticleTag(articleResult.getId(), ""));

		// 3、更新 Resource 表 referenceCount 字段
		for (Resource imageResource : articleResult.getImages()) {
			imageResource.setBeReference(true);
			resourceRepository.save(imageResource);
		}
		for (Resource accessoryResource : articleResult.getAccessories()) {
			accessoryResource.setBeReference(true);
			resourceRepository.save(accessoryResource);
		}

		return articleResult;
	}

	@Override
	public Article updateArticle(String USER_ROLE, Article article) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) <= 1 && article.getStatus().equals(ArticleStatus.PUBLISHED)) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户角色等级没有发布文章的权限，可联系管理员审核并发布！");
		}

		Article oldArticle = articleRepository.findById(article.getId()).orElse(null);
		if (oldArticle == null) {
			throw new IllegalParameterException("【非法参数异常】- 欲更新的文章不存在！");
		}

		// 更新 Resource 表 referenceCount 字段
		for (Resource imageResource : article.getImages()) {
			imageResource.setBeReference(true);
			resourceRepository.save(imageResource);
		}

		// 对比新旧文章 images 引用列表，新 article 对象只有最新添加的图片列表，
		// 所以需要将旧 article 中还在用的图片引用添加进新的，不再用的对应资源计数 -1
		for (Resource imageResource : oldArticle.getImages()) {
			if (article.getContent().contains(imageResource.getLocation())) {
				List<Resource> tempImageResourceList = Arrays.asList(article.getImages());
				List<Resource> imageResourceList = new ArrayList<>(tempImageResourceList);
				imageResourceList.add(imageResource);
				article.setImages(imageResourceList.toArray(new Resource[0]));
			} else {
				imageResource.setBeReference(false);
				resourceRepository.save(imageResource);
			}
		}

		// 如果分类有更新，就更新对应分类的文章计数，以及更新 文章-分类 绑定 ArticleCategory 表
		if (!article.getCategory().getUrlAlias().equals(oldArticle.getCategory().getUrlAlias())) {
			Category newCategory = categoryRepository.findByUrlAlias(article.getCategory().getUrlAlias());
			newCategory.setArticleCount(newCategory.getArticleCount() + 1);
			categoryRepository.save(newCategory);

			Category oldCategory = categoryRepository.findByUrlAlias(oldArticle.getCategory().getUrlAlias());
			if (oldCategory != null) {
				oldCategory.setArticleCount(oldCategory.getArticleCount() - 1);
				categoryRepository.save(oldCategory);

				ArticleCategory articleCategory = articleCategoryRepository.findByArticleIdAndCategoryUrlAlias(article.getId(),
				                                                                                               oldCategory.getUrlAlias());
				articleCategory.setCategoryUrlAlias(newCategory.getUrlAlias());
				articleCategoryRepository.save(articleCategory);
			} else {
				articleCategoryRepository.save(new ArticleCategory(article.getId(),
				                                                   article.getCategory().getUrlAlias()));
			}
		}

		// 处理标签变动，更新标签计数，以及更新 文章-标签 绑定 ArticleTag 表
		List<String> newArticleTagList = Arrays.asList(article.getTags());
		List<String> oldArticleTagList = Arrays.asList(oldArticle.getTags());
		for (String newTagName : newArticleTagList) {
			if (!oldArticleTagList.contains(newTagName)) {
				Tag tag = tagRepository.findByName(newTagName);
				if (tag != null) {
					tag.setArticleCount(tag.getArticleCount() + 1);
					tagRepository.save(tag);
				} else {
					tag = tagRepository.save(new Tag(newTagName, 1));
				}

				articleTagRepository.save(new ArticleTag(article.getId(), tag.getName()));
			}
		}
		for (String oldTagName : oldArticleTagList) {
			if (!newArticleTagList.contains(oldTagName)) {
				Tag tag = tagRepository.findByName(oldTagName);
				tag.setArticleCount(tag.getArticleCount() - 1);
				tagRepository.save(tag);

				articleTagRepository.deleteArticleTagByArticleIdAndTagName(article.getId(), tag.getName());
			}
		}

		return articleRepository.save(article);
	}

	@Override
	public void deleteArticle(String USER_ROLE, Integer articleId) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) <= 1) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户角色等级没有删除文章的权限！");
		}

		Article article = articleRepository.findById(articleId).orElse(null);
		if (article == null) {
			throw new IllegalParameterException("【非法参数异常】- 欲删除的文章不存在！");
		}
		articleRepository.delete(article);

		// 更新 Category 表 articleCount 字段，删除 ArticleCategory 表 对应文章分类绑定关系
		Category category = categoryRepository.findByUrlAlias(article.getCategory().getUrlAlias());
		if (category != null) {
			category.setArticleCount(category.getArticleCount() - 1);
			categoryRepository.save(category);
			articleCategoryRepository.deleteArticleCategoryByArticleIdAndCategoryUrlAlias(articleId,
			                                                                              category.getUrlAlias());
		}

		// 更新 Tag 表 articleCount 字段，删除 ArticleTag 表 对应文章标签绑定关系
		String[] tagNameList = article.getTags();
		for (String tagName : tagNameList) {
			// 更新 articleCount
			Tag tag = tagRepository.findByName(tagName);
			tag.setArticleCount(tag.getArticleCount() - 1);
			tagRepository.save(tag);
			// 删除 ArticleTag 绑定
			articleTagRepository.deleteArticleTagByArticleIdAndTagName(articleId, tagName);
		}
		// 删除空标签的 文章-标签 绑定
		articleTagRepository.deleteArticleTagByArticleIdAndTagName(articleId, "");

		// 通过 images 和 accessories 字段更新 Resource 表的 referenceCount 字段
		for (Resource imageResource : article.getImages()) {
			Resource resource = resourceRepository.findById(imageResource.getLocation()).orElse(null);
			if (resource != null) {
				resource.setBeReference(false);
				resourceRepository.save(resource);
			}
		}
		for (Resource accessoryResource : article.getAccessories()) {
			Resource resource = resourceRepository.findById(accessoryResource.getLocation()).orElse(null);
			if (resource != null) {
				resource.setBeReference(false);
				resourceRepository.save(resource);
			}
		}
	}

	@Override
	public ArticleFilterConditions getFilterConditions() {
		List<Article> articleList = articleRepository.findAll(ORDER_BY_CREATED_AT);
		List<String> dateList = new ArrayList<>();
		List<Category> categoryList = new ArrayList<>();
		List<Tag> tagList = new ArrayList<>();
		long allExcludeRecycleBinCount = 0;
		long releaseCount = 0;
		long pendingReviewCount = 0;
		long draftCount = 0;
		long recycleBinCount = 0;

		if (articleList != null && articleList.size() > 0) {
			// 获取创建文章最早时间和最晚时间，生成时间选择列表
			DateFormat format = new SimpleDateFormat("yyyy-MM");
			String beginTime = format.format(articleList.get(articleList.size() - 1).getCreatedAt());
			String finalTime = format.format(articleList.get(0).getCreatedAt());
			for (int year = Integer.parseInt(finalTime.split("-")[0]); year >= Integer.parseInt(beginTime.split("-")[0]); year--) {
				if (year == Integer.parseInt(finalTime.split("-")[0])) {
					if (year == Integer.parseInt(beginTime.split("-")[0])) {
						for (int month = Integer.parseInt(finalTime.split("-")[1]); month >= Integer.parseInt(beginTime.split(
								"-")[1]); month--) {
							dateList.add(year + "-" + String.format("%02d", month));
						}
					} else {
						for (int month = Integer.parseInt(finalTime.split("-")[1]); month >= 1; month--) {
							dateList.add(year + "-" + String.format("%02d", month));
						}
					}
				} else if (year == Integer.parseInt(beginTime.split("-")[0])) {
					for (int month = 12; month >= Integer.parseInt(beginTime.split("-")[1]); month--) {
						dateList.add(year + "-" + String.format("%02d", month));
					}
				} else {
					for (int month = 12; month >= 1; month--) {
						dateList.add(year + "-" + String.format("%02d", month));
					}
				}
			}

			// 装填其他数据
			categoryList = categoryRepository.findAll();
			tagList = tagRepository.findAll(ORDER_BY_CREATED_AT);
			allExcludeRecycleBinCount = articleRepository.countByBeDelete(false);
			releaseCount = articleRepository.countByBeDeleteAndStatus(false, ArticleStatus.PUBLISHED);
			pendingReviewCount = articleRepository.countByBeDeleteAndStatus(false, ArticleStatus.PENDING_REVIEW);
			draftCount = articleRepository.countByBeDeleteAndStatus(false, ArticleStatus.DRAFT);
			recycleBinCount = articleRepository.countByBeDelete(true);
		}

		return new ArticleFilterConditions(dateList,
		                                   categoryList,
		                                   tagList,
		                                   allExcludeRecycleBinCount,
		                                   releaseCount,
		                                   pendingReviewCount,
		                                   draftCount,
		                                   recycleBinCount);
	}

	@Override
	public List<Article> getArticlesByStatus(String status) {
		if (status.equals(ArticleStatus.ALL)) {
			return articleRepository.findAllByBeDelete(false, ORDER_BY_CREATED_AT);
		} else if (status.equals(ArticleStatus.RECYCLE_BIN)) {
			return articleRepository.findAllByBeDelete(true, ORDER_BY_CREATED_AT);
		} else {
			return articleRepository.findAllByBeDeleteAndStatus(false, status, ORDER_BY_CREATED_AT);
		}
	}

	@Override
	public List<Article> getArticlesByConditions(String selectedStatus, String selectedDate, String selectedCategory, String selectedTag) {
		// 默认 beDelete 字段为 false
		boolean beDelete = false;
		// 默认 status 字段为 %
		String status = "%";
		// 处理查询时间范围
		Map<String, Date> dateMap = QueryDateRange.handle(selectedDate);

		// 处理文章状态的查询条件
		if (selectedStatus.equals(ArticleStatus.RECYCLE_BIN)) {
			beDelete = true;
		} else if (!selectedStatus.equals(ArticleStatus.ALL) && !selectedStatus.equals("null") && !selectedStatus.equals(
				"")) {
			status = selectedStatus;
		}
		if (selectedCategory == null || selectedCategory.equals("null") || selectedCategory.equals("")) {
			selectedCategory = "%";
		}
		if (selectedTag == null || selectedTag.equals("null") || selectedTag.equals("")) {
			selectedTag = "%";
		}

		return articleRepository.findAllByConditions(dateMap.get("startDate"),
		                                             dateMap.get("endDate"),
		                                             status,
		                                             beDelete,
		                                             selectedCategory,
		                                             selectedTag);
	}

	@Override
	public Article moveToRecycleBin(String USER_ROLE, Boolean beDelete, Article article) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) <= 1) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户角色等级没有将文章移动到回收站的权限！");
		}

		article.setBeDelete(beDelete);
		return articleRepository.save(article);
	}
}
