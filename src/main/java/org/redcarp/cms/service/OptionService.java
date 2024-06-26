package org.redcarp.cms.service;

import org.redcarp.cms.domain.Options;
import org.redcarp.cms.domain.options.*;

import java.util.List;

public interface OptionService {

	List<Options> getOptions();

	SiteTitle setSiteTitle(String USER_ROLE, SiteTitle siteTitle);

	CopyrightInfo setCopyrightInfo(String USER_ROLE, CopyrightInfo copyrightInfo);

	WebsiteFilingInfo setWebsiteFilingInfo(String USER_ROLE, WebsiteFilingInfo websiteFilingInfo);

	List<HomeCarouselImages> setHomeCarouselImages(String USER_ROLE, List<HomeCarouselImages> homeCarouselImages);

	List<FriendlyLinks> setFriendlyLinks(String USER_ROLE, List<FriendlyLinks> friendlyLinks);
}
