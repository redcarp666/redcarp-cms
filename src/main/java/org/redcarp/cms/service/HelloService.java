package org.redcarp.cms.service;

import org.redcarp.cms.domain.request.InitSystemInfo;
import org.redcarp.cms.domain.response.LoginUserInfo;

public interface HelloService {

	Boolean alreadyInitSystem();

	LoginUserInfo initSystem(InitSystemInfo initSystemInfo);
}
