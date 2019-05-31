package com.innovate.filseserver.model;

import java.io.Serializable;
import java.util.Date;

import com.easysoft.commons.annotation.LikeQuery;
import com.easysoft.commons.annotation.PrimaryKey;
import com.easysoft.commons.annotation.TableName;
import com.easysoft.commons.cons.Constants;

/**
 * 授权用户信息
* Title: GrantUser
* Description: 
* Company: easysoft.ltd 
* @author IvanHsu
* @date 2019年5月24日
 */
@SuppressWarnings("serial")
@PrimaryKey("id")
@TableName("t_upload_user")
public class UploadUser implements Serializable{

	private String id;
	@LikeQuery
	private String name;
	@LikeQuery
	private String loginName;
	private String password;
	private String status;
	private String authCode;
	private String comments;
	private Date updateTime;
	private Date createTime;
	private String salt;
	private String isSuper = Constants.ENUM_N;
	private String delFlag = Constants.STATUS_NORMAL;

	
	public UploadUser(String id) {
		super();
		this.id = id;
	}

	public UploadUser() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}


	public String getIsSuper() {
		return isSuper;
	}

	public void setIsSuper(String isSuper) {
		this.isSuper = isSuper;
	}

	public enum STATUS{
		NORMAL,//正常
		FREZZ//冻结
	}
}
