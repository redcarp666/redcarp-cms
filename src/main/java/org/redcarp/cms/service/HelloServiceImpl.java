package org.redcarp.cms.service;

import org.redcarp.cms.config.UserRoleFields;
import org.redcarp.cms.domain.User;
import org.redcarp.cms.domain.request.InitSystemInfo;
import org.redcarp.cms.domain.response.LoginUserInfo;
import org.redcarp.cms.repository.UserRepository;
import org.redcarp.cms.util.JwtToken;
import org.redcarp.cms.util.MD5Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class HelloServiceImpl implements HelloService {

	private final UserRepository userRepository;
	@Value("${custom.jwt-secret-key}")
	private String JWT_SECRET_KEY;

	@Autowired
	public HelloServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Boolean alreadyInitSystem() {
		return userRepository.findById("admin").orElse(null) != null;
	}

	@Override
	public LoginUserInfo initSystem(InitSystemInfo initSystemInfo) {
		if (initSystemInfo.getAdminPassword() == null || initSystemInfo.getAdminPassword().trim().equals("")) {
			return new LoginUserInfo(false, "密码不能为空！", null, null);
		}
		if (!Pattern.compile("^\\w{6,20}$").matcher(initSystemInfo.getAdminPassword()).matches()) {
			return new LoginUserInfo(false, "密码要求 6-20 位常规 ASCII 字符！", null, null);
		}

		User adminUser = userRepository.save(new User("admin",
		                                              MD5Encode.encode(initSystemInfo.getAdminPassword()),
		                                              "",
		                                              UserRoleFields.SUPER_ADMIN));
		return new LoginUserInfo(true,
		                         "超级管理员已创建！",
		                         JwtToken.createToken(adminUser, JWT_SECRET_KEY),
		                         new User(adminUser));
	}
}
