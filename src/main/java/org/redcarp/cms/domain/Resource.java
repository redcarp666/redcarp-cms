package org.redcarp.cms.domain;

import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Component
public class Resource extends _BaseEntity implements Serializable {

	@Id
	@Column(columnDefinition = "char(50)")
	private String location;

	@Column(nullable = false)
	private String originalFilename;

	@Column(nullable = false)
	private String storageFilename;

	@Column(nullable = false)
	private String fileType;

	@Column(nullable = false)
	private String fileSize;

	@Column(nullable = false)
	private Boolean beReference;

	public Resource() {
	}

	public Resource(Resource resource) {
		this.location = resource.location;
		this.originalFilename = resource.originalFilename;
		this.storageFilename = resource.storageFilename;
		this.fileType = resource.fileType;
		this.fileSize = resource.fileSize;
		this.beReference = resource.beReference;
		this.createdAt = resource.createdAt;
		this.updatedAt = resource.updatedAt;
	}

	public Resource(String location, String originalFilename, String storageFilename, String fileType, String fileSize, Boolean beReference) {
		this.location = location;
		this.originalFilename = originalFilename;
		this.storageFilename = storageFilename;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.beReference = beReference;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getStorageFilename() {
		return storageFilename;
	}

	public void setStorageFilename(String storageFilename) {
		this.storageFilename = storageFilename;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public Boolean getBeReference() {
		return beReference;
	}

	public void setBeReference(Boolean beReference) {
		this.beReference = beReference;
	}
}
