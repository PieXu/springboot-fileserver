package com.innovate.filseserver.model;

import java.io.Serializable;
import java.util.Date;

import com.easysoft.commons.annotation.LikeQuery;
import com.easysoft.commons.annotation.PrimaryKey;
import com.easysoft.commons.annotation.TableName;
import com.easysoft.commons.annotation.VirtualColumn;

/**
 * 
 * Title: UploadFile Description: 附件信息 Company: easysoft.ltd
 * 
 * @author IvanHsu
 * @date 2019年5月28日
 */
@TableName("t_upload_file")
@PrimaryKey("id")
public class UploadFile implements Serializable {

	@VirtualColumn
	private static final long serialVersionUID = -8850091429138469762L;
	private String id;
	@LikeQuery
	private String name;
	private String type;
	@LikeQuery
	private String ext;
	private Long size;
	private String objectId;
	private String isTemp;
	private String remoteIp;
	private String comments;
	private Date updateTime;
	private Date createTime;
	private String grantUser;
	private String path;
	private String uploadUser;
	private String fullPath;
	private String delFlag = "0";
	
	public UploadFile() {
		super();
	}
	public UploadFile(String id) {
		super();
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getIsTemp() {
		return isTemp;
	}

	public void setIsTemp(String isTemp) {
		this.isTemp = isTemp;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getGrantUser() {
		return grantUser;
	}

	public void setGrantUser(String grantUser) {
		this.grantUser = grantUser;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

}
