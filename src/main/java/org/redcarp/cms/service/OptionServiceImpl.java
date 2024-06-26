package org.redcarp.cms.service;

import org.redcarp.cms.config.OptionsFields;
import org.redcarp.cms.config.UserRoleFields;
import org.redcarp.cms.domain.Options;
import org.redcarp.cms.domain.Resource;
import org.redcarp.cms.domain.options.*;
import org.redcarp.cms.exception.IllegalParameterException;
import org.redcarp.cms.exception.UserRolePermissionException;
import org.redcarp.cms.repository.OptionsRepository;
import org.redcarp.cms.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {

	private final OptionsRepository optionsRepository;

	private final ResourceRepository resourceRepository;

	@Autowired
	public OptionServiceImpl(OptionsRepository optionsRepository, ResourceRepository resourceRepository) {
		this.optionsRepository = optionsRepository;
		this.resourceRepository = resourceRepository;
	}

	@Override
	public List<Options> getOptions() {
		return optionsRepository.findAll();
	}

	@Override
	public SiteTitle setSiteTitle(String USER_ROLE, SiteTitle siteTitle) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) < 3) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户不能设置首页轮播图，请联系更高等级的管理员！");
		}

		Options option = optionsRepository.save(new Options(OptionsFields.SITE_TITLE,
		                                                    new _OptionValue<>(siteTitle),
		                                                    true));
		return (SiteTitle) option.getValue().getContent();
	}

	@Override
	public CopyrightInfo setCopyrightInfo(String USER_ROLE, CopyrightInfo copyrightInfo) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) < 3) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户不能设置首页轮播图，请联系更高等级的管理员！");
		}

		Options option = optionsRepository.save(new Options(OptionsFields.COPYRIGHT_INFO,
		                                                    new _OptionValue<>(copyrightInfo),
		                                                    true));
		return (CopyrightInfo) option.getValue().getContent();
	}

	@Override
	public WebsiteFilingInfo setWebsiteFilingInfo(String USER_ROLE, WebsiteFilingInfo websiteFilingInfo) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) < 3) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户不能设置首页轮播图，请联系更高等级的管理员！");
		}

		Options option = optionsRepository.save(new Options(OptionsFields.WEBSITE_FILING_INFO,
		                                                    new _OptionValue<>(websiteFilingInfo),
		                                                    true));
		return (WebsiteFilingInfo) option.getValue().getContent();
	}

	@Override
	public List<HomeCarouselImages> setHomeCarouselImages(String USER_ROLE, List<HomeCarouselImages> homeCarouselImages) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) < 2) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户不能设置首页轮播图，请联系更高等级的管理员！");
		}

		// 标注资源为引用状态
		for (HomeCarouselImages homeCarouselImage : homeCarouselImages) {
			Resource oldImageResource = resourceRepository.findById(homeCarouselImage.getImageLocation()).orElse(null);
			if (oldImageResource == null) {
				throw new IllegalParameterException("【非法参数异常】- 访问服务器不存在的资源！");
			}
			oldImageResource.setBeReference(true);
			resourceRepository.save(oldImageResource);
		}

		// 不再使用的轮播图资源更新引用状态为 false
		Options carouselImageOption = optionsRepository.findById(OptionsFields.HOME_CAROUSEL_IMAGES).orElse(null);
		if (carouselImageOption != null) {
			for (HomeCarouselImages tempCarouselImage : (List<HomeCarouselImages>) carouselImageOption.getValue().getContent()) {
				List<String> stringHomeCarouselImages = new ArrayList<>();
				for (HomeCarouselImages homeCarouselImage : homeCarouselImages) {
					stringHomeCarouselImages.add(homeCarouselImage.getImageLocation());
				}
				if (!stringHomeCarouselImages.contains(tempCarouselImage.getImageLocation())) {
					Resource oldImageResource = resourceRepository.findById(tempCarouselImage.getImageLocation()).orElse(
							null);
					if (oldImageResource == null) {
						throw new IllegalParameterException("【非法参数异常】- 访问服务器不存在的资源！");
					}
					oldImageResource.setBeReference(false);
					resourceRepository.save(oldImageResource);
				}
			}
		}

		Options option = optionsRepository.save(new Options(OptionsFields.HOME_CAROUSEL_IMAGES,
		                                                    new _OptionValue<>(homeCarouselImages),
		                                                    true));
		return (List<HomeCarouselImages>) option.getValue().getContent();
	}

	@Override
	public List<FriendlyLinks> setFriendlyLinks(String USER_ROLE, List<FriendlyLinks> friendlyLinks) {
		// 验证用户角色权限
		if (UserRoleFields.getUserRoleLevel(USER_ROLE) < 2) {
			throw new UserRolePermissionException("【用户角色权限异常】- 当前用户不能设置站点友情链接，请联系更高等级的管理员！");
		}

		Options option = optionsRepository.save(new Options(OptionsFields.FRIENDLY_LINKS,
		                                                    new _OptionValue<>(friendlyLinks),
		                                                    true));
		return (List<FriendlyLinks>) option.getValue().getContent();
	}
}
