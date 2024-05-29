package org.redcarp.cms.repository;

import org.redcarp.cms.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

	@Query(value = "select new User(user) from User user order by user.createdAt desc")
	List<User> getAllNotIncludedPassword();
}
