package org.redcarp.cms.service;

import org.redcarp.cms.domain.Tag;

import java.util.List;

public interface TagService {

	List<Tag> getTags();

	List<Tag> createTag(Tag tag);

	List<Tag> deleteTag(String USER_ROLE, String name);
}
